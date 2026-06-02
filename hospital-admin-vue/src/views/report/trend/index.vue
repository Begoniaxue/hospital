<template>
  <div class="page-container">
    <div class="page-header">
      <h3>患者就诊趋势图</h3>
      <div>
        <el-button @click="handleExport" v-if="hasPermission('report:trend:export')">
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
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <el-icon><Tickets /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总挂号量</div>
            <div class="stat-value">{{ summary.registrationCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总门诊量</div>
            <div class="stat-value">{{ summary.outpatientCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总住院量</div>
            <div class="stat-value">{{ summary.inpatientCount || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-bottom: 20px;">
      <template #header>
        <span>患者就诊趋势</span>
      </template>
      <div ref="chartRef" style="height: 450px;"></div>
    </el-card>

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="date" label="日期" width="150" />
      <el-table-column prop="registrationCount" label="挂号量" width="120">
        <template #default="scope">
          <el-tag type="primary" size="small">{{ scope.row.registrationCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="outpatientCount" label="门诊量" width="120">
        <template #default="scope">
          <el-tag type="success" size="small">{{ scope.row.outpatientCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inpatientCount" label="住院量" width="120">
        <template #default="scope">
          <el-tag type="warning" size="small">{{ scope.row.inpatientCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="totalCount" label="合计" width="120">
        <template #default="scope">
          <span style="font-weight: bold; font-size: 16px;">{{ scope.row.totalCount || 0 }}</span>
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
  getPatientTrendReport,
  exportPatientTrendReport
} from '@/api/report'

const userStore = useUserStore()

const queryForm = reactive({
  dateRange: []
})

const tableData = ref([])
const summary = reactive({
  registrationCount: 0,
  outpatientCount: 0,
  inpatientCount: 0,
  totalCount: 0
})

const chartRef = ref(null)
let chartInstance = null

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  
  const dates = tableData.value.map(item => item.date)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['挂号量', '门诊量', '住院量', '总量'],
      top: 10
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
      data: dates,
      axisLabel: {
        rotate: 30
      }
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '挂号量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: tableData.value.map(item => item.registrationCount || 0),
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: '门诊量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: tableData.value.map(item => item.outpatientCount || 0),
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        }
      },
      {
        name: '住院量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: tableData.value.map(item => item.inpatientCount || 0),
        itemStyle: {
          color: '#E6A23C'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
            { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
          ])
        }
      },
      {
        name: '总量',
        type: 'line',
        smooth: true,
        symbol: 'diamond',
        symbolSize: 10,
        lineStyle: {
          width: 3,
          type: 'dashed'
        },
        data: tableData.value.map(item => item.totalCount || 0),
        itemStyle: {
          color: '#F56C6C'
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
  
  const res = await getPatientTrendReport(params)
  tableData.value = res.data || []
  
  summary.registrationCount = tableData.value.reduce((sum, item) => sum + (item.registrationCount || 0), 0)
  summary.outpatientCount = tableData.value.reduce((sum, item) => sum + (item.outpatientCount || 0), 0)
  summary.inpatientCount = tableData.value.reduce((sum, item) => sum + (item.inpatientCount || 0), 0)
  summary.totalCount = tableData.value.reduce((sum, item) => sum + (item.totalCount || 0), 0)
  
  nextTick(() => {
    initChart()
  })
}

const handleReset = () => {
  queryForm.dateRange = []
  handleQuery()
}

const handleExport = async () => {
  const params = {}
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.startDate = queryForm.dateRange[0]
    params.endDate = queryForm.dateRange[1]
  }
  const res = await exportPatientTrendReport(params)
  const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '患者就诊趋势.xlsx'
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
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
</style>
