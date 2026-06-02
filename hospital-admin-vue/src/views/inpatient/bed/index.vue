<template>
  <div class="page-container">
    <div class="page-header">
      <h3>床位分配</h3>
      <div>
        <el-button type="primary" @click="handleAssign" v-if="hasPermission('inpatient:bed:assign')">
          <el-icon><Plus /></el-icon>
          分配床位
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="床位号">
        <el-input v-model="queryForm.bedNo" placeholder="请输入床位号" clearable />
      </el-form-item>
      <el-form-item label="病房">
        <el-select v-model="queryForm.wardName" placeholder="请选择" clearable style="width: 150px;">
          <el-option label="内科病房" value="内科病房" />
          <el-option label="外科病房" value="外科病房" />
          <el-option label="儿科病房" value="儿科病房" />
          <el-option label="ICU病房" value="ICU病房" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择" clearable>
          <el-option label="空闲" :value="0" />
          <el-option label="已占用" :value="1" />
          <el-option label="维修中" :value="2" />
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
      <el-table-column prop="bedNo" label="床位号" width="120" />
      <el-table-column prop="wardName" label="病房" width="120" />
      <el-table-column prop="bedType" label="床位类型" width="120" />
      <el-table-column prop="price" label="价格(元/天)" width="120">
        <template #default="scope">
          {{ Number(scope.row.price).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="patientName" label="当前患者" width="120" />
      <el-table-column prop="inpatientNo" label="住院号" width="140" />
      <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              link
              type="primary"
              @click="handleAssign(scope.row)"
              v-if="scope.row.status === 0 && hasPermission('inpatient:bed:assign')"
            >
              分配
            </el-button>
            <el-button
              size="small"
              link
              type="warning"
              @click="handleRelease(scope.row)"
              v-if="scope.row.status === 1 && hasPermission('inpatient:bed:release')"
            >
              释放
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

  <el-dialog v-model="dialogVisible" title="分配床位" width="600px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="床位号" prop="bedId">
        <el-select v-model="form.bedId" placeholder="请选择床位" style="width: 100%;">
          <el-option
            v-for="bed in freeBeds"
            :key="bed.id"
            :label="bed.bedNo + ' - ' + bed.wardName"
            :value="bed.id"
          />
        </el-select>
      </el-form-item>
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
  getBedPage,
  getBedList,
  assignBed,
  releaseBed
} from '@/api/inpatient'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  bedNo: '',
  wardName: '',
  status: ''
})

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const freeBeds = ref([])
const inpatients = ref([])

const form = reactive({
  bedId: null,
  inpatientId: null
})

const rules = {
  bedId: [{ required: true, message: '请选择床位', trigger: 'change' }],
  inpatientId: [{ required: true, message: '请选择住院患者', trigger: 'change' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const getStatusType = (status) => {
  const types = ['success', 'danger', 'warning']
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = ['空闲', '已占用', '维修中']
  return texts[status] || '未知'
}

const handleQuery = async () => {
  const params = { ...queryForm }
  const res = await getBedPage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.bedNo = ''
  queryForm.wardName = ''
  queryForm.status = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleAssign = async (row) => {
  const [bedRes, inpatientRes] = await Promise.all([
    getBedList({ status: 0 }),
    getBedList({ status: 0 })
  ])
  freeBeds.value = bedRes.data
  inpatients.value = inpatientRes.data || []
  if (row && row.id) {
    form.bedId = row.id
  } else {
    form.bedId = null
  }
  form.inpatientId = null
  dialogVisible.value = true
}

const handleRelease = (row) => {
  ElMessageBox.confirm('确定要释放该床位吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await releaseBed(row.id)
    ElMessage.success('释放成功')
    handleQuery()
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await assignBed(form)
      ElMessage.success('分配成功')
      dialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  handleQuery()
})
</script>
