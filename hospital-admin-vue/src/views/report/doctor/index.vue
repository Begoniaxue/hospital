<template>
  <div class="page-container">
    <div class="page-header">
      <h3>医生接诊量统计</h3>
      <div>
        <el-button @click="handleExport" v-if="hasPermission('report:doctor:export')">
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
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>医生接诊量排名</span>
          </template>
          <div ref="chartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column type="index" label="排名" width="80" align="center">
        <template #default="scope">
          <el-tag v-if="scope.$index === 0" type="danger" effect="dark">第1名</el-tag>
          <el-tag v-else-if="scope.$index === 1" type="warning" effect="dark">第2名</el-tag>
          <el-tag v-else-if="scope.$index === 2" type="success" effect="dark">第3名</el-tag>
          <span v-else>第{{ scope.$index + 1 }}名</span>
        </template>
      </el-table-column>
      <el-table-column prop="doctorName" label="医生姓名" width="120" />
      <el-table-column prop="department" label="所属科室" width="120" />
      <el-table-column prop="registrationCount" label="挂号人数" width="120" />
      <el-table-column prop="outpatientCount" label="门诊人数" width="120" />
      <el-table-column prop="inpatientCount" label="住院人数" width="120" />
      <el-table-column prop="totalCount" label="总接诊量" width="120" sortable>
        <template #default="scope">
          <span style="font-weight: bold; color: #409EFF;">{{ scope.row.totalCount }}</span>
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
  getDoctorWorkloadReport,
  exportDoctorWorkloadReport
} from '@/api/report'

const userStore = useUserStore()

const queryForm = reactive({
  dateRange: [],
  department: ''
})

const tableData = ref([])
const chartRef = ref(null)
let chartInstance = null

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  
  const sortedData = [...tableData.value].sort((a, b) => b.totalCount - a.totalCount).slice(0, 10)
  const names = sortedData.map(item => item.doctorName).reverse()
  const counts = sortedData.map(item => item.totalCount).reverse()
  
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
      type: 'value',
      name: '接诊人数'
    },
    yAxis: {
      type: 'category',
      data: names
    },
    series: [
      {
        name: '接诊人数',
        type: 'bar',
        data: counts,
        itemStyle: {
          color: function(params) {
            const colors = ['#F56C6C', '#E6A23C', '#67C23A', '#409EFF', '#909399']
            return colors[params.dataIndex % colors.length]
          }
        },
        label: {
          show: true,
          position: 'right'
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
  
  const res = await getDoctorWorkloadReport(params)
  tableData.value = (res.data || []).sort((a, b) => b.totalCount - a.totalCount)
  
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
  const res = await exportDoctorWorkloadReport(params)
  const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '医生接诊量统计.xlsx'
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
