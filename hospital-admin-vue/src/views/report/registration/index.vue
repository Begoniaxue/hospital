<template>
  <div class="page-container">
    <div class="page-header">
      <h3>挂号量统计</h3>
      <div>
        <el-button @click="handleExport" v-if="hasPermission('report:registration:export')">
          <el-icon><Download /></el-icon>
          导出Excel
        </el-button>
        <el-button @click="handlePrint">
          <el-icon><Printer /></el-icon>
          打印
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="queryForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="科室">
        <el-select v-model="queryForm.department" placeholder="请选择" clearable style="width: 150px;">
          <el-option label="内科" value="内科" />
          <el-option label="外科" value="外科" />
          <el-option label="儿科" value="儿科" />
          <el-option label="妇产科" value="妇产科" />
          <el-option label="骨科" value="骨科" />
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

    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #409EFF;">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">今日挂号</div>
            <div class="stat-value">{{ summary.todayCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67C23A;">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">本周挂号</div>
            <div class="stat-value">{{ summary.weekCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #E6A23C;">
            <el-icon><Tickets /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">本月挂号</div>
            <div class="stat-value">{{ summary.monthCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #F56C6C;">
            <el-icon><Money /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">挂号收入</div>
            <div class="stat-value">¥{{ (summary.totalFee || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-bottom: 20px;">
      <div ref="chartRef" style="height: 400px;"></div>
    </el-card>

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="date" label="日期" width="120" />
      <el-table-column prop="department" label="科室" width="120" />
      <el-table-column prop="count" label="挂号人数" width="120" />
      <el-table-column prop="totalFee" label="挂号费用" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.totalFee).toFixed(2) }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import * as echarts from 'echarts'
import {
  getRegistrationReport,
  getRegistrationSummary,
  exportRegistrationReport
} from '@/api/report'

const userStore = useUserStore()

const queryForm = reactive({
  dateRange: [],
  department: ''
})

const tableData = ref([])
const summary = reactive({
  todayCount: 0,
  weekCount: 0,
  monthCount: 0,
  totalFee: 0
})

const chartRef = ref(null)
let chartInstance = null

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['挂号人数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: tableData.value.map(item => item.date)
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '挂号人数',
        type: 'bar',
        data: tableData.value.map(item => item.count),
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
  chartInstance.setOption(option)
}

const handleQuery = async () => {
  const params = {}
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.startDate = queryForm.dateRange[0]
    params.endDate = queryForm.dateRange[1]
  }
  if (queryForm.department) {
    params.department = queryForm.department
  }
  
  const [reportRes, summaryRes] = await Promise.all([
    getRegistrationReport(params),
    getRegistrationSummary(params)
  ])
  
  tableData.value = reportRes.data || []
  Object.assign(summary, summaryRes.data || {})
  
  nextTick(() => {
    initChart()
  })
}

const handleReset = () => {
  queryForm.dateRange = []
  queryForm.department = ''
  handleQuery()
}

const handleExport = async () => {
  const params = {}
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.startDate = queryForm.dateRange[0]
    params.endDate = queryForm.dateRange[1]
  }
  if (queryForm.department) {
    params.department = queryForm.department
  }
  const res = await exportRegistrationReport(params)
  const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '挂号量统计.xlsx'
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const handlePrint = () => {
  window.print()
}

const handleResize = () => {
  chartInstance && chartInstance.resize()
}

onMounted(() => {
  handleQuery()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance && chartInstance.dispose()
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  margin-right: 15px;
}
.stat-content {
  flex: 1;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
</style>
