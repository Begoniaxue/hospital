<template>
  <div class="page-container">
    <div class="page-header">
      <h3>医生评价管理</h3>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="医生ID">
        <el-input v-model="queryForm.doctorId" placeholder="请输入医生ID" clearable />
      </el-form-item>
      <el-form-item label="评分">
        <el-select v-model="queryForm.rating" placeholder="请选择评分" clearable>
          <el-option label="全部" :value="null" />
          <el-option label="1星" :value="1" />
          <el-option label="2星" :value="2" />
          <el-option label="3星" :value="3" />
          <el-option label="4星" :value="4" />
          <el-option label="5星" :value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
          <el-option label="全部" :value="null" />
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
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
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="patientName" label="患者姓名" width="120" />
      <el-table-column prop="doctorName" label="医生姓名" width="120" />
      <el-table-column prop="departmentName" label="科室" width="150" />
      <el-table-column prop="rating" label="评分" width="140">
        <template #default="scope">
          <div class="rating-stars">
            <el-rate
              v-model="scope.row.rating"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" show-overflow-tooltip min-width="250" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="180" />
      <el-table-column label="操作" width="240" fixed="right">
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
              type="primary"
              @click="handleReply(scope.row)"
            >
              回复
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleApprove(scope.row)"
              v-if="scope.row.status === 0"
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

  <el-dialog v-model="detailVisible" :title="detailTitle" width="700px">
    <el-descriptions :column="2" border v-if="detailData">
      <el-descriptions-item label="ID">{{ detailData.id }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(detailData.status)" size="small">
          {{ getStatusText(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
      <el-descriptions-item label="提交时间">{{ detailData.createTime }}</el-descriptions-item>
      <el-descriptions-item label="医生姓名">{{ detailData.doctorName }}</el-descriptions-item>
      <el-descriptions-item label="科室">{{ detailData.departmentName }}</el-descriptions-item>
      <el-descriptions-item label="评分" :span="2">
        <el-rate
          v-model="detailData.rating"
          disabled
          show-score
          text-color="#ff9900"
          score-template="{value}分"
        />
      </el-descriptions-item>
      <el-descriptions-item label="评价内容" :span="2">
        <div style="white-space: pre-wrap; min-height: 80px;">{{ detailData.content }}</div>
      </el-descriptions-item>
      <el-descriptions-item label="图片" :span="2" v-if="detailData.images && detailData.images.length > 0">
        <div class="image-list">
          <el-image
            v-for="(img, index) in detailData.images"
            :key="index"
            :src="img"
            :preview-src-list="detailData.images"
            fit="cover"
            style="width: 100px; height: 100px; margin-right: 10px; border-radius: 4px;"
          />
        </div>
      </el-descriptions-item>
      <el-descriptions-item label="回复内容" :span="2" v-if="detailData.reply">
        <div style="white-space: pre-wrap; min-height: 60px; background: #f5f7fa; padding: 10px; border-radius: 4px;">
          {{ detailData.reply }}
        </div>
      </el-descriptions-item>
      <el-descriptions-item label="回复时间" v-if="detailData.replyTime">{{ detailData.replyTime }}</el-descriptions-item>
      <el-descriptions-item label="回复人" v-if="detailData.replyUserName">{{ detailData.replyUserName }}</el-descriptions-item>
    </el-descriptions>

    <el-form
      v-if="showReplyForm"
      ref="replyFormRef"
      :model="replyForm"
      :rules="replyRules"
      label-width="80px"
      style="margin-top: 20px; padding-top: 20px; border-top: 1px solid #ebeef5;"
    >
      <el-form-item label="回复内容" prop="reply">
        <el-input
          v-model="replyForm.reply"
          type="textarea"
          :rows="4"
          placeholder="请输入回复内容"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="detailVisible = false">关闭</el-button>
      <template v-if="showReplyForm">
        <el-button @click="detailVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReplySubmit">提交回复</el-button>
      </template>
      <template v-if="showAuditForm && detailData.status === 0">
        <el-button @click="detailVisible = false">取消</el-button>
        <el-button type="danger" @click="handleAuditReject">驳回</el-button>
        <el-button type="success" @click="handleAuditApprove">通过</el-button>
      </template>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import {
  getReviewPage,
  getReviewDetail,
  replyReview,
  updateReviewStatus
} from '@/api/review'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  doctorId: '',
  rating: null,
  status: null
})

const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = ref(null)
const detailTitle = ref('评价详情')
const showReplyForm = ref(false)
const showAuditForm = ref(false)
const replyFormRef = ref(null)

const replyForm = reactive({
  id: null,
  reply: ''
})

const replyRules = {
  reply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已驳回'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

const handleQuery = async () => {
  const res = await getReviewPage(queryForm)
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const handleReset = () => {
  queryForm.doctorId = ''
  queryForm.rating = null
  queryForm.status = null
  queryForm.pageNum = 1
  handleQuery()
}

const handleView = async (row) => {
  const res = await getReviewDetail(row.id)
  detailData.value = res.data
  detailTitle.value = '评价详情'
  showReplyForm.value = false
  showAuditForm.value = false
  detailVisible.value = true
}

const handleReply = async (row) => {
  const res = await getReviewDetail(row.id)
  detailData.value = res.data
  detailTitle.value = '回复评价'
  replyForm.id = row.id
  replyForm.reply = ''
  showReplyForm.value = true
  showAuditForm.value = false
  detailVisible.value = true
}

const handleReplySubmit = async () => {
  if (!replyFormRef.value) return
  await replyFormRef.value.validate(async (valid) => {
    if (valid) {
      await replyReview({
        id: replyForm.id,
        reply: replyForm.reply
      })
      ElMessage.success('回复成功')
      detailVisible.value = false
      handleQuery()
    }
  })
}

const handleApprove = async (row) => {
  const res = await getReviewDetail(row.id)
  detailData.value = res.data
  detailTitle.value = '审核评价'
  showReplyForm.value = false
  showAuditForm.value = true
  detailVisible.value = true
}

const handleAuditApprove = () => {
  ElMessageBox.confirm('确定要通过该评价吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    await updateReviewStatus({
      id: detailData.value.id,
      status: 1
    })
    ElMessage.success('审核通过')
    detailVisible.value = false
    handleQuery()
  })
}

const handleAuditReject = () => {
  ElMessageBox.confirm('确定要驳回该评价吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await updateReviewStatus({
      id: detailData.value.id,
      status: 2
    })
    ElMessage.success('已驳回')
    detailVisible.value = false
    handleQuery()
  })
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.search-form {
  margin-bottom: 20px;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.rating-stars {
  display: flex;
  align-items: center;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>
