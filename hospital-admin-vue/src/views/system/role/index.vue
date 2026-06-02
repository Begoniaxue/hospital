<template>
  <div class="page-container">
    <div class="page-header">
      <h3>角色管理</h3>
      <el-button type="primary" @click="handleAdd" v-if="hasPermission('system:role:add')">
        <el-icon><Plus /></el-icon>
        新增角色
      </el-button>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="queryForm.keyword" placeholder="角色名称/编码" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleCode" label="角色编码" width="150" />
      <el-table-column prop="description" label="角色描述" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('system:role:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handlePermission(scope.row)"
              v-if="hasPermission('system:role:permission')"
            >
              分配权限
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-if="hasPermission('system:role:delete')"
            >
              删除
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="queryForm.pageNum"
      v-model:page-size="queryForm.pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleQuery"
      @current-change="handleQuery"
      style="margin-top: 20px; justify-content: flex-end;"
    />
  </div>

  <el-dialog v-model="dialogVisible" title="角色信息" width="500px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="form.roleName" />
      </el-form-item>
      <el-form-item label="角色编码" prop="roleCode">
        <el-input v-model="form.roleCode" />
      </el-form-item>
      <el-form-item label="角色描述" prop="description">
        <el-input v-model="form.description" type="textarea" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="form.sort" :min="0" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="permissionDialogVisible" title="分配权限" width="600px">
    <el-tree
      ref="treeRef"
      :data="permissionTree"
      :props="{ label: 'name', children: 'children' }"
      show-checkbox
      node-key="id"
      :default-checked-keys="checkedPermissions"
    />
    <template #footer>
      <el-button @click="permissionDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleAssignPermission">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getRolePage,
  addRole,
  updateRole,
  deleteRole,
  getRole,
  assignPermissions
} from '@/api/role'
import { getPermissionTree } from '@/api/permission'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const formRef = ref(null)
const treeRef = ref(null)
const permissionTree = ref([])
const currentRoleId = ref(null)
const checkedPermissions = ref([])

const form = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: '',
  sort: 0
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const handleQuery = async () => {
  const res = await getRolePage(queryForm)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    roleName: '',
    roleCode: '',
    description: '',
    sort: 0
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  const res = await getRole(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateRole({ role: form, permissionIds: [] })
        ElMessage.success('修改成功')
      } else {
        await addRole({ role: form, permissionIds: [] })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handlePermission = async (row) => {
  currentRoleId.value = row.id
  const res = await getRole(row.id)
  checkedPermissions.value = res.data.permissionIds || []
  permissionDialogVisible.value = true
}

const handleAssignPermission = async () => {
  if (!treeRef.value) return
  const checkedKeys = treeRef.value.getCheckedKeys()
  const halfCheckedKeys = treeRef.value.getHalfCheckedKeys()
  const allKeys = [...checkedKeys, ...halfCheckedKeys]
  await assignPermissions(currentRoleId.value, allKeys)
  ElMessage.success('权限分配成功')
  permissionDialogVisible.value = false
}

onMounted(async () => {
  handleQuery()
  const res = await getPermissionTree()
  permissionTree.value = res.data
})
</script>
