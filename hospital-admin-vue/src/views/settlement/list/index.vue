<template>
  <div class="page-container">
    <div class="page-header">
      <h3>收费结算</h3>
      <el-button type="primary" @click="handleCharge" v-if="hasPermission('settlement:list:charge')">
        <el-icon><Plus /></el-icon>
        收费结算
      </el-button>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="queryForm.keyword" placeholder="结算单号/患者姓名/发票号" clearable />
      </el-form-item>
      <el-form-item label="支付方式">
        <el-select v-model="queryForm.payMethod" placeholder="全部" clearable>
          <el-option label="现金" :value="1" />
          <el-option label="微信" :value="2" />
          <el-option label="支付宝" :value="3" />
          <el-option label="医保" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="全部" clearable>
          <el-option label="未收费" :value="0" />
          <el-option label="已收费" :value="1" />
          <el-option label="已退费" :value="2" />
          <el-option label="部分退费" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="结算时间">
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
      <el-table-column prop="settlementNo" label="结算单号" width="160" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="registrationFee" label="挂号费" width="100" align="right">
        <template #default="scope">
          {{ scope.row.registrationFee?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="drugFee" label="药品费" width="100" align="right">
        <template #default="scope">
          {{ scope.row.drugFee?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="examFee" label="检查费" width="100" align="right">
        <template #default="scope">
          {{ scope.row.examFee?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="otherFee" label="其他费" width="100" align="right">
        <template #default="scope">
          {{ scope.row.otherFee?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="总金额" width="110" align="right">
        <template #default="scope">
          <span style="font-weight: bold;">{{ scope.row.totalAmount?.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="discountAmount" label="优惠金额" width="100" align="right">
        <template #default="scope">
          {{ scope.row.discountAmount?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="actualAmount" label="实收金额" width="110" align="right">
        <template #default="scope">
          <span style="color: #f56c6c;">{{ scope.row.actualAmount?.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="payMethod" label="支付方式" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.payMethod === 1">现金</el-tag>
          <el-tag v-else-if="scope.row.payMethod === 2" type="success">微信</el-tag>
          <el-tag v-else-if="scope.row.payMethod === 3" type="primary">支付宝</el-tag>
          <el-tag v-else-if="scope.row.payMethod === 4" type="warning">医保</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="info">未收费</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">已收费</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="danger">已退费</el-tag>
          <el-tag v-else-if="scope.row.status === 3" type="warning">部分退费</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="settleTime" label="结算时间" width="180" />
      <el-table-column prop="operatorName" label="操作员" width="100" />
      <el-table-column label="操作" width="240" fixed="right">
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
              v-if="hasPermission('settlement:list:print') && scope.row.status === 1"
            >
              打印
            </el-button>
            <el-button
              size="small"
              link
              type="danger"
              @click="handleRefund(scope.row)"
              v-if="hasPermission('settlement:refund:apply') && scope.row.status === 1"
            >
              退费
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

  <el-dialog v-model="detailVisible" title="结算详情" width="800px">
    <el-descriptions :column="3" border>
      <el-descriptions-item label="结算单号">{{ detailData.settlementNo }}</el-descriptions-item>
      <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
      <el-descriptions-item label="支付方式">
        <el-tag v-if="detailData.payMethod === 1">现金</el-tag>
        <el-tag v-else-if="detailData.payMethod === 2" type="success">微信</el-tag>
        <el-tag v-else-if="detailData.payMethod === 3" type="primary">支付宝</el-tag>
        <el-tag v-else-if="detailData.payMethod === 4" type="warning">医保</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="总金额">{{ detailData.totalAmount?.toFixed(2) }}</el-descriptions-item>
      <el-descriptions-item label="优惠金额">{{ detailData.discountAmount?.toFixed(2) }}</el-descriptions-item>
      <el-descriptions-item label="实收金额">
        <span style="color: #f56c6c;">{{ detailData.actualAmount?.toFixed(2) }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="结算时间">{{ detailData.settleTime }}</el-descriptions-item>
      <el-descriptions-item label="操作员">{{ detailData.operatorName }}</el-descriptions-item>
      <el-descriptions-item label="发票号">{{ detailData.invoiceNo }}</el-descriptions-item>
    </el-descriptions>
    <h4 style="margin-top: 16px;">费用明细</h4>
    <el-table :data="detailData.items" border style="width: 100%;">
      <el-table-column prop="type" label="类型" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.type === 1">挂号费</el-tag>
          <el-tag v-else-if="scope.row.type === 2">药品费</el-tag>
          <el-tag v-else-if="scope.row.type === 3">检查费</el-tag>
          <el-tag v-else-if="scope.row.type === 4">其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="quantity" label="数量" width="80" align="center" />
      <el-table-column prop="price" label="单价" width="100" align="right">
        <template #default="scope">
          {{ scope.row.price?.toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100" align="right">
        <template #default="scope">
          {{ scope.row.amount?.toFixed(2) }}
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="detailVisible = false">关闭</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="printVisible" title="打印小票" width="500px">
    <div id="print-area" style="padding: 20px; font-family: monospace; font-size: 14px;">
      <div style="text-align: center; font-size: 18px; font-weight: bold; margin-bottom: 10px;">收费结算小票</div>
      <div style="border-top: 1px dashed #000; margin: 10px 0;"></div>
      <div style="display: flex; justify-content: space-between;">
        <span>结算单号：{{ printData.settlementNo }}</span>
      </div>
      <div style="display: flex; justify-content: space-between;">
        <span>患者姓名：{{ printData.patientName }}</span>
      </div>
      <div style="display: flex; justify-content: space-between;">
        <span>发票号：{{ printData.invoiceNo }}</span>
      </div>
      <div style="border-top: 1px dashed #000; margin: 10px 0;"></div>
      <table style="width: 100%; border-collapse: collapse;">
        <thead>
          <tr style="border-bottom: 1px solid #000;">
            <th style="text-align: left; padding: 4px 0;">项目</th>
            <th style="text-align: center; padding: 4px 0;">数量</th>
            <th style="text-align: right; padding: 4px 0;">金额</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in printData.items" :key="item.name" style="border-bottom: 1px dashed #ccc;">
            <td style="padding: 4px 0;">{{ item.name }}</td>
            <td style="text-align: center; padding: 4px 0;">{{ item.quantity }}</td>
            <td style="text-align: right; padding: 4px 0;">{{ item.amount?.toFixed(2) }}</td>
          </tr>
        </tbody>
      </table>
      <div style="border-top: 1px dashed #000; margin: 10px 0;"></div>
      <div style="text-align: right;">
        <div>总金额：{{ printData.totalAmount?.toFixed(2) }}</div>
        <div>优惠金额：{{ printData.discountAmount?.toFixed(2) }}</div>
        <div style="font-weight: bold;">实收金额：{{ printData.actualAmount?.toFixed(2) }}</div>
      </div>
      <div style="border-top: 1px dashed #000; margin: 10px 0;"></div>
      <div style="display: flex; justify-content: space-between;">
        <span>支付方式：{{ payMethodText(printData.payMethod) }}</span>
      </div>
      <div style="display: flex; justify-content: space-between;">
        <span>结算时间：{{ printData.settleTime }}</span>
      </div>
      <div style="display: flex; justify-content: space-between;">
        <span>操作员：{{ printData.operatorName }}</span>
      </div>
    </div>
    <template #footer>
      <el-button @click="printVisible = false">关闭</el-button>
      <el-button type="primary" @click="doPrint">打印</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="chargeVisible" title="收费结算" width="700px">
    <el-form ref="chargeFormRef" :model="chargeForm" :rules="chargeRules" label-width="100px">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="患者ID" prop="patientId">
            <el-input v-model="chargeForm.patientId" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="患者姓名" prop="patientName">
            <el-input v-model="chargeForm.patientName" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="挂号费" prop="registrationFee">
            <el-input-number v-model="chargeForm.registrationFee" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="药品费" prop="drugFee">
            <el-input-number v-model="chargeForm.drugFee" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="检查费" prop="examFee">
            <el-input-number v-model="chargeForm.examFee" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="其他费" prop="otherFee">
            <el-input-number v-model="chargeForm.otherFee" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="总金额">
            <el-input :model-value="chargeTotalAmount.toFixed(2)" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="优惠金额" prop="discountAmount">
            <el-input-number v-model="chargeForm.discountAmount" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="实收金额">
            <el-input :model-value="chargeActualAmount.toFixed(2)" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付方式" prop="payMethod">
            <el-select v-model="chargeForm.payMethod" placeholder="请选择">
              <el-option label="现金" :value="1" />
              <el-option label="微信" :value="2" />
              <el-option label="支付宝" :value="3" />
              <el-option label="医保" :value="4" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="chargeForm.remark" type="textarea" :rows="2" />
      </el-form-item>

      <h4 style="margin: 10px 0;">费用明细</h4>
      <div v-for="(item, index) in chargeForm.items" :key="index" style="display: flex; gap: 8px; margin-bottom: 8px; align-items: center;">
        <el-select v-model="item.type" placeholder="类型" style="width: 120px;">
          <el-option label="挂号费" :value="1" />
          <el-option label="药品费" :value="2" />
          <el-option label="检查费" :value="3" />
          <el-option label="其他" :value="4" />
        </el-select>
        <el-input v-model="item.name" placeholder="名称" style="width: 140px;" />
        <el-input-number v-model="item.quantity" :min="1" style="width: 110px;" />
        <el-input-number v-model="item.price" :min="0" :precision="2" style="width: 130px;" />
        <span style="width: 90px; text-align: right;">{{ (item.quantity * item.price)?.toFixed(2) }}</span>
        <el-button type="danger" :icon="Delete" circle @click="removeChargeItem(index)" />
      </div>
      <el-button type="primary" link @click="addChargeItem">+ 添加明细</el-button>
    </el-form>
    <template #footer>
      <el-button @click="chargeVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleChargeSubmit">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="refundVisible" title="退费申请" width="500px">
    <el-form ref="refundFormRef" :model="refundForm" :rules="refundRules" label-width="100px">
      <el-form-item label="退费金额" prop="refundAmount">
        <el-input-number v-model="refundForm.refundAmount" :min="0" :precision="2" style="width: 100%;" />
      </el-form-item>
      <el-form-item label="退费原因" prop="reason">
        <el-input v-model="refundForm.reason" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="refundVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleRefundSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import {
  getSettlementPage,
  getSettlement,
  createSettlement,
  applyRefund
} from '@/api/settlement'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  payMethod: null,
  status: null
})

const dateRange = ref([])
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = reactive({
  settlementNo: '',
  patientName: '',
  payMethod: null,
  totalAmount: null,
  discountAmount: null,
  actualAmount: null,
  settleTime: '',
  operatorName: '',
  invoiceNo: '',
  items: []
})
const printVisible = ref(false)
const printData = reactive({
  settlementNo: '',
  patientName: '',
  invoiceNo: '',
  items: [],
  totalAmount: null,
  discountAmount: null,
  actualAmount: null,
  payMethod: null,
  settleTime: '',
  operatorName: ''
})
const chargeVisible = ref(false)
const chargeFormRef = ref(null)
const chargeForm = reactive({
  patientId: '',
  patientName: '',
  registrationFee: 0,
  drugFee: 0,
  examFee: 0,
  otherFee: 0,
  discountAmount: 0,
  payMethod: null,
  remark: '',
  items: []
})
const chargeRules = {
  patientId: [{ required: true, message: '请输入患者ID', trigger: 'blur' }],
  patientName: [{ required: true, message: '请输入患者姓名', trigger: 'blur' }],
  payMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
}
const refundVisible = ref(false)
const refundFormRef = ref(null)
const refundForm = reactive({
  settlementId: null,
  refundAmount: 0,
  reason: ''
})
const refundRules = {
  refundAmount: [{ required: true, message: '请输入退费金额', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入退费原因', trigger: 'blur' }]
}

const chargeTotalAmount = computed(() => {
  return (chargeForm.registrationFee || 0) + (chargeForm.drugFee || 0) + (chargeForm.examFee || 0) + (chargeForm.otherFee || 0)
})

const chargeActualAmount = computed(() => {
  return chargeTotalAmount.value - (chargeForm.discountAmount || 0)
})

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const payMethodText = (val) => {
  const map = { 1: '现金', 2: '微信', 3: '支付宝', 4: '医保' }
  return map[val] || ''
}

const handleQuery = async () => {
  const params = { ...queryForm }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  }
  const res = await getSettlementPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.payMethod = null
  queryForm.status = null
  queryForm.pageNum = 1
  dateRange.value = []
  handleQuery()
}

const handleView = async (row) => {
  const res = await getSettlement(row.id)
  Object.assign(detailData, res.data)
  detailVisible.value = true
}

const handlePrint = async (row) => {
  const res = await getSettlement(row.id)
  Object.assign(printData, res.data)
  printVisible.value = true
}

const doPrint = () => {
  window.print()
}

const handleCharge = () => {
  Object.assign(chargeForm, {
    patientId: '',
    patientName: '',
    registrationFee: 0,
    drugFee: 0,
    examFee: 0,
    otherFee: 0,
    discountAmount: 0,
    payMethod: null,
    remark: '',
    items: []
  })
  chargeVisible.value = true
}

const addChargeItem = () => {
  chargeForm.items.push({ type: null, name: '', quantity: 1, price: 0 })
}

const removeChargeItem = (index) => {
  chargeForm.items.splice(index, 1)
}

const handleChargeSubmit = async () => {
  if (!chargeFormRef.value) return
  await chargeFormRef.value.validate(async (valid) => {
    if (valid) {
      await createSettlement(chargeForm)
      ElMessage.success('收费成功')
      chargeVisible.value = false
      handleQuery()
    }
  })
}

const handleRefund = (row) => {
  refundForm.settlementId = row.id
  refundForm.refundAmount = 0
  refundForm.reason = ''
  refundVisible.value = true
}

const handleRefundSubmit = async () => {
  if (!refundFormRef.value) return
  await refundFormRef.value.validate(async (valid) => {
    if (valid) {
      await applyRefund({ settlementId: refundForm.settlementId, refundAmount: refundForm.refundAmount, reason: refundForm.reason })
      ElMessage.success('退费申请已提交')
      refundVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  handleQuery()
})
</script>
