<template>
  <div class="page-container">
    <div class="page-header">
      <h3>医生管理</h3>
      <div>
        <el-button type="primary" @click="handleAdd" v-if="hasPermission('outpatient:doctor:add')">
          <el-icon><Plus /></el-icon>
          新增医生
        </el-button>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0" v-if="hasPermission('outpatient:doctor:delete')">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="queryForm" class="search-form">
      <el-form-item label="医生姓名">
        <el-input v-model="queryForm.name" placeholder="请输入医生姓名" clearable />
      </el-form-item>
      <el-form-item label="工号">
        <el-input v-model="queryForm.jobNo" placeholder="请输入工号" clearable />
      </el-form-item>
      <el-form-item label="所属科室">
        <el-select v-model="queryForm.departmentId" placeholder="请选择科室" clearable>
          <el-option
            v-for="item in departmentList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="职称">
        <el-select v-model="queryForm.title" placeholder="请选择职称" clearable>
          <el-option label="主任医师" value="主任医师" />
          <el-option label="副主任医师" value="副主任医师" />
          <el-option label="主治医师" value="主治医师" />
          <el-option label="住院医师" value="住院医师" />
          <el-option label="医师" value="医师" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
          <el-option label="在职" :value="1" />
          <el-option label="离职" :value="0" />
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

    <el-table :data="tableData" border @selection-change="handleSelectionChange" style="width: 100%;">
      <el-table-column type="selection" width="55" />
      <el-table-column label="头像" width="80">
        <template #default="scope">
          <el-avatar :size="40" :src="scope.row.avatar || defaultAvatar">
            {{ scope.row.name?.charAt(0) }}
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="doctorNo" label="工号" width="100" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="70">
        <template #default="scope">
          {{ scope.row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column prop="departmentName" label="所属科室" width="130" />
      <el-table-column prop="title" label="职称" width="110">
        <template #default="scope">
          <el-tag size="small" type="primary">{{ scope.row.title }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="specialty" label="专业特长" show-overflow-tooltip min-width="150" />
      <el-table-column prop="consultationFee" label="挂号费(元)" width="110">
        <template #default="scope">
          <span class="fee-text">¥{{ scope.row.consultationFee || scope.row.registrationFee || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
            {{ scope.row.status === 1 ? '在职' : '离职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click="handleSchedule(scope.row)"
              v-if="hasPermission('outpatient:schedule:add')"
            >
              排班
            </el-button>
            <el-button
              size="small"
              @click="handleEdit(scope.row)"
              v-if="hasPermission('outpatient:doctor:edit')"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-if="hasPermission('outpatient:doctor:delete')"
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

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="750px" class="doctor-dialog">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" :disabled="isView">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="医生头像" prop="avatar">
            <div class="avatar-uploader">
              <el-avatar :size="120" :src="form.avatar || defaultAvatar" class="avatar-preview">
                {{ form.name?.charAt(0) }}
              </el-avatar>
              <div class="avatar-actions" v-if="!isView">
                <el-button size="small" type="primary" @click="generateAvatar">
                  生成头像
                </el-button>
                <el-button size="small" @click="clearAvatar">
                  清除
                </el-button>
              </div>
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="16">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入医生姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="工号" prop="doctorNo">
                <el-input v-model="form.doctorNo" placeholder="请输入工号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="form.gender">
                  <el-radio :value="1">男</el-radio>
                  <el-radio :value="0">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="排序" prop="sort">
                <el-input-number v-model="form.sort" :min="0" :max="999" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="所属科室" prop="departmentId">
            <el-select v-model="form.departmentId" placeholder="请选择科室" style="width: 100%;" @change="handleDepartmentChange">
              <el-option
                v-for="item in departmentList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职称" prop="title">
            <el-select v-model="form.title" placeholder="请选择职称" style="width: 100%;">
              <el-option label="主任医师" value="主任医师" />
              <el-option label="副主任医师" value="副主任医师" />
              <el-option label="主治医师" value="主治医师" />
              <el-option label="住院医师" value="住院医师" />
              <el-option label="医师" value="医师" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="挂号费" prop="consultationFee">
            <el-input-number v-model="form.consultationFee" :min="0" :precision="2" :step="5" style="width: 100%;" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="专业特长" prop="specialty">
        <el-input v-model="form.specialty" type="textarea" :rows="2" placeholder="请输入专业特长，如：心血管疾病、高血压、冠心病等" />
      </el-form-item>
      <el-form-item label="个人简介" prop="introduction">
        <el-input v-model="form.introduction" type="textarea" :rows="4" placeholder="请输入医生个人简介、教育背景、从业经历等" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">在职</el-radio>
          <el-radio :value="0">离职</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button type="primary" @click="handleSubmit" v-if="!isView">确定</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="scheduleDialogVisible" title="排班管理" width="900px" class="schedule-dialog">
    <div class="schedule-header">
      <div class="doctor-info">
        <el-avatar :size="50" :src="currentDoctor.avatar || defaultAvatar">
          {{ currentDoctor.name?.charAt(0) }}
        </el-avatar>
        <div class="doctor-detail">
          <div class="doctor-name">{{ currentDoctor.name }}</div>
          <div class="doctor-meta">
            <el-tag size="small" type="primary">{{ currentDoctor.title }}</el-tag>
            <span class="dept">{{ currentDoctor.departmentName }}</span>
          </div>
        </div>
      </div>
      <div class="schedule-actions">
        <el-button type="primary" @click="handleBatchGenerateSchedule">
          <el-icon><Calendar /></el-icon>
          批量生成排班
        </el-button>
      </div>
    </div>

    <el-form :inline="true" :model="scheduleQueryForm" class="schedule-search">
      <el-form-item label="排班日期">
        <el-date-picker
          v-model="scheduleDateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadScheduleList">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :data="scheduleList" border style="width: 100%;">
      <el-table-column prop="scheduleDate" label="排班日期" width="120" />
      <el-table-column prop="timeSlot" label="时段" width="100">
        <template #default="scope">
          {{ scope.row.timeSlot === '上午' ? '上午' : scope.row.timeSlot === '下午' ? '下午' : '全天' }}
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="100" />
      <el-table-column prop="endTime" label="结束时间" width="100" />
      <el-table-column prop="maxCount" label="最大号量" width="100" />
      <el-table-column prop="remainingCount" label="剩余号量" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.remainingCount === 0 ? 'danger' : 'success'" size="small">
            {{ scope.row.remainingCount }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="registrationFee" label="挂号费" width="100">
        <template #default="scope">¥{{ scope.row.registrationFee }}</template>
      </el-table-column>
      <el-table-column prop="isSuspended" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.isSuspended === 1 ? 'danger' : 'success'" size="small">
            {{ scope.row.isSuspended === 1 ? '已停诊' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <div class="table-actions">
            <el-button
              size="small"
              :type="scope.row.isSuspended === 1 ? 'success' : 'warning'"
              @click="toggleSuspend(scope.row)"
            >
              {{ scope.row.isSuspended === 1 ? '恢复' : '停诊' }}
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="deleteSchedule(scope.row)"
            >
              删除
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="scheduleQueryForm.pageNum"
      v-model:page-size="scheduleQueryForm.pageSize"
      :total="scheduleTotal"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadScheduleList"
      @current-change="loadScheduleList"
      style="margin-top: 15px; justify-content: flex-end;"
    />

    <template #footer>
      <el-button @click="scheduleDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="batchScheduleDialogVisible" title="批量生成排班" width="500px">
    <el-form ref="batchScheduleFormRef" :model="batchScheduleForm" :rules="batchScheduleRules" label-width="100px">
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker
          v-model="batchScheduleForm.startDate"
          type="date"
          placeholder="选择开始日期"
          value-format="YYYY-MM-DD"
          style="width: 100%;"
        />
      </el-form-item>
      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker
          v-model="batchScheduleForm.endDate"
          type="date"
          placeholder="选择结束日期"
          value-format="YYYY-MM-DD"
          style="width: 100%;"
        />
      </el-form-item>
      <el-form-item label="排班时段" prop="timeSlots">
        <el-checkbox-group v-model="batchScheduleForm.timeSlots">
          <el-checkbox label="上午">上午 (8:00-12:00)</el-checkbox>
          <el-checkbox label="下午">下午 (14:00-17:30)</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="最大号量" prop="maxCount">
        <el-input-number v-model="batchScheduleForm.maxCount" :min="1" :max="100" style="width: 100%;" />
      </el-form-item>
      <el-form-item label="挂号费" prop="registrationFee">
        <el-input-number v-model="batchScheduleForm.registrationFee" :min="0" :precision="2" style="width: 100%;" />
      </el-form-item>
      <el-form-item label="选择星期">
        <el-checkbox-group v-model="batchScheduleForm.weekdays">
          <el-checkbox :label="1">周一</el-checkbox>
          <el-checkbox :label="2">周二</el-checkbox>
          <el-checkbox :label="3">周三</el-checkbox>
          <el-checkbox :label="4">周四</el-checkbox>
          <el-checkbox :label="5">周五</el-checkbox>
          <el-checkbox :label="6">周六</el-checkbox>
          <el-checkbox :label="0">周日</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="batchScheduleDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmBatchGenerate">确定生成</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Plus, Delete, Search, Refresh } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import {
  getDoctorPage,
  getDoctorList,
  getDoctor,
  addDoctor,
  updateDoctor,
  deleteDoctor,
  batchDeleteDoctor
} from '@/api/doctor'
import { getDepartmentList } from '@/api/department'
import {
  getSchedulePage,
  addSchedule,
  updateSchedule,
  deleteSchedule as deleteScheduleApi,
  suspendSchedule,
  batchGenerateSchedule
} from '@/api/schedule'

const userStore = useUserStore()

const defaultAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20doctor%20avatar%20portrait%20medical&image_size=square_hd'

const queryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  jobNo: '',
  departmentId: null,
  title: null,
  status: null
})

const tableData = ref([])
const departmentList = ref([])
const total = ref(0)
const selectedIds = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const isView = ref(false)
const dialogTitle = ref('')

const form = reactive({
  id: null,
  doctorNo: '',
  name: '',
  gender: 1,
  departmentId: null,
  departmentName: '',
  title: '',
  specialty: '',
  introduction: '',
  consultationFee: 0,
  phone: '',
  avatar: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  doctorNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择科室', trigger: 'change' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }]
}

const scheduleDialogVisible = ref(false)
const scheduleList = ref([])
const scheduleTotal = ref(0)
const currentDoctor = ref({})
const scheduleDateRange = ref([])
const scheduleQueryForm = reactive({
  pageNum: 1,
  pageSize: 10,
  doctorId: null,
  departmentId: null,
  startDate: null,
  endDate: null
})

const batchScheduleDialogVisible = ref(false)
const batchScheduleFormRef = ref(null)
const batchScheduleForm = reactive({
  startDate: '',
  endDate: '',
  timeSlots: ['上午', '下午'],
  maxCount: 20,
  registrationFee: 0,
  weekdays: [1, 2, 3, 4, 5]
})

const batchScheduleRules = {
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  timeSlots: [{ required: true, message: '请选择排班时段', trigger: 'change' }],
  maxCount: [{ required: true, message: '请输入最大号量', trigger: 'blur' }]
}

const hasPermission = (code) => {
  return userStore.permissions.includes(code)
}

const loadDepartmentList = async () => {
  const res = await getDepartmentList({ status: 1 })
  departmentList.value = res.data || []
}

const handleQuery = async () => {
  const res = await getDoctorPage(queryForm)
  tableData.value = res.data.records || []
  total.value = res.data.total || 0
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.jobNo = ''
  queryForm.departmentId = null
  queryForm.title = null
  queryForm.status = null
  queryForm.pageNum = 1
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleDepartmentChange = (deptId) => {
  const dept = departmentList.value.find(d => d.id === deptId)
  if (dept) {
    form.departmentName = dept.name
  }
}

const generateAvatar = () => {
  const prompts = [
    'professional male doctor portrait with white coat smiling friendly medical avatar',
    'professional female doctor portrait with white coat smiling friendly medical avatar',
    'asian doctor portrait professional medical staff avatar hospital',
    'experienced senior doctor portrait gray hair medical expert avatar'
  ]
  const randomPrompt = prompts[Math.floor(Math.random() * prompts.length)]
  form.avatar = `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=${encodeURIComponent(randomPrompt)}&image_size=square_hd&t=${Date.now()}`
}

const clearAvatar = () => {
  form.avatar = ''
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增医生'
  Object.assign(form, {
    id: null,
    doctorNo: '',
    name: '',
    gender: 1,
    departmentId: null,
    departmentName: '',
    title: '',
    specialty: '',
    introduction: '',
    consultationFee: 0,
    phone: '',
    avatar: '',
    sort: 0,
    status: 1
  })
  generateAvatar()
  loadDepartmentList()
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '医生详情'
  const res = await getDoctor(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isView.value = false
  dialogTitle.value = '编辑医生'
  const res = await getDoctor(row.id)
  Object.assign(form, res.data)
  if (!form.avatar) {
    generateAvatar()
  }
  loadDepartmentList()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const submitData = { ...form }
      if (!submitData.avatar) {
        generateAvatar()
        submitData.avatar = form.avatar
      }
      if (form.id) {
        await updateDoctor(submitData)
        ElMessage.success('修改成功')
      } else {
        await addDoctor(submitData)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      handleQuery()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该医生吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteDoctor(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个医生吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await batchDeleteDoctor(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  })
}

const handleSchedule = async (row) => {
  currentDoctor.value = { ...row }
  scheduleQueryForm.doctorId = row.id
  scheduleQueryForm.departmentId = row.departmentId
  batchScheduleForm.registrationFee = row.consultationFee || row.registrationFee || 0
  scheduleDialogVisible.value = true
  nextTick(() => {
    loadScheduleList()
  })
}

const loadScheduleList = async () => {
  const params = {
    ...scheduleQueryForm
  }
  if (scheduleDateRange.value && scheduleDateRange.value.length === 2) {
    params.startDate = scheduleDateRange.value[0]
    params.endDate = scheduleDateRange.value[1]
  }
  const res = await getSchedulePage(params)
  scheduleList.value = res.data.records || []
  scheduleTotal.value = res.data.total || 0
}

const handleBatchGenerateSchedule = () => {
  const today = new Date()
  const nextWeek = new Date(today)
  nextWeek.setDate(today.getDate() + 14)
  batchScheduleForm.startDate = today.toISOString().split('T')[0]
  batchScheduleForm.endDate = nextWeek.toISOString().split('T')[0]
  batchScheduleDialogVisible.value = true
}

const confirmBatchGenerate = async () => {
  if (!batchScheduleFormRef.value) return
  await batchScheduleFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await batchGenerateSchedule({
          doctorId: currentDoctor.value.id,
          startDate: batchScheduleForm.startDate,
          endDate: batchScheduleForm.endDate,
          timeSlots: batchScheduleForm.timeSlots,
          maxCount: batchScheduleForm.maxCount,
          registrationFee: batchScheduleForm.registrationFee,
          weekdays: batchScheduleForm.weekdays,
          departmentId: currentDoctor.value.departmentId,
          departmentName: currentDoctor.value.departmentName,
          doctorName: currentDoctor.value.name
        })
        ElMessage.success('排班生成成功')
        batchScheduleDialogVisible.value = false
        loadScheduleList()
      } catch (e) {
        console.error(e)
      }
    }
  })
}

const toggleSuspend = async (row) => {
  const action = row.isSuspended === 1 ? '恢复' : '停诊'
  ElMessageBox.confirm(`确定要${action}该排班吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await suspendSchedule(row.id, row.isSuspended === 1 ? '' : '医生临时停诊')
      ElMessage.success(`${action}成功`)
      loadScheduleList()
    } catch (e) {
      console.error(e)
    }
  })
}

const deleteSchedule = async (row) => {
  ElMessageBox.confirm('确定要删除该排班吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteScheduleApi(row.id)
    ElMessage.success('删除成功')
    loadScheduleList()
  })
}

onMounted(() => {
  loadDepartmentList()
  handleQuery()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.search-form {
  margin-bottom: 20px;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.fee-text {
  color: #f56c6c;
  font-weight: 600;
}

.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-preview {
  border: 2px solid #e4e7ed;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.avatar-actions {
  display: flex;
  gap: 8px;
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.doctor-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.doctor-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.doctor-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dept {
  font-size: 14px;
  color: #606266;
}

.schedule-search {
  margin-bottom: 16px;
}

.doctor-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
}

.schedule-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
}
</style>
