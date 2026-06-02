<template>
  <div class="page-container">
    <div class="page-header">
      <h3>营收统计</h3>
      <div>
        <el-button @click="handleExport" v-if="hasPermission('report:revenue:export')">
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
      <el-form-item label="统计维度">
        <el-select v-model="queryForm.dimension" placeholder="请选择" style="width: 120px;">
          <el-option label="按日" value="day" />
          <el-option label="按月" value="month" />
          <el-option label="按科室" value="department" />
          <el-option label="按医生" value="doctor" />
        </el-select>
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
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">总营收</div>
            <div class="stat-value">¥{{ (summary.totalAmount || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <el-icon><Tickets /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">挂号费</div>
            <div class="stat-value">¥{{ (summary.registrationFee || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <el-icon><MedicineBox /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">药品费</div>
            <div class="stat-value">¥{{ (summary.drugFee || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
            <el-icon><DataAnalysis /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">检查费</div>
            <div class="stat-value">¥{{ (summary.examFee || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <el-icon><FirstAidKit /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">治疗费</div>
            <div class="stat-value">¥{{ (summary.treatmentFee || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">住院费</div>
            <div class="stat-value">¥{{ (summary.inpatientFee || 0).toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-bottom: 20px;">
      <template #header>
        <span>营收趋势</span>
      </template>
      <div ref="chartRef" style="height: 400px;"></div>
    </el-card>

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="date" label="日期/科室/医生" width="150" />
      <el-table-column prop="registrationFee" label="挂号费" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.registrationFee).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="drugFee" label="药品费" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.drugFee).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="examFee" label="检查费" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.examFee).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="treatmentFee" label="治疗费" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.treatmentFee).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="inpatientFee" label="住院费" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.inpatientFee).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="otherFee" label="其他费用" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.otherFee).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="合计" width="140">
        <template #default="scope">
          <span style="font-weight: bold; color: #F56C6C;">¥{{ Number(scope.row.totalAmount).toFixed(2) }}</span>
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
  getRevenueReport,
  getRevenueSummary,
  exportRevenueReport
} from '@/api/report'

const userStore = useUserStore()

const queryForm = reactive({
  dateRange: [],
  dimension: 'day',
  department: ''
})

const tableData = ref([])
const summary = reactive({
  totalAmount: 0,
  registrationFee: 0,
  drugFee: 0,
  examFee: 0,
  treatmentFee: 0,
  inpatientFee: 0,
  otherFee: 0
})

const chartRef = ref(null)
let chartInstance = null

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  
  const dates = tableData.value.map(item => item.date || item.department || item.doctorName)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    legend: {
      data: ['挂号费', '药品费', '检查费', '治疗费', '住院费', '其他费用']
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
      name: '金额(元)'
    },
    series: [
      {
        name: '挂号费',
        type: 'line',
        stack: 'Total',
        smooth: true,
        areaStyle: {},
        data: tableData.value.map(item => item.registrationFee || 0)
      },
      {
        name: '药品费',
        type: 'line',
        stack: 'Total',
        smooth: true,
        areaStyle: {},
        data: tableData.value.map(item => item.drugFee || 0)
      },
      {
        name: '检查费',
        type: 'line',
        stack: 'Total',
        smooth: true,
        areaStyle: {},
        data: tableData.value.map(item => item.examFee || 0)
      },
      {
        name: '治疗费',
        type: 'line',
        stack: 'Total',
        smooth: true,
        areaStyle: {},
        data: tableData.value.map(item => item.treatmentFee || 0)
      },
      {
        name: '住院费',
        type: 'line',
        stack: 'Total',
        smooth: true,
        areaStyle: {},
        data: tableData.value.map(item => item.inpatientFee || 0)
      },
      {
        name: '其他费用',
        type: 'line',
        stack: 'Total',
        smooth: true,
        label: {
          show: true,
          position: 'top'
        },
        areaStyle: {},
        data: tableData.value.map(item => item.otherFee || 0)
      }
    ]
  }
  chartInstance.setOption(option)
}

const handleQuery = async () => {
  const params = {
    dimension: queryForm.dimension
  }
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.startDate = queryForm.dateRange[0]
    params.endDate = queryForm.dateRange[1]
  }
  if (queryForm.department) {
    params.department = queryForm.department
  }
  
  const [reportRes, summaryRes] = await Promise.all([
    getRevenueReport(params),
    getRevenueSummary(params)
  ])
  
  tableData.value = reportRes.data || []
  Object.assign(summary, summaryRes.data || {})
  
  nextTick(() => {
    initChart()
  })
}

const handleReset = () => {
  queryForm.dateRange = []
  queryForm.dimension = 'day'
  queryForm.department = ''
  handleQuery()
}

const handleExport = async () => {
  const params = {
    dimension: queryForm.dimension
  }
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.startDate = queryForm.dateRange[0]
    params.endDate = queryForm.dateRange[1]
  }
  if (queryForm.department) {
    params.department = queryForm.department
  }
  const res = await exportRevenueReport(params)
  const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '营收统计.xlsx'
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
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-right: 12px;
}
.stat-content {
  flex: 1;
}
.stat-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 3px;
}
.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}
</style>
