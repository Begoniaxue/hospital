<template>
  <div class="page-container">
    <div class="page-header">
      <h3>住院档案</h3>
      <div>
        <el-button type="primary" @click="handleArchive" v-if="hasPermission('inpatient:archive:archive')">
          <el-icon><Plus /></el-icon>
          归档
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
      <el-form-item label="归档状态">
        <el-select v-model="queryForm.archiveStatus" placeholder="请选择" clearable>
          <el-option label="未归档" :value="0" />
          <el-option label="已归档" :value="1" />
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
      <el-table-column prop="archiveNo" label="档案编号" width="180" />
      <el-table-column prop="inpatientNo" label="住院号" width="140" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="archiveStatus" label="归档状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.archiveStatus === 1 ? 'success' : 'warning'">
            {{ scope.row.archiveStatus === 1 ? '已归档' : '未归档' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="archiveTime" label="归档时间" width="180" />
      <el-table-column prop="archiveUserName" label="归档人" width="100" />
      <el-table-column prop="borrowStatus" label="借阅状态" width="100">
        <template #default="scope">
          <el-tag :type="getBorrowType(scope.row.borrowStatus)">
            {{ getBorrowText(scope.row.borrowStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="borrowUserName" label="借阅人" width="100" />
      <el-table-column prop="borrowTime" label="借阅时间" width="180" />
      <el-table-column prop="expectedReturnTime" label="预计归还" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              link
              type="primary"
              @click="handleArchive(scope.row)"
              v-if="scope.row.archiveStatus === 0 && hasPermission('inpatient:archive:archive')"
            >
              归档
            </el-button>
            <el-button
              size="small"
              link
              type="primary"
              @click="handleBorrow(scope.row)"
              v-if="scope.row.archiveStatus === 1 && scope.row.borrowStatus === 0 && hasPermission('inpatient:archive:borrow')"
            >
              借阅
            </el-button>
            <el-button
              size="small"
              link
              type="warning"
              @click="handleReturn(scope.row)"
              v-if="scope.row.borrowStatus === 1 && hasPermission('inpatient:archive:return')"
            >
              归还
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

  <el-dialog v-model="archiveDialogVisible" title="归档" width="500px">
    <el-form ref="archiveFormRef" :model="archiveForm" :rules="archiveRules" label-width="100px">
      <el-form-item label="住院号" prop="inpatientId">
        <el-select v-model="archiveForm.inpatientId" placeholder="请选择出院患者" style="width: 100%;" filterable>
          <el-option
            v-for="inpatient in dischargedInpatients"
            :key="inpatient.id"
            :label="inpatient.inpatientNo + ' - ' + inpatient.patientName"
            :value="inpatient.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="档案路径">
        <el-input v-model="archiveForm.filePath" placeholder="请输入档案存储路径" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="archiveForm.remark" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="archiveDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleArchiveSubmit">确认归档</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="borrowDialogVisible" title="借阅档案" width="500px">
    <el-form ref="borrowFormRef" :model="borrowForm" :rules="borrowRules" label-width="100px">
      <el-form-item label="档案编号">
        <el-input v-model="borrowForm.archiveNo" disabled />
      </el-form-item>
      <el-form-item label="预计归还日期" prop="expectedReturnTime">
        <el-date-picker v-model="borrowForm.expectedReturnTime" type="date" placeholder="请选择" value-format="YYYY-MM-DD" style="width: 100%;" />
      </el-form-item>
      <el-form-item label="借阅原因" prop="remark">
        <el-input v-model="borrowForm.remark" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="borrowDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleBorrowSubmit">确认借阅</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="returnDialogVisible" title="归还档案" width="500px">
    <el-form ref="returnFormRef" :model="returnForm" label-width="100px">
      <el-form-item label="档案编号">
        <el-input v-model="returnForm.archiveNo" disabled />
      </el-form-item>
      <el-form-item label="借阅人">
        <el-input v-model="returnForm.borrowUserName" disabled />
      </el-form-item>
      <el-form-item label="借阅时间">
        <el-input v-model="returnForm.borrowTime" disabled />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="returnForm.remark" type="textarea" :rows="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="returnDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleReturnSubmit">确认归还</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getArchivePage,
  archiveInpatient,
  borrowArchive,
  returnArchive
} from '@/api/inpatient'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  patientName: '',
  inpatientNo: '',
  archiveStatus: ''
})

const tableData = ref([])
const total = ref(0)
const archiveDialogVisible = ref(false)
const borrowDialogVisible = ref(false)
const returnDialogVisible = ref(false)
const archiveFormRef = ref(null)
const borrowFormRef = ref(null)
const returnFormRef = ref(null)
const dischargedInpatients = ref([])

const archiveForm = reactive({
  inpatientId: null,
  filePath: '',
  remark: ''
})

const borrowForm = reactive({
  id: null,
  archiveNo: '',
  expectedReturnTime: '',
  remark: ''
})

const returnForm = reactive({
  id: null,
  archiveNo: '',
  borrowUserName: '',
  borrowTime: '',
  remark: ''
})

const archiveRules = {
  inpatientId: [{ required: true, message: '请选择出院患者', trigger: 'change' }]
}

const borrowRules = {
  expectedReturnTime: [{ required: true, message: '请选择预计归还日期', trigger: 'change' }],
  remark: [{ required: true, message: '请输入借阅原因', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const getBorrowType = (status) => {
  const types = ['success', 'warning', 'info']
  return types[status] || 'info'
}

const getBorrowText = (status) => {
  const texts = ['未借阅', '已借阅', '已归还']
  return texts[status] || '未知'
}

const handleQuery = async () => {
  const params = { ...queryForm }
  const res = await getArchivePage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.patientName = ''
  queryForm.inpatientNo = ''
  queryForm.archiveStatus = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleArchive = (row) => {
  dischargedInpatients.value = []
  if (row && row.id) {
    archiveForm.inpatientId = row.inpatientId
  } else {
    archiveForm.inpatientId = null
  }
  archiveForm.filePath = ''
  archiveForm.remark = ''
  archiveDialogVisible.value = true
}

const handleBorrow = (row) => {
  Object.assign(borrowForm, {
    id: row.id,
    archiveNo: row.archiveNo,
    expectedReturnTime: '',
    remark: ''
  })
  borrowDialogVisible.value = true
}

const handleReturn = (row) => {
  Object.assign(returnForm, {
    id: row.id,
    archiveNo: row.archiveNo,
    borrowUserName: row.borrowUserName,
    borrowTime: row.borrowTime,
    remark: ''
  })
  returnDialogVisible.value = true
}

const handleArchiveSubmit = async () => {
  if (!archiveFormRef.value) return
  await archiveFormRef.value.validate(async (valid) => {
    if (valid) {
      await archiveInpatient(archiveForm)
      ElMessage.success('归档成功')
      archiveDialogVisible.value = false
      handleQuery()
    }
  })
}

const handleBorrowSubmit = async () => {
  if (!borrowFormRef.value) return
  await borrowFormRef.value.validate(async (valid) => {
    if (valid) {
      await borrowArchive(borrowForm)
      ElMessage.success('借阅成功')
      borrowDialogVisible.value = false
      handleQuery()
    }
  })
}

const handleReturnSubmit = async () => {
  await returnArchive(returnForm)
  ElMessage.success('归还成功')
  returnDialogVisible.value = false
  handleQuery()
}

onMounted(() => {
  handleQuery()
})
</script>
