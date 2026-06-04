<template>
  <div class="page-container">
    <div class="page-header">
      <h3>医生管理</h3>
      <div>
        <el-button type="primary" @click="handleAdd" v-if="hasPermission('outpatient:doctor:add')">
          <el-icon><Plus /></el-icon>
          新增医生
        </el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0" v-if="hasPermission('outpatient:doctor:delete')">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="医生姓名">
        <el-input v-model="queryForm.name" placeholder="请输入医生姓名" clearable />
      </el-form-item>
      <el-form-item label="工号">
        <el-input v-model="queryForm.jobNo" placeholder="请输入工号" clearable />
      </el-form-item>
      <el-form-item label="所属科室">
        <el-select v-model="queryForm.departmentId" placeholder="请选择科室" clearable>
          <el-option
            v-for="item in departmentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
          <el-option label="在职" :value="1" />
          <el-option label="离职" :value="0" />
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

    <el-table :data="tableData" border @selection-change="handleSelectionChange" style="width: 100%;">
      <el-table-column type="selection" width="55" />
      <el-table-column prop="jobNo" label="工号" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="scope">
          {{ scope.row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="departmentName" label="所属科室" width="150" />
      <el-table-column prop="title" label="职称" width="120" />
      <el-table-column prop="specialty" label="专业特长" show-overflow-tooltip />
      <el-table-column prop="registrationFee" label="挂号费(元)" width="120">
        <template #default="scope">
          ¥{{ scope.row.registrationFee }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              size="small"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('outpatient:doctor:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-if="hasPermission('outpatient:doctor:delete')"
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

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" :disabled="isView">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工号" prop="jobNo">
            <el-input v-model="form.jobNo" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="form.gender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="0">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属科室" prop="departmentId">
            <el-select v-model="form.departmentId" placeholder="请选择科室">
          <el-option
            v-for="item in departmentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职称" prop="title">
            <el-select v-model="form.title" placeholder="请选择职称">
              <el-option label="主任医师" value="主任医师" />
              <el-option label="副主任医师" value="副主任医师" />
              <el-option label="主治医师" value="主治医师" />
              <el-option label="住院医师" value="住院医师" />
              <el-option label="医师" value="医师" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="挂号费" prop="registrationFee">
            <el-input-number v-model="form.registrationFee" :min="0" :precision="2" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="专业特长" prop="specialty">
        <el-input v-model="form.specialty" type="textarea" :rows="2" />
      </el-form-item>
      <el-form-item label="个人简介" prop="introduction">
        <el-input v-model="form.introduction" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">在职</el-radio>
          <el-radio :value="0">离职</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleSubmit" v-if="!isView">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getDoctorPage,
  getDoctorList,
  getDoctor,
  addDoctor,
  updateDoctor,
  deleteDoctor,
  batchDeleteDoctor
} from '@/api/doctor'
import { getDepartmentList } from '@/api/department'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  jobNo: '',
  departmentId: null,
  status: null
})

const tableData = ref([])
const departmentList = ref([])
const total = ref(0)
const selectedIds = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const isView = ref(false)
const dialogTitle = ref('')

const form = reactive({
  id: null,
  jobNo: '',
  name: '',
  gender: 1,
  departmentId: null,
  title: '',
  specialty: '',
  introduction: '',
  registrationFee: 0,
  phone: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  jobNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择科室', trigger: 'change' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const loadDepartmentList = async () => {
  const res = await getDepartmentList({ status: 1 })
  departmentList.value = res.data || []
}

const handleQuery = async () => {
  const res = await getDoctorPage(queryForm)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.jobNo = ''
  queryForm.departmentId = null
  queryForm.status = null
  queryForm.pageNum = 1
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增医生'
  Object.assign(form, {
    id: null,
    jobNo: '',
    name: '',
    gender: 1,
    departmentId: null,
    title: '',
    specialty: '',
    introduction: '',
    registrationFee: 0,
    phone: '',
    status: 1
  })
  loadDepartmentList()
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '医生详情'
  const res = await getDoctor(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isView.value = false
  dialogTitle.value = '编辑医生'
  const res = await getDoctor(row.id)
  Object.assign(form, res.data)
  loadDepartmentList()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateDoctor(form)
        ElMessage.success('修改成功')
      } else {
        await addDoctor(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该医生吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDoctor(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个医生吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await batchDeleteDoctor(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

onMounted(() => {
  loadDepartmentList()
  handleQuery()
})
</script>
