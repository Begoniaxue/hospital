<template>
  <div class="page-container">
    <div class="page-header">
      <h3>投诉建议管理</h3>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="类型">
        <el-select v-model="queryForm.type" placeholder="请选择类型" clearable>
          <el-option label="全部" :value="null" />
          <el-option label="投诉" :value="1" />
          <el-option label="建议" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
          <el-option label="全部" :value="null" />
          <el-option label="待处理" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已处理" :value="2" />
          <el-option label="已关闭" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键字">
        <el-input v-model="queryForm.keyword" placeholder="请输入关键字" clearable />
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
      <el-table-column prop="complaintNo" label="投诉单号" width="180" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.type === 1 ? 'danger' : 'primary'" size="small">
            {{ scope.row.type === 1 ? '投诉' : '建议' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="patientName" label="患者姓名" width="120" />
      <el-table-column prop="patientPhone" label="患者电话" width="140" />
      <el-table-column label="投诉对象" width="180">
        <template #default="scope">
          <div>
            <div v-if="scope.row.departmentName">科室：{{ scope.row.departmentName }}</div>
            <div v-if="scope.row.doctorName">医生：{{ scope.row.doctorName }}</div>
            <div v-if="!scope.row.departmentName && !scope.row.doctorName">-</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" show-overflow-tooltip min-width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              @click="handleView(scope.row)"
            >
              查看详情
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="handleProcess(scope.row)"
              v-if="scope.row.status === 0 || scope.row.status === 1"
            >
              处理
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

  <el-dialog v-model="detailVisible" title="投诉建议详情" width="700px">
    <el-descriptions :column="2" border v-if="detailData">
      <el-descriptions-item label="投诉单号">{{ detailData.complaintNo }}</el-descriptions-item>
      <el-descriptions-item label="类型">
        <el-tag :type="detailData.type === 1 ? 'danger' : 'primary'" size="small">
          {{ detailData.type === 1 ? '投诉' : '建议' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
      <el-descriptions-item label="患者电话">{{ detailData.patientPhone }}</el-descriptions-item>
      <el-descriptions-item label="科室" v-if="detailData.departmentName">{{ detailData.departmentName }}</el-descriptions-item>
      <el-descriptions-item label="医生" v-if="detailData.doctorName">{{ detailData.doctorName }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(detailData.status)" size="small">
          {{ getStatusText(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="提交时间">{{ detailData.createTime }}</el-descriptions-item>
      <el-descriptions-item label="标题" :span="2">{{ detailData.title }}</el-descriptions-item>
      <el-descriptions-item label="内容" :span="2">
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
      <el-descriptions-item label="处理人" v-if="detailData.handlerName">{{ detailData.handlerName }}</el-descriptions-item>
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
      <el-button type="primary" @click="handleReplySubmit" v-if="showReplyForm">提交回复</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import {
  getComplaintPage,
  getComplaintDetail,
  handleComplaint,
  updateComplaintStatus
} from '@/api/complaint'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  type: null,
  status: null,
  keyword: ''
})

const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = ref(null)
const showReplyForm = ref(false)
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
    0: '待处理',
    1: '处理中',
    2: '已处理',
    3: '已关闭'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info'
  }
  return typeMap[status] || 'info'
}

const handleQuery = async () => {
  const res = await getComplaintPage(queryForm)
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const handleReset = () => {
  queryForm.type = null
  queryForm.status = null
  queryForm.keyword = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleView = async (row) => {
  const res = await getComplaintDetail(row.id)
  detailData.value = res.data
  showReplyForm.value = false
  detailVisible.value = true
}

const handleProcess = async (row) => {
  const res = await getComplaintDetail(row.id)
  detailData.value = res.data
  replyForm.id = row.id
  replyForm.reply = ''
  showReplyForm.value = true
  detailVisible.value = true
}

const handleReplySubmit = async () => {
  if (!replyFormRef.value) return
  await replyFormRef.value.validate(async (valid) => {
    if (valid) {
      await handleComplaint({
        id: replyForm.id,
        reply: replyForm.reply
      })
      ElMessage.success('回复成功')
      detailVisible.value = false
      handleQuery()
    }
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

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>
