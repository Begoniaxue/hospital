<template>
  <div class="page-container">
    <div class="page-header">
      <h3>挂号订单管理</h3>
    </div>

    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="4.8" v-for="item in statistics" :key="item.label">
        <el-card :body-style="{ padding: '20px' }">
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <div style="font-size: 14px; color: #909399;">{{ item.label }}</div>
              <div style="font-size: 28px; font-weight: bold; color: #303133; margin-top: 10px;">{{ item.value }}</div>
            </div>
            <el-icon :size="40" :color="item.color"><component :is="item.icon" /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="挂号单号">
        <el-input v-model="queryForm.orderNo" placeholder="请输入挂号单号" clearable />
      </el-form-item>
      <el-form-item label="患者姓名">
        <el-input v-model="queryForm.patientName" placeholder="请输入患者姓名" clearable />
      </el-form-item>
      <el-form-item label="科室">
        <el-select v-model="queryForm.departmentId" placeholder="请选择科室" clearable>
          <el-option
            v-for="item in departmentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="医生">
        <el-select v-model="queryForm.doctorId" placeholder="请选择医生" clearable filterable>
          <el-option
            v-for="item in doctorList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
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
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
          <el-option label="待支付" :value="0" />
          <el-option label="已支付/待就诊" :value="1" />
          <el-option label="就诊中" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
          <el-option label="已退费" :value="5" />
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
      <el-table-column prop="orderNo" label="挂号单号" width="180" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="patientPhone" label="联系电话" width="130" />
      <el-table-column prop="departmentName" label="科室" width="150" />
      <el-table-column prop="doctorName" label="医生" width="100" />
      <el-table-column prop="doctorTitle" label="职称" width="120" />
      <el-table-column prop="scheduleDate" label="就诊日期" width="120" />
      <el-table-column prop="timeSlot" label="时段" width="80">
        <template #default="scope">
          {{ scope.row.timeSlot === 1 ? '上午' : scope.row.timeSlot === 2 ? '下午' : '全天' }}
        </template>
      </el-table-column>
      <el-table-column prop="registrationFee" label="挂号费(元)" width="120">
        <template #default="scope">
          ¥{{ scope.row.registrationFee }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="140">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="挂号时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              @click="handleView(scope.row)"
            >
              详情
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handleCheckin(scope.row)"
              v-if="scope.row.status === 1 && hasPermission('outpatient:registration:edit')"
            >
              签到
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="handleFinish(scope.row)"
              v-if="scope.row.status === 2 && hasPermission('outpatient:registration:edit')"
            >
              完成就诊
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleRefund(scope.row)"
              v-if="[1, 2].includes(scope.row.status) && hasPermission('outpatient:registration:refund')"
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

  <el-dialog v-model="detailVisible" title="挂号订单详情" width="700px">
    <el-descriptions :column="2" border v-if="detailData">
      <el-descriptions-item label="挂号单号">{{ detailData.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="getStatusType(detailData.status)">
          {{ getStatusText(detailData.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ detailData.gender === 1 ? '男' : '女' }}</el-descriptions-item>
      <el-descriptions-item label="年龄">{{ detailData.age }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ detailData.patientPhone }}</el-descriptions-item>
      <el-descriptions-item label="身份证号">{{ detailData.idCard }}</el-descriptions-item>
      <el-descriptions-item label="科室">{{ detailData.departmentName }}</el-descriptions-item>
      <el-descriptions-item label="医生">{{ detailData.doctorName }}</el-descriptions-item>
      <el-descriptions-item label="职称">{{ detailData.doctorTitle }}</el-descriptions-item>
      <el-descriptions-item label="就诊日期">{{ detailData.scheduleDate }}</el-descriptions-item>
      <el-descriptions-item label="时段">
        {{ detailData.timeSlot === 1 ? '上午' : detailData.timeSlot === 2 ? '下午' : '全天' }}
      </el-descriptions-item>
      <el-descriptions-item label="挂号费">¥{{ detailData.registrationFee }}</el-descriptions-item>
      <el-descriptions-item label="支付方式">{{ detailData.payMethod || '-' }}</el-descriptions-item>
      <el-descriptions-item label="挂号时间" :span="2">{{ detailData.createTime }}</el-descriptions-item>
      <el-descriptions-item label="签到时间" :span="2">{{ detailData.checkinTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="完成时间" :span="2">{{ detailData.finishTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="退费原因" v-if="detailData.status === 5" :span="2">
        {{ detailData.refundReason || '-' }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailVisible = false">关闭</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="refundVisible" title="退费确认" width="500px">
    <el-form ref="refundFormRef" :model="refundForm" :rules="refundRules" label-width="100px">
      <el-form-item label="退费原因" prop="reason">
        <el-input v-model="refundForm.reason" type="textarea" :rows="4" placeholder="请输入退费原因" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="refundVisible = false">取消</el-button>
      <el-button type="primary" @click="handleRefundSubmit">确认退费</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getRegistrationPage,
  getRegistration,
  updateRegistrationStatus,
  checkinRegistration,
  finishRegistration,
  refundRegistration,
  getRegistrationStatistics
} from '@/api/registration'
import { getDepartmentList } from '@/api/department'
import { getDoctorList } from '@/api/doctor'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: '',
  patientName: '',
  departmentId: null,
  doctorId: null,
  status: null
})

const dateRange = ref([])
const tableData = ref([])
const departmentList = ref([])
const doctorList = ref([])
const total = ref(0)
const detailVisible = ref(false)
const refundVisible = ref(false)
const detailData = ref(null)
const refundFormRef = ref(null)

const statistics = ref([
  { label: '挂号总数', value: 0, icon: 'Tickets', color: '#409EFF' },
  { label: '待就诊', value: 0, icon: 'Clock', color: '#E6A23C' },
  { label: '已完成', value: 0, icon: 'CircleCheck', color: '#67C23A' },
  { label: '已取消', value: 0, icon: 'CircleClose', color: '#909399' },
  { label: '已退费', value: 0, icon: 'Refund', color: '#F56C6C' }
])

const refundForm = reactive({
  id: null,
  reason: ''
})

const refundRules = {
  reason: [{ required: true, message: '请输入退费原因', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付/待就诊',
    2: '就诊中',
    3: '已完成',
    4: '已取消',
    5: '已退费'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info',
    4: 'info',
    5: 'danger'
  }
  return typeMap[status] || 'info'
}

const loadDepartmentList = async () => {
  const res = await getDepartmentList({ status: 1 })
  departmentList.value = res.data || []
}

const loadDoctorList = async () => {
  const res = await getDoctorList({ status: 1 })
  doctorList.value = res.data || []
}

const loadStatistics = async () => {
  const res = await getRegistrationStatistics()
  const data = res.data || {}
  statistics.value = [
    { label: '挂号总数', value: data.total || 0, icon: 'Tickets', color: '#409EFF' },
    { label: '待就诊', value: data.pending || 0, icon: 'Clock', color: '#E6A23C' },
    { label: '已完成', value: data.completed || 0, icon: 'CircleCheck', color: '#67C23A' },
    { label: '已取消', value: data.cancelled || 0, icon: 'CircleClose', color: '#909399' },
    { label: '已退费', value: data.refunded || 0, icon: 'Refund', color: '#F56C6C' }
  ]
}

const handleQuery = async () => {
  const params = { ...queryForm }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = dateRange.value[0]
    params.endDate = dateRange.value[1]
  }
  const res = await getRegistrationPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.orderNo = ''
  queryForm.patientName = ''
  queryForm.departmentId = null
  queryForm.doctorId = null
  queryForm.status = null
  queryForm.pageNum = 1
  dateRange.value = []
  handleQuery()
}

const handleView = async (row) => {
  const res = await getRegistration(row.id)
  detailData.value = res.data
  detailVisible.value = true
}

const handleCheckin = (row) => {
  ElMessageBox.confirm('确定要为该患者签到吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await checkinRegistration(row.id)
    ElMessage.success('签到成功')
    handleQuery()
    loadStatistics()
  })
}

const handleFinish = (row) => {
  ElMessageBox.confirm('确定要完成本次就诊吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await finishRegistration(row.id)
    ElMessage.success('就诊完成')
    handleQuery()
    loadStatistics()
  })
}

const handleRefund = (row) => {
  refundForm.id = row.id
  refundForm.reason = ''
  refundVisible.value = true
}

const handleRefundSubmit = async () => {
  if (!refundFormRef.value) return
  await refundFormRef.value.validate(async (valid) => {
    if (valid) {
      ElMessageBox.confirm('确定要进行退费操作吗？退费后将无法撤销。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await refundRegistration(refundForm.id, refundForm.reason)
        ElMessage.success('退费成功')
        refundVisible.value = false
        handleQuery()
        loadStatistics()
      })
    }
  })
}

onMounted(() => {
  loadDepartmentList()
  loadDoctorList()
  handleQuery()
  loadStatistics()
})
</script>
