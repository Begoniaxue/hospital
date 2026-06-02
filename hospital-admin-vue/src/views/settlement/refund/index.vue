<template>
  <div class="page-container">
    <div class="page-header">
      <h3>退费管理</h3>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="queryForm.keyword" placeholder="退费单号/患者姓名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="全部" clearable>
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间">
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
      <el-table-column prop="refundNo" label="退费单号" width="160" />
      <el-table-column prop="settlementNo" label="结算单号" width="160" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="refundAmount" label="退费金额" width="120" align="right">
        <template #default="scope">
          <span style="color: #f56c6c;">{{ scope.row.refundAmount?.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="reason" label="退费原因" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === 1" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applyUserName" label="申请人" width="100" />
      <el-table-column prop="applyTime" label="申请时间" width="180" />
      <el-table-column prop="auditUserName" label="审核人" width="100" />
      <el-table-column prop="auditTime" label="审核时间" width="180" />
      <el-table-column prop="auditRemark" label="审核备注" show-overflow-tooltip />
      <el-table-column label="操作" width="160" fixed="right">
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
              type="warning"
              @click="handleAudit(scope.row)"
              v-if="hasPermission('settlement:refund:audit') && scope.row.status === 0"
            >
              审核
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

  <el-dialog v-model="detailVisible" title="退费详情" width="600px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="退费单号">{{ detailData.refundNo }}</el-descriptions-item>
      <el-descriptions-item label="结算单号">{{ detailData.settlementNo }}</el-descriptions-item>
      <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
      <el-descriptions-item label="退费金额">
        <span style="color: #f56c6c;">{{ detailData.refundAmount?.toFixed(2) }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="退费原因" :span="2">{{ detailData.reason }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag v-if="detailData.status === 0" type="warning">待审核</el-tag>
        <el-tag v-else-if="detailData.status === 1" type="success">已通过</el-tag>
        <el-tag v-else-if="detailData.status === 2" type="danger">已拒绝</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="申请人">{{ detailData.applyUserName }}</el-descriptions-item>
      <el-descriptions-item label="申请时间">{{ detailData.applyTime }}</el-descriptions-item>
      <el-descriptions-item label="审核人">{{ detailData.auditUserName }}</el-descriptions-item>
      <el-descriptions-item label="审核时间">{{ detailData.auditTime }}</el-descriptions-item>
      <el-descriptions-item label="审核备注">{{ detailData.auditRemark }}</el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailVisible = false">关闭</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="auditVisible" title="退费审核" width="500px">
    <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="100px">
      <el-form-item label="审核结果" prop="status">
        <el-radio-group v-model="auditForm.status">
          <el-radio :value="1">通过</el-radio>
          <el-radio :value="2">拒绝</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="审核备注" prop="auditRemark">
        <el-input v-model="auditForm.auditRemark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="auditVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleAuditSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getRefundPage,
  getRefund,
  auditRefund
} from '@/api/settlement'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  status: null
})

const dateRange = ref([])
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = reactive({
  refundNo: '',
  settlementNo: '',
  patientName: '',
  refundAmount: null,
  reason: '',
  status: null,
  applyUserName: '',
  applyTime: '',
  auditUserName: '',
  auditTime: '',
  auditRemark: ''
})
const auditVisible = ref(false)
const auditFormRef = ref(null)
const auditForm = reactive({
  refundId: null,
  status: 1,
  auditRemark: ''
})
const auditRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
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
  const res = await getRefundPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.status = null
  queryForm.pageNum = 1
  dateRange.value = []
  handleQuery()
}

const handleView = async (row) => {
  const res = await getRefund(row.id)
  Object.assign(detailData, res.data)
  detailVisible.value = true
}

const handleAudit = (row) => {
  auditForm.refundId = row.id
  auditForm.status = 1
  auditForm.auditRemark = ''
  auditVisible.value = true
}

const handleAuditSubmit = async () => {
  if (!auditFormRef.value) return
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      await auditRefund({ refundId: auditForm.refundId, status: auditForm.status, auditRemark: auditForm.auditRemark })
      ElMessage.success('审核成功')
      auditVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  handleQuery()
})
</script>
