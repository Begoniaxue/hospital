<template>
  <div class="page-container">
    <div class="page-header">
      <h3>结算报表</h3>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="日结" name="daily">
        <el-form :inline="true" class="search-form">
          <el-form-item label="日期">
            <el-date-picker
              v-model="dailyDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleDailyQuery">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
          </el-form-item>
        </el-form>

        <el-descriptions title="日结统计" :column="2" border v-if="dailyData">
          <el-descriptions-item label="总笔数">{{ dailyData.totalCount }}</el-descriptions-item>
          <el-descriptions-item label="总金额">{{ dailyData.totalAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="挂号费合计">{{ dailyData.registrationFeeTotal?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="药品费合计">{{ dailyData.drugFeeTotal?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="检查费合计">{{ dailyData.examFeeTotal?.toFixed(2) }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="汇总" :column="1" border v-if="dailyData" style="margin-top: 20px;">
          <el-descriptions-item label="日期">{{ dailyDate }}</el-descriptions-item>
          <el-descriptions-item label="其他费合计">{{ dailyData.otherFeeTotal?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额合计">{{ dailyData.discountAmountTotal?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="实收金额合计">
            <span style="color: #f56c6c; font-weight: bold;">{{ dailyData.actualAmountTotal?.toFixed(2) }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>

      <el-tab-pane label="月结" name="monthly">
        <el-form :inline="true" class="search-form">
          <el-form-item label="月份">
            <el-date-picker
              v-model="monthlyDate"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleMonthlyQuery">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
          </el-form-item>
        </el-form>

        <el-descriptions title="月结统计" :column="2" border v-if="monthlyData">
          <el-descriptions-item label="总笔数">{{ monthlyData.totalCount }}</el-descriptions-item>
          <el-descriptions-item label="总金额">{{ monthlyData.totalAmount?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="挂号费合计">{{ monthlyData.registrationFeeTotal?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="药品费合计">{{ monthlyData.drugFeeTotal?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="检查费合计">{{ monthlyData.examFeeTotal?.toFixed(2) }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="汇总" :column="1" border v-if="monthlyData" style="margin-top: 20px;">
          <el-descriptions-item label="月份">{{ monthlyDate }}</el-descriptions-item>
          <el-descriptions-item label="其他费合计">{{ monthlyData.otherFeeTotal?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="优惠金额合计">{{ monthlyData.discountAmountTotal?.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="实收金额合计">
            <span style="color: #f56c6c; font-weight: bold;">{{ monthlyData.actualAmountTotal?.toFixed(2) }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getDailySettlement,
  getMonthlySettlement
} from '@/api/settlement'

const activeTab = ref('daily')
const dailyDate = ref('')
const monthlyDate = ref('')
const dailyData = ref(null)
const monthlyData = ref(null)

const handleDailyQuery = async () => {
  if (!dailyDate.value) {
    ElMessage.warning('请选择日期')
    return
  }
  const res = await getDailySettlement(dailyDate.value)
  dailyData.value = res.data
}

const handleMonthlyQuery = async () => {
  if (!monthlyDate.value) {
    ElMessage.warning('请选择月份')
    return
  }
  const res = await getMonthlySettlement(monthlyDate.value)
  monthlyData.value = res.data
}

onMounted(() => {})
</script>
