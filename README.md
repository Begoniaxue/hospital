# 医院管理系统

基于 Vue3 + SpringBoot + MySQL 的轻量化医院Web端管理系统

## 技术栈

### 前端
- Vue 3.x
- Vite 5.x
- Element Plus
- Vue Router 4.x
- Pinia 状态管理
- Axios HTTP 客户端

### 后端
- Spring Boot 2.7.x
- Spring Security + JWT
- MyBatis Plus 3.5.x
- Redis (验证码存储)
- MySQL 8.0

## 功能模块

### 1. 登录 & 权限模块
- ✅ 多角色账号密码登录
- ✅ 验证码防刷
- ✅ RBAC 动态权限，不同角色仅可见对应菜单
- ✅ 密码修改
- ✅ 账号启用/禁用
- ✅ 角色新增/编辑/分配权限
- ✅ 登录日志、操作日志表结构已预留

### 2. 患者管理模块
- ✅ 患者档案新增/编辑/查询/删除
- ✅ 存储：姓名、性别、年龄、身份证、联系方式、既往病史、过敏史、紧急联系人
- ✅ 患者档案模糊搜索
- ✅ 按时间筛选

## 项目结构

```
hospital-
├── hospital-admin-vue      # 前端项目
│   ├── src
│   │   ├── api         # API 接口
│   │   ├── assets      # 静态资源
│   │   ├── components    # 公共组件
│   │   ├── layout        # 布局组件
│   │   ├── router        # 路由配置
│   │   ├── store         # 状态管理
│   │   ├── utils         # 工具函数
│   │   ├── views         # 页面组件
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── hospital-admin-server   # 后端项目
│   ├── src/main
│   │   ├── java/com/hospital
│   │   │   ├── config      # 配置类
│   │   │   ├── controller  # 控制层
│   │   │   ├── dto         # 数据传输对象
│   │   │   ├── entity      # 实体类
│   │   │   ├── mapper      # 数据访问层
│   │   │   ├── security    # 安全相关
│   │   │   ├── service     # 业务逻辑层
│   │   │   ├── utils       # 工具类
│   │   │   └── HospitalAdminApplication.java
│   │   └── resources
│   │       ├── mapper      # MyBatis XML
│   │       └── application.yml
│   └── pom.xml
│
└── sql                   # 数据库脚本
    └── hospital_admin.sql
```

## 快速开始

### 环境要求

- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Redis 5.0+

### 1. 数据库初始化

```bash
# 创建数据库并执行脚本
mysql -u root -p
source sql/hospital_admin.sql
```

### 2. 后端启动

```bash
cd hospital-admin-server

# 修改 application.yml 中的数据库和Redis配置

# 启动项目
mvn spring-boot:run
```

后端服务地址: http://localhost:8080/api

### 3. 前端启动

```bash
cd hospital-admin-vue

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端访问地址: http://localhost:3000

## 默认账号

| 用户名 | 密码 | 角色 | 权限说明
---|---|---|---
admin | 123456 | 超级管理员 | 拥有所有权限
doctor | 123456 | 医生 | 可管理患者
nurse | 123456 | 护士 | 仅可查看患者

## 主要功能截图

### 登录页面
- 用户名密码登录
- 图形验证码
- 点击验证码刷新

### 首页
- 数据统计卡片
- 系统功能说明

### 系统管理
- 用户管理：用户增删改查、分配角色、重置密码
- 角色管理：角色增删改查、分配权限
- 权限管理：权限菜单管理

### 患者管理
- 患者列表
- 模糊搜索（姓名、手机号、身份证、病历号）
- 按创建时间筛选
- 患者详情查看/编辑
