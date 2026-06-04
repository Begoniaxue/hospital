<template>
  <div class="page-container">
    <div class="page-header">
      <h3>科室管理</h3>
      <div>
        <el-button type="primary" @click="handleAdd" v-if="hasPermission('outpatient:department:add')">
          <el-icon><Plus /></el-icon>
          新增科室
        </el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0" v-if="hasPermission('outpatient:department:delete')">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="科室名称">
        <el-input v-model="queryForm.name" placeholder="请输入科室名称" clearable />
      </el-form-item>
      <el-form-item label="科室编码">
        <el-input v-model="queryForm.code" placeholder="请输入科室编码" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
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

    <el-table
      :data="tableData"
      border
      row-key="id"
      default-expand-all
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      @selection-change="handleSelectionChange"
      style="width: 100%;"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="科室名称" />
      <el-table-column prop="code" label="科室编码" width="150" />
      <el-table-column prop="type" label="科室类型" width="120">
        <template #default="scope">
          {{ scope.row.type === 1 ? '一级科室' : '二级科室' }}
        </template>
      </el-table-column>
      <el-table-column prop="parentName" label="上级科室" width="150" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('outpatient:department:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-if="hasPermission('outpatient:department:delete')"
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

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="上级科室" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="departmentTree"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          check-strictly
          placeholder="请选择上级科室（不选为一级科室）"
          clearable
        />
      </el-form-item>
      <el-form-item label="科室名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入科室名称" />
      </el-form-item>
      <el-form-item label="科室编码" prop="code">
        <el-input v-model="form.code" placeholder="请输入科室编码" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="form.sort" :min="0" :max="999" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="科室描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入科室描述" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getDepartmentPage,
  getDepartmentList,
  getDepartment,
  addDepartment,
  updateDepartment,
  deleteDepartment,
  batchDeleteDepartment
} from '@/api/department'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  code: '',
  status: null
})

const tableData = ref([])
const departmentTree = ref([])
const total = ref(0)
const selectedIds = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const dialogTitle = ref('')

const form = reactive({
  id: null,
  parentId: null,
  name: '',
  code: '',
  sort: 0,
  status: 1,
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入科室名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入科室编码', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const loadDepartmentTree = async () => {
  const res = await getDepartmentList({ status: 1 })
  departmentTree.value = res.data || []
}

const handleQuery = async () => {
  const res = await getDepartmentPage(queryForm)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.code = ''
  queryForm.status = null
  queryForm.pageNum = 1
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  dialogTitle.value = '新增科室'
  Object.assign(form, {
    id: null,
    parentId: null,
    name: '',
    code: '',
    sort: 0,
    status: 1,
    description: ''
  })
  loadDepartmentTree()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑科室'
  const res = await getDepartment(row.id)
  Object.assign(form, res.data)
  loadDepartmentTree()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateDepartment(form)
        ElMessage.success('修改成功')
      } else {
        await addDepartment(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该科室吗？删除后下级科室也将被删除。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个科室吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await batchDeleteDepartment(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

onMounted(() => {
  handleQuery()
})
</script>
