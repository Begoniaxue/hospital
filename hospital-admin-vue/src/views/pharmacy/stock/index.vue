<template>
  <div class="page-container">
    <div class="page-header">
      <h3>库存管理</h3>
      <div>
        <el-button type="primary" @click="handleStockIn" v-if="hasPermission('pharmacy:stock:in')">
          <el-icon><Plus /></el-icon>
          入库登记
        </el-button>
        <el-button type="warning" @click="handleStockOut" v-if="hasPermission('pharmacy:stock:out')">
          <el-icon><Minus /></el-icon>
          出库登记
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="药品">
        <el-select v-model="queryForm.drugId" placeholder="请选择药品" clearable filterable style="width: 220px;">
          <el-option
            v-for="item in drugList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="queryForm.type" placeholder="请选择" clearable>
          <el-option label="入库" :value="1" />
          <el-option label="出库" :value="2" />
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
      <el-table-column prop="drugName" label="药品名称" width="160" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.type === 1 ? 'success' : 'danger'">
            {{ scope.row.type === 1 ? '入库' : '出库' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="100" />
      <el-table-column prop="beforeStock" label="变动前库存" width="120" />
      <el-table-column prop="afterStock" label="变动后库存" width="120" />
      <el-table-column prop="reason" label="原因" show-overflow-tooltip />
      <el-table-column prop="operatorName" label="操作人" width="100" />
      <el-table-column prop="createTime" label="操作时间" width="180" />
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

  <el-dialog v-model="stockDialogVisible" :title="stockDialogTitle" width="500px">
    <el-form ref="stockFormRef" :model="stockForm" :rules="stockRules" label-width="100px">
      <el-form-item label="药品" prop="drugId">
        <el-select v-model="stockForm.drugId" placeholder="请选择药品" filterable style="width: 100%;">
          <el-option
            v-for="item in drugList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数量" prop="quantity">
        <el-input-number v-model="stockForm.quantity" :min="1" style="width: 100%;" />
      </el-form-item>
      <el-form-item label="原因" prop="reason">
        <el-input v-model="stockForm.reason" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="stockDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleStockSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getStockLogPage,
  stockIn,
  stockOut,
  getDrugPage
} from '@/api/drug'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  drugId: '',
  type: ''
})

const dateRange = ref([])
const tableData = ref([])
const total = ref(0)
const drugList = ref([])
const stockDialogVisible = ref(false)
const stockFormRef = ref(null)
const stockDialogTitle = ref('')
const stockType = ref(1)

const stockForm = reactive({
  drugId: '',
  quantity: 1,
  reason: ''
})

const stockRules = {
  drugId: [{ required: true, message: '请选择药品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入原因', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const loadDrugList = async () => {
  const res = await getDrugPage({ pageNum: 1, pageSize: 1000 })
  drugList.value = res.data.records
}

const handleQuery = async () => {
  const params = { ...queryForm }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startTime = dateRange.value[0]
    params.endTime = dateRange.value[1]
  }
  const res = await getStockLogPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.drugId = ''
  queryForm.type = ''
  queryForm.pageNum = 1
  dateRange.value = []
  handleQuery()
}

const handleStockIn = () => {
  stockType.value = 1
  stockDialogTitle.value = '入库登记'
  Object.assign(stockForm, {
    drugId: '',
    quantity: 1,
    reason: ''
  })
  stockDialogVisible.value = true
}

const handleStockOut = () => {
  stockType.value = 2
  stockDialogTitle.value = '出库登记'
  Object.assign(stockForm, {
    drugId: '',
    quantity: 1,
    reason: ''
  })
  stockDialogVisible.value = true
}

const handleStockSubmit = async () => {
  if (!stockFormRef.value) return
  await stockFormRef.value.validate(async (valid) => {
    if (valid) {
      const data = {
        drugId: stockForm.drugId,
        type: stockType.value,
        quantity: stockForm.quantity,
        reason: stockForm.reason
      }
      if (stockType.value === 1) {
        await stockIn(data)
        ElMessage.success('入库成功')
      } else {
        await stockOut(data)
        ElMessage.success('出库成功')
      }
      stockDialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  loadDrugList()
  handleQuery()
})
</script>
