<template>
  <div class="page-container">
    <div class="page-header">
      <h3>患者管理</h3>
      <el-button type="primary" @click="handleAdd" v-if="hasPermission('patient:list:add')">
        <el-icon><Plus /></el-icon>
        新增患者
      </el-button>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="queryForm.keyword" placeholder="姓名/手机号/身份证/病历号" clearable />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
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
      <el-table-column prop="patientNo" label="病历号" width="140" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="scope">
          {{ scope.row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" width="80" />
      <el-table-column prop="idCard" label="身份证号" width="180" />
      <el-table-column prop="phone" label="联系方式" width="130" />
      <el-table-column prop="medicalHistory" label="既往病史" show-overflow-tooltip />
      <el-table-column prop="emergencyContact" label="紧急联系人" width="120" />
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
              v-if="hasPermission('patient:list:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-if="hasPermission('patient:list:delete')"
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
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="form.gender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="0">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="年龄" prop="age">
            <el-input-number v-model="form.age" :min="0" :max="150" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="联系方式" prop="phone">
            <el-input v-model="form.phone" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="地址">
            <el-input v-model="form.address" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="既往病史" prop="medicalHistory">
        <el-input v-model="form.medicalHistory" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item label="过敏史" prop="allergyHistory">
        <el-input v-model="form.allergyHistory" type="textarea" :rows="2" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="紧急联系人" prop="emergencyContact">
            <el-input v-model="form.emergencyContact" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="紧急联系电话" prop="emergencyPhone">
            <el-input v-model="form.emergencyPhone" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" />
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
  getPatientPage,
  addPatient,
  updatePatient,
  deletePatient,
  getPatient
} from '@/api/patient'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: ''
})

const dateRange = ref([])
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isView = ref(false)
const dialogTitle = ref('')

const form = reactive({
  id: null,
  patientNo: '',
  name: '',
  gender: 1,
  age: null,
  idCard: '',
  phone: '',
  address: '',
  medicalHistory: '',
  allergyHistory: '',
  emergencyContact: '',
  emergencyPhone: '',
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const handleQuery = async () => {
  const params = { ...queryForm }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  }
  const res = await getPatientPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.pageNum = 1
  dateRange.value = []
  handleQuery()
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增患者'
  Object.assign(form, {
    id: null,
    patientNo: '',
    name: '',
    gender: 1,
    age: null,
    idCard: '',
    phone: '',
    address: '',
    medicalHistory: '',
    allergyHistory: '',
    emergencyContact: '',
    emergencyPhone: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '患者详情'
  const res = await getPatient(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isView.value = false
  dialogTitle.value = '编辑患者'
  const res = await getPatient(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updatePatient(form)
        ElMessage.success('修改成功')
      } else {
        await addPatient(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该患者吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deletePatient(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

onMounted(() => {
  handleQuery()
})
</script>
