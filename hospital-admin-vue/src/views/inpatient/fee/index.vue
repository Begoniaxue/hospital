<template>
  <div class="page-container">
    <div class="page-header">
      <h3>费用汇总</h3>
      <div>
        <el-button type="primary" @click="handleAdd" v-if="hasPermission('inpatient:fee:add')">
          <el-icon><Plus /></el-icon>
          新增费用
        </el-button>
        <el-button @click="handleExport" v-if="hasPermission('inpatient:fee:export')">
          <el-icon><Download /></el-icon>
          导出Excel
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
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="queryForm.dateRange"
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

    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">总费用</div>
          <div class="stat-value">¥{{ feeSummary.totalAmount || '0.00' }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">已缴押金</div>
          <div class="stat-value">¥{{ feeSummary.depositAmount || '0.00' }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-title">剩余金额</div>
          <div class="stat-value" :class="feeSummary.balance >= 0 ? 'positive' : 'negative'">
            ¥{{ feeSummary.balance || '0.00' }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="feeNo" label="费用单号" width="180" />
      <el-table-column prop="inpatientNo" label="住院号" width="140" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="feeDate" label="费用日期" width="180" />
      <el-table-column prop="feeType" label="费用类型" width="120" />
      <el-table-column prop="feeName" label="费用名称" width="150" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="unitPrice" label="单价" width="100">
        <template #default="scope">
          ¥{{ Number(scope.row.unitPrice).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.amount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="operatorName" label="操作人" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'warning'">
            {{ scope.row.status === 1 ? '已计费' : '待计费' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" show-overflow-tooltip />
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

  <el-dialog v-model="dialogVisible" title="新增费用" width="600px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
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
      <el-form-item label="费用类型" prop="feeType">
        <el-select v-model="form.feeType" placeholder="请选择" style="width: 100%;">
          <el-option label="药品费" value="药品费" />
          <el-option label="检查费" value="检查费" />
          <el-option label="检验费" value="检验费" />
          <el-option label="治疗费" value="治疗费" />
          <el-option label="手术费" value="手术费" />
          <el-option label="床位费" value="床位费" />
          <el-option label="护理费" value="护理费" />
          <el-option label="其他费用" value="其他费用" />
        </el-select>
      </el-form-item>
      <el-form-item label="费用名称" prop="feeName">
        <el-input v-model="form.feeName" />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="数量" prop="quantity">
            <el-input-number v-model="form.quantity" :min="1" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单价" prop="unitPrice">
            <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%;" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="金额">
        <el-input :value="(form.quantity * form.unitPrice).toFixed(2)" disabled>
          <template #append>元</template>
        </el-input>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" />
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
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getFeePage,
  addFee,
  exportFee
} from '@/api/inpatient'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  patientName: '',
  inpatientNo: '',
  dateRange: []
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const inpatients = ref([])
const feeSummary = reactive({
  totalAmount: 0,
  depositAmount: 0,
  balance: 0
})

const form = reactive({
  inpatientId: null,
  feeType: '',
  feeName: '',
  quantity: 1,
  unitPrice: null,
  remark: ''
})

const rules = {
  inpatientId: [{ required: true, message: '请选择住院患者', trigger: 'change' }],
  feeType: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
  feeName: [{ required: true, message: '请输入费用名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const handleQuery = async () => {
  const params = { ...queryForm }
  if (params.dateRange && params.dateRange.length === 2) {
    params.startDate = params.dateRange[0]
    params.endDate = params.dateRange[1]
  }
  delete params.dateRange
  const res = await getFeePage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.patientName = ''
  queryForm.inpatientNo = ''
  queryForm.dateRange = []
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  inpatients.value = []
  Object.assign(form, {
    inpatientId: null,
    feeType: '',
    feeName: '',
    quantity: 1,
    unitPrice: null,
    remark: ''
  })
  dialogVisible.value = true
}

const handleExport = async () => {
  const params = { ...queryForm }
  if (params.dateRange && params.dateRange.length === 2) {
    params.startDate = params.dateRange[0]
    params.endDate = params.dateRange[1]
  }
  delete params.dateRange
  const res = await exportFee(params)
  const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '住院费用明细.xlsx'
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await addFee(form)
      ElMessage.success('新增成功')
      dialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-value.positive {
  color: #67c23a;
}
.stat-value.negative {
  color: #f56c6c;
}
</style>
