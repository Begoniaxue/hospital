<template>
  <div class="page-container">
    <div class="page-header">
      <h3>排班管理</h3>
      <div>
        <el-button type="primary" @click="handleBatchGenerate" v-if="hasPermission('outpatient:schedule:add')">
          <el-icon><Calendar /></el-icon>
          批量生成排班
        </el-button>
        <el-button type="success" @click="handleAdd" v-if="hasPermission('outpatient:schedule:add')">
          <el-icon><Plus /></el-icon>
          新增排班
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="医生">
        <el-select v-model="queryForm.doctorId" placeholder="请选择医生" clearable filterable>
          <el-option
            v-for="item in doctorList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="科室">
        <el-select v-model="queryForm.departmentId" placeholder="请选择科室" clearable>
          <el-option
            v-for="item in departmentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="排班日期">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="停诊状态">
        <el-select v-model="queryForm.suspended" placeholder="请选择状态" clearable>
          <el-option label="正常" :value="0" />
          <el-option label="已停诊" :value="1" />
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
      <el-table-column prop="scheduleDate" label="排班日期" width="120" />
      <el-table-column prop="timeSlot" label="时段" width="100">
        <template #default="scope">
          {{ scope.row.timeSlot === 1 ? '上午' : scope.row.timeSlot === 2 ? '下午' : '全天' }}
        </template>
      </el-table-column>
      <el-table-column prop="departmentName" label="科室" width="150" />
      <el-table-column prop="doctorName" label="医生" width="100" />
      <el-table-column prop="doctorTitle" label="职称" width="120" />
      <el-table-column prop="registrationFee" label="挂号费(元)" width="120">
        <template #default="scope">
          ¥{{ scope.row.registrationFee }}
        </template>
      </el-table-column>
      <el-table-column prop="maxCount" label="最大号量" width="100" />
      <el-table-column prop="remainingCount" label="剩余号量" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.remainingCount === 0 ? 'danger' : 'success'">
            {{ scope.row.remainingCount }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="suspended" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.suspended === 1 ? 'danger' : 'success'">
            {{ scope.row.suspended === 1 ? '已停诊' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="suspendReason" label="停诊原因" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              type="warning"
              @click="handleSuspend(scope.row)"
              v-if="!scope.row.suspended && hasPermission('outpatient:schedule:edit')"
            >
              停诊
            </el-button>
            <el-button
              size="small"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('outpatient:schedule:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-if="hasPermission('outpatient:schedule:delete')"
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

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="科室" prop="departmentId">
        <el-select v-model="form.departmentId" placeholder="请选择科室" @change="handleDepartmentChange">
          <el-option
            v-for="item in departmentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="医生" prop="doctorId">
        <el-select v-model="form.doctorId" placeholder="请选择医生" filterable>
          <el-option
            v-for="item in filteredDoctors"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="排班日期" prop="scheduleDate">
        <el-date-picker
          v-model="form.scheduleDate"
          type="date"
          placeholder="请选择日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="时段" prop="timeSlot">
        <el-radio-group v-model="form.timeSlot">
          <el-radio :value="1">上午</el-radio>
          <el-radio :value="2">下午</el-radio>
          <el-radio :value="3">全天</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="最大号量" prop="maxCount">
        <el-input-number v-model="form.maxCount" :min="1" :max="100" />
      </el-form-item>
      <el-form-item label="挂号费" prop="registrationFee">
        <el-input-number v-model="form.registrationFee" :min="0" :precision="2" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="suspendDialogVisible" title="停诊" width="500px">
    <el-form ref="suspendFormRef" :model="suspendForm" :rules="suspendRules" label-width="100px">
      <el-form-item label="停诊原因" prop="reason">
        <el-input v-model="suspendForm.reason" type="textarea" :rows="4" placeholder="请输入停诊原因" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="suspendDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSuspendSubmit">确定停诊</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="batchDialogVisible" title="批量生成排班" width="700px">
    <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
      <el-form-item label="科室" prop="departmentId">
        <el-select v-model="batchForm.departmentId" placeholder="请选择科室" @change="handleBatchDepartmentChange">
          <el-option
            v-for="item in departmentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="医生" prop="doctorIds">
        <el-select v-model="batchForm.doctorIds" multiple placeholder="请选择医生" filterable>
          <el-option
            v-for="item in batchFilteredDoctors"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker
          v-model="batchForm.startDate"
          type="date"
          placeholder="请选择开始日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker
          v-model="batchForm.endDate"
          type="date"
          placeholder="请选择结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item label="排班星期" prop="weekdays">
        <el-checkbox-group v-model="batchForm.weekdays">
          <el-checkbox :label="1">周一</el-checkbox>
          <el-checkbox :label="2">周二</el-checkbox>
          <el-checkbox :label="3">周三</el-checkbox>
          <el-checkbox :label="4">周四</el-checkbox>
          <el-checkbox :label="5">周五</el-checkbox>
          <el-checkbox :label="6">周六</el-checkbox>
          <el-checkbox :label="7">周日</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="时段" prop="timeSlots">
        <el-checkbox-group v-model="batchForm.timeSlots">
          <el-checkbox :label="1">上午</el-checkbox>
          <el-checkbox :label="2">下午</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="最大号量" prop="maxCount">
        <el-input-number v-model="batchForm.maxCount" :min="1" :max="100" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="batchDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleBatchSubmit">生成</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import {
  getSchedulePage,
  getSchedule,
  addSchedule,
  updateSchedule,
  deleteSchedule,
  suspendSchedule,
  batchGenerateSchedule
} from '@/api/schedule'
import { getDepartmentList } from '@/api/department'
import { getDoctorList } from '@/api/doctor'

const userStore = useUserStore()

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  doctorId: null,
  departmentId: null,
  suspended: null
})

const dateRange = ref([])
const tableData = ref([])
const departmentList = ref([])
const doctorList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const suspendDialogVisible = ref(false)
const batchDialogVisible = ref(false)
const formRef = ref(null)
const suspendFormRef = ref(null)
const batchFormRef = ref(null)
const dialogTitle = ref('')

const form = reactive({
  id: null,
  departmentId: null,
  doctorId: null,
  scheduleDate: '',
  timeSlot: 1,
  maxCount: 30,
  registrationFee: 0
})

const suspendForm = reactive({
  id: null,
  reason: ''
})

const batchForm = reactive({
  departmentId: null,
  doctorIds: [],
  startDate: '',
  endDate: '',
  weekdays: [1, 2, 3, 4, 5],
  timeSlots: [1, 2],
  maxCount: 30
})

const rules = {
  departmentId: [{ required: true, message: '请选择科室', trigger: 'change' }],
  doctorId: [{ required: true, message: '请选择医生', trigger: 'change' }],
  scheduleDate: [{ required: true, message: '请选择排班日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时段', trigger: 'change' }],
  maxCount: [{ required: true, message: '请输入最大号量', trigger: 'blur' }]
}

const suspendRules = {
  reason: [{ required: true, message: '请输入停诊原因', trigger: 'blur' }]
}

const batchRules = {
  departmentId: [{ required: true, message: '请选择科室', trigger: 'change' }],
  doctorIds: [{ required: true, message: '请选择医生', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  weekdays: [{ required: true, message: '请选择排班星期', trigger: 'change' }],
  timeSlots: [{ required: true, message: '请选择时段', trigger: 'change' }],
  maxCount: [{ required: true, message: '请输入最大号量', trigger: 'blur' }]
}

const filteredDoctors = computed(() => {
  if (!form.departmentId) return doctorList.value
  return doctorList.value.filter(d => d.departmentId === form.departmentId)
})

const batchFilteredDoctors = computed(() => {
  if (!batchForm.departmentId) return doctorList.value
  return doctorList.value.filter(d => d.departmentId === batchForm.departmentId)
})

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const loadDepartmentList = async () => {
  const res = await getDepartmentList({ status: 1 })
  departmentList.value = res.data || []
}

const loadDoctorList = async () => {
  const res = await getDoctorList({ status: 1 })
  doctorList.value = res.data || []
}

const handleQuery = async () => {
  const params = { ...queryForm }
  if (dateRange.value && dateRange.value.length === 2) {
    params.startDate = dateRange.value[0]
    params.endDate = dateRange.value[1]
  }
  const res = await getSchedulePage(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleReset = () => {
  queryForm.doctorId = null
  queryForm.departmentId = null
  queryForm.suspended = null
  queryForm.pageNum = 1
  dateRange.value = []
  handleQuery()
}

const handleDepartmentChange = () => {
  form.doctorId = null
}

const handleBatchDepartmentChange = () => {
  batchForm.doctorIds = []
}

const handleAdd = () => {
  dialogTitle.value = '新增排班'
  Object.assign(form, {
    id: null,
    departmentId: null,
    doctorId: null,
    scheduleDate: '',
    timeSlot: 1,
    maxCount: 30,
    registrationFee: 0
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑排班'
  const res = await getSchedule(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.id) {
        await updateSchedule(form)
        ElMessage.success('修改成功')
      } else {
        await addSchedule(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该排班吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteSchedule(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleSuspend = (row) => {
  suspendForm.id = row.id
  suspendForm.reason = ''
  suspendDialogVisible.value = true
}

const handleSuspendSubmit = async () => {
  if (!suspendFormRef.value) return
  await suspendFormRef.value.validate(async (valid) => {
    if (valid) {
      await suspendSchedule(suspendForm.id, suspendForm.reason)
      ElMessage.success('停诊成功')
      suspendDialogVisible.value = false
      handleQuery()
    }
  })
}

const handleBatchGenerate = () => {
  Object.assign(batchForm, {
    departmentId: null,
    doctorIds: [],
    startDate: '',
    endDate: '',
    weekdays: [1, 2, 3, 4, 5],
    timeSlots: [1, 2],
    maxCount: 30
  })
  batchDialogVisible.value = true
}

const handleBatchSubmit = async () => {
  if (!batchFormRef.value) return
  await batchFormRef.value.validate(async (valid) => {
    if (valid) {
      const params = {
        startDate: batchForm.startDate,
        endDate: batchForm.endDate
      }
      const data = {
        departmentId: batchForm.departmentId,
        doctorIds: batchForm.doctorIds,
        weekdays: batchForm.weekdays,
        timeSlots: batchForm.timeSlots,
        maxCount: batchForm.maxCount
      }
      await batchGenerateSchedule(params, data)
      ElMessage.success('批量生成成功')
      batchDialogVisible.value = false
      handleQuery()
    }
  })
}

onMounted(() => {
  loadDepartmentList()
  loadDoctorList()
  handleQuery()
})
</script>
