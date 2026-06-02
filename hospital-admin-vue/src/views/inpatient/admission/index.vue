<template>
  <div class="page-container">
    <div class="page-header">
      <h3>入院登记</h3>
      <div>
        <el-button type="primary" @click="handleAdd" v-if="hasPermission('inpatient:admission:add')">
          <el-icon><Plus /></el-icon>
          新增入院
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="患者姓名">
        <el-input v-model="queryForm.patientName" placeholder="请输入患者姓名" clearable />
      </el-form-item>
      <el-form-item label="住院号">
        <el-input v-model="queryForm.inpatientNo" placeholder="请输入住院号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择" clearable>
          <el-option label="待入院" :value="0" />
          <el-option label="已入院" :value="1" />
          <el-option label="已出院" :value="2" />
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

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="inpatientNo" label="住院号" width="140" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="patientGender" label="性别" width="80">
        <template #default="scope">
          {{ scope.row.patientGender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="patientAge" label="年龄" width="80" />
      <el-table-column prop="department" label="科室" width="120" />
      <el-table-column prop="doctorName" label="主治医生" width="100" />
      <el-table-column prop="bedNo" label="床位号" width="100" />
      <el-table-column prop="wardName" label="病房" width="120" />
      <el-table-column prop="diagnosis" label="诊断" show-overflow-tooltip />
      <el-table-column prop="admissionDate" label="入院日期" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              link
              type="primary"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              size="small"
              link
              type="primary"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('inpatient:admission:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              link
              type="danger"
              @click="handleDelete(scope.row)"
              v-if="hasPermission('inpatient:admission:delete')"
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
          <el-form-item label="患者姓名" prop="patientName">
            <el-input v-model="form.patientName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别">
            <el-radio-group v-model="form.patientGender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="年龄">
            <el-input-number v-model="form.patientAge" :min="0" :max="150" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="入院日期" prop="admissionDate">
            <el-date-picker v-model="form.admissionDate" type="datetime" placeholder="请选择" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%;" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="科室" prop="department">
            <el-select v-model="form.department" placeholder="请选择" style="width: 100%;">
              <el-option label="内科" value="内科" />
              <el-option label="外科" value="外科" />
              <el-option label="儿科" value="儿科" />
              <el-option label="妇产科" value="妇产科" />
              <el-option label="骨科" value="骨科" />
              <el-option label="神经内科" value="神经内科" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="主治医生">
            <el-input v-model="form.doctorName" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="病房">
            <el-select v-model="form.wardName" placeholder="请选择" style="width: 100%;">
              <el-option label="内科病房" value="内科病房" />
              <el-option label="外科病房" value="外科病房" />
              <el-option label="儿科病房" value="儿科病房" />
              <el-option label="ICU病房" value="ICU病房" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="床位号">
            <el-input v-model="form.bedNo" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="入院类型">
            <el-select v-model="form.admissionType" placeholder="请选择" style="width: 100%;">
              <el-option label="普通入院" value="普通入院" />
              <el-option label="急诊入院" value="急诊入院" />
              <el-option label="转科入院" value="转科入院" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预计天数">
            <el-input-number v-model="form.expectedDays" :min="1" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="诊断" prop="diagnosis">
        <el-input v-model="form.diagnosis" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="0">待入院</el-radio>
          <el-radio :value="1">已入院</el-radio>
          <el-radio :value="2">已出院</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" />
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
  getInpatientPage,
  getInpatient,
  addInpatient,
  updateInpatient,
  deleteInpatient
} from '@/api/inpatient'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  patientName: '',
  inpatientNo: '',
  status: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isView = ref(false)
const dialogTitle = ref('')

const form = reactive({
  id: null,
  patientName: '',
  patientGender: 1,
  patientAge: null,
  admissionDate: '',
  department: '',
  doctorName: '',
  wardName: '',
  bedNo: '',
  admissionType: '',
  expectedDays: 7,
  diagnosis: '',
  status: 0,
  remark: ''
})

const rules = {
  patientName: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
  admissionDate: [{ required: true, message: '请选择入院日期', trigger: 'change' }],
  department: [{ required: true, message: '请选择科室', trigger: 'change' }],
  diagnosis: [{ required: true, message: '请输入诊断', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const getStatusType = (status) => {
  const types = ['info', 'success', 'warning']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待入院', '已入院', '已出院']
  return texts[status] || '未知'
}

const handleQuery = async () => {
  const params = { ...queryForm }
  const res = await getInpatientPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.patientName = ''
  queryForm.inpatientNo = ''
  queryForm.status = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增入院'
  Object.assign(form, {
    id: null,
    patientName: '',
    patientGender: 1,
    patientAge: null,
    admissionDate: '',
    department: '',
    doctorName: '',
    wardName: '',
    bedNo: '',
    admissionType: '',
    expectedDays: 7,
    diagnosis: '',
    status: 0,
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '入院详情'
  const res = await getInpatient(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isView.value = false
  dialogTitle.value = '编辑入院'
  const res = await getInpatient(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateInpatient(form)
        ElMessage.success('修改成功')
      } else {
        await addInpatient(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该入院记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteInpatient(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

onMounted(() => {
  handleQuery()
})
</script>
