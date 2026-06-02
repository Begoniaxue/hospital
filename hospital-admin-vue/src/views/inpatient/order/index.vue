<template>
  <div class="page-container">
    <div class="page-header">
      <h3>住院医嘱</h3>
      <div>
        <el-button type="primary" @click="handleAdd" v-if="hasPermission('inpatient:order:add')">
          <el-icon><Plus /></el-icon>
          新增医嘱
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="患者姓名">
        <el-input v-model="queryForm.patientName" placeholder="请输入患者姓名" clearable />
      </el-form-item>
      <el-form-item label="住院号">
        <el-input v-model="queryForm.inpatientNo" placeholder="请输入住院号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择" clearable>
          <el-option label="待执行" :value="0" />
          <el-option label="已执行" :value="1" />
          <el-option label="已取消" :value="2" />
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
      <el-table-column prop="orderNo" label="医嘱单号" width="180" />
      <el-table-column prop="inpatientNo" label="住院号" width="140" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="doctorName" label="医生" width="100" />
      <el-table-column prop="orderType" label="医嘱类型" width="100" />
      <el-table-column prop="orderContent" label="医嘱内容" show-overflow-tooltip />
      <el-table-column prop="orderTime" label="开嘱时间" width="180" />
      <el-table-column prop="executeUserName" label="执行人" width="100" />
      <el-table-column prop="executeTime" label="执行时间" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              link
              type="success"
              @click="handleExecute(scope.row)"
              v-if="scope.row.status === 0 && hasPermission('inpatient:order:execute')"
            >
              执行
            </el-button>
            <el-button
              size="small"
              link
              type="danger"
              @click="handleCancel(scope.row)"
              v-if="scope.row.status === 0 && hasPermission('inpatient:order:cancel')"
            >
              取消
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

  <el-dialog v-model="dialogVisible" title="新增医嘱" width="600px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="住院号" prop="inpatientId">
        <el-select v-model="form.inpatientId" placeholder="请选择住院患者" style="width: 100%;" filterable>
          <el-option
            v-for="inpatient in inpatients"
            :key="inpatient.id"
            :label="inpatient.inpatientNo + ' - ' + inpatient.patientName"
            :value="inpatient.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="医嘱类型" prop="orderType">
        <el-select v-model="form.orderType" placeholder="请选择" style="width: 100%;">
          <el-option label="药品医嘱" value="药品医嘱" />
          <el-option label="检查医嘱" value="检查医嘱" />
          <el-option label="检验医嘱" value="检验医嘱" />
          <el-option label="治疗医嘱" value="治疗医嘱" />
          <el-option label="护理医嘱" value="护理医嘱" />
          <el-option label="饮食医嘱" value="饮食医嘱" />
          <el-option label="其他医嘱" value="其他医嘱" />
        </el-select>
      </el-form-item>
      <el-form-item label="医嘱内容" prop="orderContent">
        <el-input v-model="form.orderContent" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getOrderPage,
  addOrder,
  executeOrder,
  cancelOrder
} from '@/api/inpatient'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  patientName: '',
  inpatientNo: '',
  status: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const inpatients = ref([])

const form = reactive({
  inpatientId: null,
  orderType: '',
  orderContent: '',
  remark: ''
})

const rules = {
  inpatientId: [{ required: true, message: '请选择住院患者', trigger: 'change' }],
  orderType: [{ required: true, message: '请选择医嘱类型', trigger: 'change' }],
  orderContent: [{ required: true, message: '请输入医嘱内容', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const getStatusType = (status) => {
  const types = ['warning', 'success', 'info']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['待执行', '已执行', '已取消']
  return texts[status] || '未知'
}

const handleQuery = async () => {
  const params = { ...queryForm }
  const res = await getOrderPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.patientName = ''
  queryForm.inpatientNo = ''
  queryForm.status = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  inpatients.value = []
  Object.assign(form, {
    inpatientId: null,
    orderType: '',
    orderContent: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleExecute = (row) => {
  ElMessageBox.confirm('确定要执行该医嘱吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await executeOrder(row.id)
    ElMessage.success('执行成功')
    handleQuery()
  })
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该医嘱吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await cancelOrder(row.id)
    ElMessage.success('取消成功')
    handleQuery()
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await addOrder(form)
      ElMessage.success('新增成功')
      dialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  handleQuery()
})
</script>
