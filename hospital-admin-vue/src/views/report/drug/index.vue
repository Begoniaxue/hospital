<template>
  <div class="page-container">
    <div class="page-header">
      <h3>药品进销存统计</h3>
      <div>
        <el-button @click="handleExport" v-if="hasPermission('report:drug:export')">
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
      <el-form-item label="药品分类">
        <el-select v-model="queryForm.category" placeholder="请选择" clearable style="width: 150px;">
          <el-option label="抗生素" value="抗生素" />
          <el-option label="解热镇痛" value="解热镇痛" />
          <el-option label="降压药" value="降压药" />
          <el-option label="降糖药" value="降糖药" />
          <el-option label="消化系统" value="消化系统" />
          <el-option label="其他" value="其他" />
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
            <el-icon><Box /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">期初库存</div>
            <div class="stat-value">{{ summary.beginStock || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #67C23A;">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">入库数量</div>
            <div class="stat-value">{{ summary.stockIn || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #E6A23C;">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">出库数量</div>
            <div class="stat-value">{{ summary.stockOut || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: #F56C6C;">
            <el-icon><Wallet /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-title">期末库存</div>
            <div class="stat-value">{{ summary.endStock || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-bottom: 20px;">
      <template #header>
        <span>库存趋势</span>
      </template>
      <div ref="chartRef" style="height: 350px;"></div>
    </el-card>

    <el-table :data="tableData" border style="width: 100%;">
      <el-table-column prop="drugCode" label="药品编码" width="120" />
      <el-table-column prop="drugName" label="药品名称" width="150" />
      <el-table-column prop="specification" label="规格" width="120" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="beginStock" label="期初库存" width="100" />
      <el-table-column prop="stockIn" label="入库数量" width="100">
        <template #default="scope">
          <span style="color: #67C23A;">+{{ scope.row.stockIn || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="stockOut" label="出库数量" width="100">
        <template #default="scope">
          <span style="color: #F56C6C;">-{{ scope.row.stockOut || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="endStock" label="期末库存" width="100">
        <template #default="scope">
          <span :style="{ color: (scope.row.endStock || 0) < 10 ? 'red' : '' }">
            {{ scope.row.endStock || 0 }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="stockInAmount" label="入库金额" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.stockInAmount || 0).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="stockOutAmount" label="出库金额" width="120">
        <template #default="scope">
          ¥{{ Number(scope.row.stockOutAmount || 0).toFixed(2) }}
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
  getDrugStockReport,
  getDrugStockSummary,
  exportDrugStockReport
} from '@/api/report'

const userStore = useUserStore()

const queryForm = reactive({
  dateRange: [],
  category: ''
})

const tableData = ref([])
const summary = reactive({
  beginStock: 0,
  stockIn: 0,
  stockOut: 0,
  endStock: 0
})

const chartRef = ref(null)
let chartInstance = null

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  
  const dates = [...new Set(tableData.value.map(item => item.date))].slice(0, 30)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['入库数量', '出库数量', '期末库存']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '数量'
    },
    series: [
      {
        name: '入库数量',
        type: 'bar',
        data: dates.map(date => {
          const items = tableData.value.filter(i => i.date === date)
          return items.reduce((sum, item) => sum + (item.stockIn || 0), 0)
        }),
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '出库数量',
        type: 'bar',
        data: dates.map(date => {
          const items = tableData.value.filter(i => i.date === date)
          return items.reduce((sum, item) => sum + (item.stockOut || 0), 0)
        }),
        itemStyle: {
          color: '#F56C6C'
        }
      },
      {
        name: '期末库存',
        type: 'line',
        data: dates.map(date => {
          const items = tableData.value.filter(i => i.date === date)
          return items.reduce((sum, item) => sum + (item.endStock || 0), 0)
        }),
        itemStyle: {
          color: '#409EFF'
        },
        lineStyle: {
          width: 3
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
  if (queryForm.category) {
    params.category = queryForm.category
  }
  
  const [reportRes, summaryRes] = await Promise.all([
    getDrugStockReport(params),
    getDrugStockSummary(params)
  ])
  
  tableData.value = reportRes.data || []
  Object.assign(summary, summaryRes.data || {})
  
  nextTick(() => {
    initChart()
  })
}

const handleReset = () => {
  queryForm.dateRange = []
  queryForm.category = ''
  handleQuery()
}

const handleExport = async () => {
  const params = {}
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.startDate = queryForm.dateRange[0]
    params.endDate = queryForm.dateRange[1]
  }
  if (queryForm.category) {
    params.category = queryForm.category
  }
  const res = await exportDrugStockReport(params)
  const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = '药品进销存统计.xlsx'
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
