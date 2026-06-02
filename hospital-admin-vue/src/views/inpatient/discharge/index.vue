<template>
  <div class="page-container">
    <div class="page-header">
      <h3>出院结算</h3>
      <div>
        <el-button type="primary" @click="handleSettle" v-if="hasPermission('inpatient:discharge:settle')">
          <el-icon><Plus /></el-icon>
          出院结算
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
          <el-option label="待结算" :value="0" />
          <el-option label="已结算" :value="1" />
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
      <el-table-column prop="dischargeNo" label="结算单号" width="180" />
      <el-table-column prop="inpatientNo" label="住院号" width="140" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="admissionDate" label="入院日期" width="180" />
      <el-table-column prop="dischargeDate" label="出院日期" width="180" />
      <el-table-column prop="actualDays" label="住院天数" width="100" />
      <el-table-column prop="totalAmount" label="总费用" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.totalAmount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="depositAmount" label="押金抵扣" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.depositAmount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="actualPay" label="实付金额" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.actualPay).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
            {{ scope.row.status === 1 ? '已结算' : '待结算' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operatorName" label="操作人" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              link
              type="primary"
              @click="handleView(scope.row)"
            >
              详情
            </el-button>
            <el-button
              size="small"
              link
              type="primary"
              @click="handlePrint(scope.row)"
              v-if="scope.row.status === 1 && hasPermission('inpatient:discharge:print')"
            >
              打印
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
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" :disabled="isView">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="住院号" prop="inpatientId">
            <el-select v-model="form.inpatientId" placeholder="请选择住院患者" style="width: 100%;" filterable @change="handleInpatientChange">
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
          <el-form-item label="出院日期" prop="dischargeDate">
            <el-date-picker v-model="form.dischargeDate" type="datetime" placeholder="请选择" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%;" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-divider>费用明细</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="总费用">
            <el-input :value="'¥' + Number(form.totalAmount).toFixed(2)" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="押金抵扣">
            <el-input :value="'¥' + Number(form.depositAmount).toFixed(2)" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="应退金额">
            <el-input :value="'¥' + Number(form.refundAmount).toFixed(2)" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="应补缴金额">
            <el-input :value="'¥' + Number(form.supplementAmount).toFixed(2)" disabled />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="实付金额">
        <el-input :value="'¥' + Number(form.actualPay).toFixed(2)" disabled />
      </el-form-item>
      <el-form-item label="支付方式" prop="payMethod" v-if="!isView">
        <el-select v-model="form.payMethod" placeholder="请选择" style="width: 100%;">
          <el-option label="现金" value="现金" />
          <el-option label="微信" value="微信" />
          <el-option label="支付宝" value="支付宝" />
          <el-option label="银行卡" value="银行卡" />
          <el-option label="医保" value="医保" />
        </el-select>
      </el-form-item>
      <el-form-item label="出院诊断">
        <el-input v-model="form.dischargeDiagnosis" type="textarea" :rows="2" :disabled="isView" />
      </el-form-item>
      <el-form-item label="出院结果">
        <el-select v-model="form.dischargeResult" placeholder="请选择" style="width: 100%;" :disabled="isView">
          <el-option label="治愈" value="治愈" />
          <el-option label="好转" value="好转" />
          <el-option label="未愈" value="未愈" />
          <el-option label="死亡" value="死亡" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" :disabled="isView" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleSubmit" v-if="!isView">确认结算</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getDischargePage,
  getDischarge,
  settleDischarge,
  printDischarge
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
const inpatients = ref([])

const form = reactive({
  id: null,
  inpatientId: null,
  dischargeDate: '',
  totalAmount: 0,
  depositAmount: 0,
  refundAmount: 0,
  supplementAmount: 0,
  actualPay: 0,
  payMethod: '',
  dischargeDiagnosis: '',
  dischargeResult: '',
  remark: ''
})

const rules = {
  inpatientId: [{ required: true, message: '请选择住院患者', trigger: 'change' }],
  dischargeDate: [{ required: true, message: '请选择出院日期', trigger: 'change' }],
  payMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const handleQuery = async () => {
  const params = { ...queryForm }
  const res = await getDischargePage(params)
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

const handleSettle = () => {
  isView.value = false
  dialogTitle.value = '出院结算'
  inpatients.value = []
  Object.assign(form, {
    id: null,
    inpatientId: null,
    dischargeDate: '',
    totalAmount: 0,
    depositAmount: 0,
    refundAmount: 0,
    supplementAmount: 0,
    actualPay: 0,
    payMethod: '',
    dischargeDiagnosis: '',
    dischargeResult: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '结算详情'
  const res = await getDischarge(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handlePrint = async (row) => {
  const res = await printDischarge(row.id)
  const blob = new Blob([res.data], { type: 'application/pdf' })
  const url = URL.createObjectURL(blob)
  const printWindow = window.open(url)
  printWindow.onload = () => {
    printWindow.print()
  }
}

const handleInpatientChange = (val) => {
  if (val) {
    form.totalAmount = 5000
    form.depositAmount = 3000
    form.supplementAmount = Math.max(0, form.totalAmount - form.depositAmount)
    form.refundAmount = Math.max(0, form.depositAmount - form.totalAmount)
    form.actualPay = form.supplementAmount
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await settleDischarge(form)
      ElMessage.success('结算成功')
      dialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  handleQuery()
})
</script>
