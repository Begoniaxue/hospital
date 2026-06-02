# 智慧医院患者微信小程序端

基于 UniApp + Vue3 开发的医院患者微信小程序，支持编译为微信小程序和 H5。

## 项目结构

```
hospital-miniprogram/
├── src/
│   ├── api/              # API 接口
│   │   ├── auth.js       # 认证相关
│   │   ├── family.js     # 就诊人管理
│   │   ├── health.js     # 健康档案
│   │   └── visit.js      # 就诊码
│   ├── pages/            # 页面
│   │   ├── index/        # 首页
│   │   ├── service/      # 就医服务
│   │   ├── message/      # 消息通知
│   │   ├── profile/      # 个人中心
│   │   ├── login/        # 登录/实名
│   │   ├── family/       # 就诊人管理
│   │   └── health/       # 健康档案
│   ├── store/            # Vuex 状态管理
│   ├── utils/            # 工具函数
│   │   ├── request.js    # 请求封装
│   │   └── index.js      # 通用工具
│   ├── static/           # 静态资源
│   ├── App.vue           # 根组件
│   ├── main.js           # 入口文件
│   └── uni.scss          # 全局样式变量
├── manifest.json         # 应用配置
├── pages.json            # 页面路由配置
├── package.json          # 项目依赖
├── vite.config.js        # Vite 配置
└── index.html            # H5 入口
```

## 功能模块

### 1. 登录 & 实名 & 就诊人管理

- 微信一键授权登录
- 手机号验证码验证
- 实名认证（姓名 + 身份证号）
- 自动匹配 HIS 系统就诊档案
- 家庭成员就诊人管理（最多 5 位）
- 就诊人切换、编辑、禁用
- 动态电子就诊二维码

### 2. 个人健康档案

- 基础信息维护（姓名、性别、出生日期、手机号、住址）
- 健康信息管理（过敏史、既往病史、慢病、手术史、药物禁忌）
- 健康附件上传（CT、验血、体检、病历等）
- 分类归档，永久存储

### 3. 四大底部导航

- **首页**：快捷入口、我的预约、待办提醒、健康科普
- **就医服务**：门诊服务、住院服务、便民服务、健康管理
- **消息通知**：系统通知、就医提醒、健康资讯
- **个人中心**：个人信息、就诊人管理、健康档案、我的订单

## 技术栈

- **框架**：UniApp + Vue3
- **状态管理**：Vuex 4
- **构建工具**：Vite
- **样式**：SCSS
- **API 请求**：uni.request 封装
- **后端对接**：Spring Boot + MyBatis Plus

## 运行方式

### H5 开发

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev:h5

# 构建生产版本
npm run build:h5
```

### 微信小程序开发

```bash
# 安装依赖
npm install

# 构建
npm run dev:mp-weixin
```

构建完成后，使用微信开发者工具打开 `unpackage/dist/dev/mp-weixin` 目录。

## 后端接口

后端服务地址：`http://localhost:8088/api`

主要接口：
- `/mini/auth/login` - 微信登录
- `/mini/auth/realName` - 实名认证
- `/mini/family/list` - 就诊人列表
- `/mini/health/record` - 健康档案
- `/mini/visit/code` - 生成就诊码

## 隐私保护

- 患者身份证、过敏史等敏感信息后端加密存储
- 小程序只做展示不落地缓存
- 敏感信息前端展示时自动脱敏（如身份证号只显示前后各 4 位）
