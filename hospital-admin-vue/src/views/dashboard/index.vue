<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <el-icon size="40" color="#409EFF"><User /></el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold;">{{ stats.userCount }}</div>
            <div style="color: #999;">用户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <el-icon size="40" color="#67C23A"><UserFilled /></el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold;">{{ stats.patientCount }}</div>
            <div style="color: #999;">患者总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <el-icon size="40" color="#E6A23C"><Setting /></el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold;">{{ stats.roleCount }}</div>
            <div style="color: #999;">角色总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center;">
            <el-icon size="40" color="#F56C6C"><Menu /></el-icon>
            <div style="margin-top: 10px; font-size: 24px; font-weight: bold;">{{ stats.permissionCount }}</div>
            <div style="color: #999;">权限总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>欢迎使用医院管理系统</span>
          </template>
          <div style="padding: 20px;">
            <h3>系统功能说明：</h3>
            <ul style="line-height: 2;">
              <li><strong>系统管理</strong>：用户管理、角色管理、权限管理</li>
              <li><strong>患者管理</strong>：患者档案的增删改查、模糊搜索、时间筛选</li>
              <li><strong>RBAC权限控制</strong>：不同角色登录后看到不同的菜单和功能</li>
              <li><strong>日志记录</strong>：登录日志、操作日志全程留痕</li>
            </ul>
            <h3 style="margin-top: 20px;">测试账号：</h3>
            <ul style="line-height: 2;">
              <li>管理员：admin / 123456 （拥有所有权限）</li>
              <li>医生：doctor / 123456 （可管理患者）</li>
              <li>护士：nurse / 123456 （仅可查看患者）</li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList } from '@/api/user'
import { getRolePage } from '@/api/role'
import { getPermissionTree } from '@/api/permission'
import { getPatientPage } from '@/api/patient'

const stats = ref({
  userCount: 0,
  patientCount: 0,
  roleCount: 0,
  permissionCount: 0
})

const countPermissions = (list) => {
  let count = 0
  list.forEach(item => {
    count++
    if (item.children && item.children.length) {
      count += countPermissions(item.children)
    }
  })
  return count
}

onMounted(async () => {
  const userRes = await getUserList({ pageNum: 1, pageSize: 1 })
  stats.value.userCount = userRes.data.total

  const patientRes = await getPatientPage({ pageNum: 1, pageSize: 1 })
  stats.value.patientCount = patientRes.data.total

  const roleRes = await getRolePage({ pageNum: 1, pageSize: 1 })
  stats.value.roleCount = roleRes.data.total

  const permRes = await getPermissionTree()
  stats.value.permissionCount = countPermissions(permRes.data)
})
</script>
