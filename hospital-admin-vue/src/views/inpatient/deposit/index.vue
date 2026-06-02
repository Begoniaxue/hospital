<template>
  <div class="page-container">
    <div class="page-header">
      <h3>押金管理</h3>
      <div>
        <el-button type="primary" @click="handlePay" v-if="hasPermission('inpatient:deposit:pay')">
          <el-icon><Plus /></el-icon>
          押金缴费
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
          <el-option label="已缴费" :value="1" />
          <el-option label="已退款" :value="2" />
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
      <el-table-column prop="depositNo" label="押金单号" width="180" />
      <el-table-column prop="inpatientNo" label="住院号" width="140" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="amount" label="押金金额" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.amount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="payMethod" label="支付方式" width="100" />
      <el-table-column prop="payTime" label="支付时间" width="180" />
      <el-table-column prop="operatorName" label="操作人" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '已缴费' : '已退款' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="refundTime" label="退款时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              link
              type="warning"
              @click="handleRefund(scope.row)"
              v-if="scope.row.status === 1 && hasPermission('inpatient:deposit:refund')"
            >
              退款
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

  <el-dialog v-model="payDialogVisible" title="押金缴费" width="500px">
    <el-form ref="payFormRef" :model="payForm" :rules="payRules" label-width="100px">
      <el-form-item label="住院号" prop="inpatientId">
        <el-select v-model="payForm.inpatientId" placeholder="请选择住院患者" style="width: 100%;" filterable>
          <el-option
            v-for="inpatient in inpatients"
            :key="inpatient.id"
            :label="inpatient.inpatientNo + ' - ' + inpatient.patientName"
            :value="inpatient.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="缴费金额" prop="amount">
        <el-input-number v-model="payForm.amount" :min="0" :precision="2" style="width: 100%;" />
      </el-form-item>
      <el-form-item label="支付方式" prop="payMethod">
        <el-select v-model="payForm.payMethod" placeholder="请选择" style="width: 100%;">
          <el-option label="现金" value="现金" />
          <el-option label="微信" value="微信" />
          <el-option label="支付宝" value="支付宝" />
          <el-option label="银行卡" value="银行卡" />
          <el-option label="医保" value="医保" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="payForm.remark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="payDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handlePaySubmit">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="refundDialogVisible" title="押金退款" width="500px">
    <el-form ref="refundFormRef" :model="refundForm" :rules="refundRules" label-width="100px">
      <el-form-item label="押金单号">
        <el-input v-model="refundForm.depositNo" disabled />
      </el-form-item>
      <el-form-item label="退款金额">
        <el-input-number v-model="refundForm.amount" :min="0" :precision="2" style="width: 100%;" disabled />
      </el-form-item>
      <el-form-item label="退款原因" prop="remark">
        <el-input v-model="refundForm.remark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="refundDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleRefundSubmit">确定退款</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getDepositPage,
  payDeposit,
  refundDeposit
} from '@/api/inpatient'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  patientName: '',
  inpatientNo: '',
  status: null
})

const tableData = ref([])
const total = ref(0)
const payDialogVisible = ref(false)
const refundDialogVisible = ref(false)
const payFormRef = ref(null)
const refundFormRef = ref(null)
const inpatients = ref([])

const payForm = reactive({
  inpatientId: null,
  amount: null,
  payMethod: '',
  remark: ''
})

const refundForm = reactive({
  id: null,
  depositNo: '',
  amount: null,
  remark: ''
})

const payRules = {
  inpatientId: [{ required: true, message: '请选择住院患者', trigger: 'change' }],
  amount: [{ required: true, message: '请输入缴费金额', trigger: 'blur' }],
  payMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
}

const refundRules = {
  remark: [{ required: true, message: '请输入退款原因', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const handleQuery = async () => {
  const params = { ...queryForm }
  const res = await getDepositPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.patientName = ''
  queryForm.inpatientNo = ''
  queryForm.status = null
  queryForm.pageNum = 1
  handleQuery()
}

const handlePay = () => {
  inpatients.value = []
  Object.assign(payForm, {
    inpatientId: null,
    amount: null,
    payMethod: '',
    remark: ''
  })
  payDialogVisible.value = true
}

const handleRefund = (row) => {
  Object.assign(refundForm, {
    id: row.id,
    depositNo: row.depositNo,
    amount: row.amount,
    remark: ''
  })
  refundDialogVisible.value = true
}

const handlePaySubmit = async () => {
  if (!payFormRef.value) return
  await payFormRef.value.validate(async (valid) => {
    if (valid) {
      await payDeposit(payForm)
      ElMessage.success('缴费成功')
      payDialogVisible.value = false
      handleQuery()
    }
  })
}

const handleRefundSubmit = async () => {
  if (!refundFormRef.value) return
  await refundFormRef.value.validate(async (valid) => {
    if (valid) {
      ElMessageBox.confirm('确定要退款吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await refundDeposit(refundForm)
        ElMessage.success('退款成功')
        refundDialogVisible.value = false
        handleQuery()
      })
    }
  })
}

onMounted(() => {
  handleQuery()
})
</script>
