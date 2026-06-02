<template>
  <div class="page-container">
    <div class="page-header">
      <h3>药品档案管理</h3>
      <div>
        <el-button @click="handleWarning">
          库存预警
        </el-button>
        <el-button @click="handleExpired">
          过期药品
        </el-button>
        <el-button type="primary" @click="handleAdd" v-if="hasPermission('pharmacy:drug:add')">
          <el-icon><Plus /></el-icon>
          新增药品
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="关键词">
        <el-input v-model="queryForm.keyword" placeholder="药品名称/编码/厂家" clearable />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="queryForm.category" placeholder="请选择" clearable>
          <el-option label="抗生素" value="抗生素" />
          <el-option label="解热镇痛" value="解热镇痛" />
          <el-option label="降压药" value="降压药" />
          <el-option label="降糖药" value="降糖药" />
          <el-option label="消化系统" value="消化系统" />
          <el-option label="抗过敏" value="抗过敏" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
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
      <el-table-column prop="drugCode" label="药品编码" width="120" />
      <el-table-column prop="name" label="药品名称" width="140" />
      <el-table-column prop="specification" label="规格" width="120" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="price" label="单价" width="100">
        <template #default="scope">
          {{ Number(scope.row.price).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100">
        <template #default="scope">
          <span :style="{ color: scope.row.stock < scope.row.stockThreshold ? 'red' : '' }">
            {{ scope.row.stock }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="stockThreshold" label="库存阈值" width="100" />
      <el-table-column prop="manufacturer" label="厂家" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="expiryDate" label="有效期" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              link
              type="primary"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              size="small"
              link
              type="primary"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('pharmacy:drug:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              link
              type="danger"
              @click="handleDelete(scope.row)"
              v-if="hasPermission('pharmacy:drug:delete')"
            >
              删除
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

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" :disabled="isView">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="药品名称" prop="name">
            <el-input v-model="form.name" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="规格">
            <el-input v-model="form.specification" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="单位">
            <el-input v-model="form.unit" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单价" prop="price">
            <el-input-number v-model="form.price" :min="0" :precision="2" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="库存">
            <el-input-number v-model="form.stock" :min="0" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="库存阈值">
            <el-input-number v-model="form.stockThreshold" :min="0" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="厂家">
            <el-input v-model="form.manufacturer" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="分类">
            <el-select v-model="form.category" placeholder="请选择">
              <el-option label="抗生素" value="抗生素" />
              <el-option label="解热镇痛" value="解热镇痛" />
              <el-option label="降压药" value="降压药" />
              <el-option label="降糖药" value="降糖药" />
              <el-option label="消化系统" value="消化系统" />
              <el-option label="抗过敏" value="抗过敏" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="有效期">
            <el-date-picker v-model="form.expiryDate" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio :value="1">启用</el-radio>
              <el-radio :value="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleSubmit" v-if="!isView">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="warningDialogVisible" title="库存预警" width="800px">
    <el-table :data="warningData" border style="width: 100%;">
      <el-table-column prop="drugCode" label="药品编码" />
      <el-table-column prop="name" label="药品名称" />
      <el-table-column prop="stock" label="当前库存" />
      <el-table-column prop="stockThreshold" label="库存阈值" />
      <el-table-column prop="manufacturer" label="厂家" />
    </el-table>
  </el-dialog>

  <el-dialog v-model="expiredDialogVisible" title="过期药品" width="800px">
    <el-table :data="expiredData" border style="width: 100%;">
      <el-table-column prop="drugCode" label="药品编码" />
      <el-table-column prop="name" label="药品名称" />
      <el-table-column prop="expiryDate" label="有效期" />
      <el-table-column prop="stock" label="库存" />
      <el-table-column prop="manufacturer" label="厂家" />
    </el-table>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getDrugPage,
  getDrug,
  addDrug,
  updateDrug,
  deleteDrug,
  getWarningDrugs,
  getExpiredDrugs
} from '@/api/drug'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  category: '',
  status: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isView = ref(false)
const dialogTitle = ref('')
const warningDialogVisible = ref(false)
const warningData = ref([])
const expiredDialogVisible = ref(false)
const expiredData = ref([])

const form = reactive({
  id: null,
  name: '',
  specification: '',
  unit: '',
  price: null,
  stock: 0,
  stockThreshold: 0,
  manufacturer: '',
  category: '',
  expiryDate: '',
  status: 1,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const handleQuery = async () => {
  const params = { ...queryForm }
  const res = await getDrugPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.category = ''
  queryForm.status = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增药品'
  Object.assign(form, {
    id: null,
    name: '',
    specification: '',
    unit: '',
    price: null,
    stock: 0,
    stockThreshold: 0,
    manufacturer: '',
    category: '',
    expiryDate: '',
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '药品详情'
  const res = await getDrug(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isView.value = false
  dialogTitle.value = '编辑药品'
  const res = await getDrug(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateDrug(form)
        ElMessage.success('修改成功')
      } else {
        await addDrug(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该药品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDrug(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleWarning = async () => {
  const res = await getWarningDrugs()
  warningData.value = res.data
  warningDialogVisible.value = true
}

const handleExpired = async () => {
  const res = await getExpiredDrugs()
  expiredData.value = res.data
  expiredDialogVisible.value = true
}

onMounted(() => {
  handleQuery()
})
</script>
