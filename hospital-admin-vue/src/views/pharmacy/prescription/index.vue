<template>
  <div class="page-container">
    <div class="page-header">
      <h3>处方审核发药</h3>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="queryForm.keyword" placeholder="处方号/患者姓名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择" clearable>
          <el-option label="待审核" :value="0" />
          <el-option label="已审核" :value="1" />
          <el-option label="已调配" :value="2" />
          <el-option label="已发药" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围">
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
      <el-table-column prop="prescriptionNo" label="处方号" width="140" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="doctorName" label="开方医生" width="100" />
      <el-table-column prop="diagnosis" label="诊断" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="statusTagType(scope.row.status)">
            {{ statusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="auditUserName" label="审核人" width="100" />
      <el-table-column prop="dispenseUserName" label="调配人" width="100" />
      <el-table-column prop="issueUserName" label="发药人" width="100" />
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
              type="warning"
              @click="handleAudit(scope.row)"
              v-if="scope.row.status === 0 && hasPermission('pharmacy:prescription:audit')"
            >
              审核
            </el-button>
            <el-button
              size="small"
              link
              type="primary"
              @click="handleDispense(scope.row)"
              v-if="scope.row.status === 1 && hasPermission('pharmacy:prescription:dispense')"
            >
              调配
            </el-button>
            <el-button
              size="small"
              link
              type="success"
              @click="handleIssue(scope.row)"
              v-if="scope.row.status === 2 && hasPermission('pharmacy:prescription:issue')"
            >
              发药
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

  <el-dialog v-model="detailDialogVisible" title="处方详情" width="800px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="处方号">{{ detailData.prescriptionNo }}</el-descriptions-item>
      <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
      <el-descriptions-item label="开方医生">{{ detailData.doctorName }}</el-descriptions-item>
      <el-descriptions-item label="诊断">{{ detailData.diagnosis }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusTagType(detailData.status)">{{ statusLabel(detailData.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
    </el-descriptions>
    <el-table :data="detailData.items" border style="width: 100%; margin-top: 20px;">
      <el-table-column prop="drugName" label="药品名称" />
      <el-table-column prop="specification" label="规格" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="price" label="单价" width="100">
        <template #default="scope">
          {{ Number(scope.row.price).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="scope">
          {{ Number(scope.row.amount).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="dosage" label="用法用量" />
      <el-table-column prop="frequency" label="频次" />
      <el-table-column prop="days" label="天数" width="80" />
    </el-table>
  </el-dialog>

  <el-dialog v-model="auditDialogVisible" title="处方审核" width="500px">
    <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="100px">
      <el-form-item label="审核结果" prop="status">
        <el-radio-group v-model="auditForm.status">
          <el-radio :value="1">通过</el-radio>
          <el-radio :value="4">拒绝</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="auditForm.remark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="auditDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getPrescriptionPage,
  getPrescription,
  auditPrescription,
  dispensePrescription,
  issuePrescription
} from '@/api/prescription'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: ''
})

const dateRange = ref([])
const tableData = ref([])
const total = ref(0)
const detailDialogVisible = ref(false)
const detailData = ref({})
const auditDialogVisible = ref(false)
const auditFormRef = ref(null)
const currentPrescriptionId = ref(null)

const auditForm = reactive({
  status: 1,
  remark: ''
})

const auditRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const statusTagType = (status) => {
  const map = { 0: 'warning', 1: '', 2: 'info', 3: 'success', 4: 'danger' }
  return map[status] || ''
}

const statusLabel = (status) => {
  const map = { 0: '待审核', 1: '已审核', 2: '已调配', 3: '已发药', 4: '已取消' }
  return map[status] || ''
}

const handleQuery = async () => {
  const params = { ...queryForm }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  }
  const res = await getPrescriptionPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.status = ''
  queryForm.pageNum = 1
  dateRange.value = []
  handleQuery()
}

const handleView = async (row) => {
  const res = await getPrescription(row.id)
  detailData.value = res.data
  detailDialogVisible.value = true
}

const handleAudit = (row) => {
  currentPrescriptionId.value = row.id
  Object.assign(auditForm, {
    status: 1,
    remark: ''
  })
  auditDialogVisible.value = true
}

const handleAuditSubmit = async () => {
  if (!auditFormRef.value) return
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      await auditPrescription({
        prescriptionId: currentPrescriptionId.value,
        status: auditForm.status,
        remark: auditForm.remark
      })
      ElMessage.success('审核成功')
      auditDialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDispense = (row) => {
  ElMessageBox.confirm('确定要调配该处方吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await dispensePrescription(row.id)
    ElMessage.success('调配成功')
    handleQuery()
  })
}

const handleIssue = (row) => {
  ElMessageBox.confirm('确定要发药吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await issuePrescription(row.id)
    ElMessage.success('发药成功')
    handleQuery()
  })
}

onMounted(() => {
  handleQuery()
})
</script>
