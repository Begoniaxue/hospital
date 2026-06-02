<template>
  <div class="page-container">
    <div class="page-header">
      <h3>病程记录</h3>
      <div>
        <el-button type="primary" @click="handleAdd" v-if="hasPermission('inpatient:record:add')">
          <el-icon><Plus /></el-icon>
          新增记录
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
      <el-form-item label="记录类型">
        <el-select v-model="queryForm.recordType" placeholder="请选择" clearable>
          <el-option label="入院记录" value="入院记录" />
          <el-option label="首次病程" value="首次病程" />
          <el-option label="日常病程" value="日常病程" />
          <el-option label="上级医师查房" value="上级医师查房" />
          <el-option label="会诊记录" value="会诊记录" />
          <el-option label="出院记录" value="出院记录" />
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
      <el-table-column prop="recordDate" label="记录日期" width="180" />
      <el-table-column prop="recordType" label="记录类型" width="120" />
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column prop="doctorName" label="医生" width="100" />
      <el-table-column prop="vitalSigns" label="生命体征" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
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
              v-if="hasPermission('inpatient:record:edit')"
            >
              编辑
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

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" :disabled="isView">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="住院号" prop="inpatientId">
            <el-select v-model="form.inpatientId" placeholder="请选择住院患者" style="width: 100%;" filterable>
              <el-option
                v-for="inpatient in inpatients"
                :key="inpatient.id"
                :label="inpatient.inpatientNo + ' - ' + inpatient.patientName"
                :value="inpatient.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="记录类型" prop="recordType">
            <el-select v-model="form.recordType" placeholder="请选择" style="width: 100%;">
              <el-option label="入院记录" value="入院记录" />
              <el-option label="首次病程" value="首次病程" />
              <el-option label="日常病程" value="日常病程" />
              <el-option label="上级医师查房" value="上级医师查房" />
              <el-option label="会诊记录" value="会诊记录" />
              <el-option label="出院记录" value="出院记录" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="记录日期" prop="recordDate">
            <el-date-picker v-model="form.recordDate" type="datetime" placeholder="请选择" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="生命体征">
        <el-input v-model="form.vitalSigns" placeholder="体温、脉搏、呼吸、血压等" />
      </el-form-item>
      <el-form-item label="记录内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="8" />
      </el-form-item>
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
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getRecordPage,
  getRecord,
  addRecord,
  updateRecord
} from '@/api/inpatient'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  patientName: '',
  inpatientNo: '',
  recordType: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isView = ref(false)
const dialogTitle = ref('')
const inpatients = ref([])

const form = reactive({
  id: null,
  inpatientId: null,
  recordType: '',
  recordDate: '',
  title: '',
  content: '',
  vitalSigns: '',
  remark: ''
})

const rules = {
  inpatientId: [{ required: true, message: '请选择住院患者', trigger: 'change' }],
  recordType: [{ required: true, message: '请选择记录类型', trigger: 'change' }],
  recordDate: [{ required: true, message: '请选择记录日期', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入记录内容', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const handleQuery = async () => {
  const params = { ...queryForm }
  const res = await getRecordPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.patientName = ''
  queryForm.inpatientNo = ''
  queryForm.recordType = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增病程记录'
  inpatients.value = []
  Object.assign(form, {
    id: null,
    inpatientId: null,
    recordType: '',
    recordDate: '',
    title: '',
    content: '',
    vitalSigns: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '病程记录详情'
  const res = await getRecord(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isView.value = false
  dialogTitle.value = '编辑病程记录'
  const res = await getRecord(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateRecord(form)
        ElMessage.success('修改成功')
      } else {
        await addRecord(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  handleQuery()
})
</script>
