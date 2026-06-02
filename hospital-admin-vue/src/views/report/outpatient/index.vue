<template>
  <div class="page-container">
    <div class="page-header">
      <h3>门诊量统计</h3>
      <div>
        <el-button @click="handleExport" v-if="hasPermission('report:outpatient:export')">
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
      <el-form-item label="医生">
        <el-select v-model="queryForm.doctorId" placeholder="请选择" clearable style="width: 150px;">
          <el-option label="张医生" :value="1" />
          <el-option label="李医生" :value="2" />
          <el-option label="王医生" :value="3" />
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
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">今日门诊</div>
            <div class="stat-value">{{ summary.todayCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67C23A;">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">本周门诊</div>
            <div class="stat-value">{{ summary.weekCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #E6A23C;">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">本月门诊</div>
            <div class="stat-value">{{ summary.monthCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #909399;">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">平均就诊时长</div>
            <div class="stat-value">{{ summary.avgDuration || 0 }}分钟</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>门诊量趋势</span>
          </template>
          <div ref="lineChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>科室分布</span>
          </template>
          <div ref="barChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="date" label="日期" width="120" />
      <el-table-column prop="department" label="科室" width="120" />
      <el-table-column prop="doctorName" label="医生" width="100" />
      <el-table-column prop="count" label="接诊人数" width="120" />
      <el-table-column prop="avgDuration" label="平均时长(分钟)" width="140" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import * as echarts from 'echarts'
import {
  getOutpatientReport,
  getOutpatientSummary,
  exportOutpatientReport
} from '@/api/report'

const userStore = useUserStore()

const queryForm = reactive({
  dateRange: [],
  department: '',
  doctorId: ''
})

const tableData = ref([])
const summary = reactive({
  todayCount: 0,
  weekCount: 0,
  monthCount: 0,
  avgDuration: 0
})

const lineChartRef = ref(null)
const barChartRef = ref(null)
let lineChartInstance = null
let barChartInstance = null

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const initLineChart = () => {
  if (!lineChartRef.value) return
  lineChartInstance = echarts.init(lineChartRef.value)
  const dates = [...new Set(tableData.value.map(item => item.date))]
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['门诊人数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '门诊人数',
        type: 'line',
        smooth: true,
        data: dates.map(date => {
          const item = tableData.value.find(i => i.date === date)
          return item ? item.count : 0
        }),
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      }
    ]
  }
  lineChartInstance.setOption(option)
}

const initBarChart = () => {
  if (!barChartRef.value) return
  barChartInstance = echarts.init(barChartRef.value)
  const deptMap = {}
  tableData.value.forEach(item => {
    if (!deptMap[item.department]) {
      deptMap[item.department] = 0
    }
    deptMap[item.department] += item.count
  })
  const departments = Object.keys(deptMap)
  const counts = departments.map(dept => deptMap[dept])
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: departments
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '门诊人数',
        type: 'bar',
        data: counts,
        itemStyle: {
          color: function(params) {
            const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
            return colors[params.dataIndex % colors.length]
          }
        }
      }
    ]
  }
  barChartInstance.setOption(option)
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
  if (queryForm.doctorId) {
    params.doctorId = queryForm.doctorId
  }
  
  const [reportRes, summaryRes] = await Promise.all([
    getOutpatientReport(params),
    getOutpatientSummary(params)
  ])
  
  tableData.value = reportRes.data || []
  Object.assign(summary, summaryRes.data || {})
  
  nextTick(() => {
    initLineChart()
    initBarChart()
  })
}

const handleReset = () => {
  queryForm.dateRange = []
  queryForm.department = ''
  queryForm.doctorId = ''
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
  if (queryForm.doctorId) {
    params.doctorId = queryForm.doctorId
  }
  const res = await exportOutpatientReport(params)
  const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '门诊量统计.xlsx'
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const handlePrint = () => {
  window.print()
}

const handleResize = () => {
  lineChartInstance && lineChartInstance.resize()
  barChartInstance && barChartInstance.resize()
}

onMounted(() => {
  handleQuery()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  lineChartInstance && lineChartInstance.dispose()
  barChartInstance && barChartInstance.dispose()
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
