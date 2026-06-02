<template>
    <view class="home-container">
        <view class="header">
            <view class="patient-switch" @click="handleSwitchPatient">
                <view class="patient-info">
                    <view class="avatar" :class="{ 'avatar-female': currentPatient?.gender === 0 }">
                        {{ currentPatient?.name?.substring(0, 1) || '用' }}
                    </view>
                    <view class="info">
                        <view class="name-row">
                            <text class="name">{{ currentPatient?.name || '请登录' }}</text>
                            <text class="arrow">▼</text>
                        </view>
                        <view class="patient-no" v-if="currentPatient?.patientNo">
                            就诊号: {{ currentPatient.patientNo }}
                        </view>
                    </view>
                </view>
                <view class="qrcode-btn" @click.stop="handleQrcode">
                    <text class="qrcode-icon">📱</text>
                    <text>就诊码</text>
                </view>
            </view>

            <view class="search-bar" @click="handleSearch">
                <text class="search-icon">🔍</text>
                <text class="search-placeholder">搜索科室、医生、药品...</text>
            </view>
        </view>

        <view class="banner">
            <swiper class="banner-swiper" indicator-dots autoplay circular :interval="4000">
                <swiper-item v-for="(item, index) in banners" :key="index">
                    <view class="banner-item" :style="{ background: item.bg }">
                        <text class="banner-title">{{ item.title }}</text>
                        <text class="banner-desc">{{ item.desc }}</text>
                    </view>
                </swiper-item>
            </swiper>
        </view>

        <view class="quick-entry">
            <view class="entry-grid">
                <view class="entry-item" @click="navigateTo('/pages/register/index')">
                    <view class="entry-icon icon-1">📅</view>
                    <text class="entry-text">预约挂号</text>
                </view>
                <view class="entry-item" @click="handleQrcode">
                    <view class="entry-icon icon-2">📱</view>
                    <text class="entry-text">就诊码</text>
                </view>
                <view class="entry-item" @click="navigateTo('/pages/payment/index')">
                    <view class="entry-icon icon-3">💳</view>
                    <text class="entry-text">门诊缴费</text>
                </view>
                <view class="entry-item" @click="navigateTo('/pages/report/index')">
                    <view class="entry-icon icon-4">📋</view>
                    <text class="entry-text">报告查询</text>
                </view>
                <view class="entry-item" @click="navigateTo('/pages/pharmacy/index')">
                    <view class="entry-icon icon-5">💊</view>
                    <text class="entry-text">药房取药</text>
                </view>
                <view class="entry-item" @click="navigateTo('/pages/doctor/index')">
                    <view class="entry-icon icon-6">👨‍⚕️</view>
                    <text class="entry-text">找医生</text>
                </view>
                <view class="entry-item" @click="navigateTo('/pages/dept/index')">
                    <view class="entry-icon icon-7">🏥</view>
                    <text class="entry-text">科室导航</text>
                </view>
                <view class="entry-item" @click="navigateTo('/pages/service/index')">
                    <view class="entry-icon icon-8">📌</view>
                    <text class="entry-text">更多服务</text>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="section-header">
                <text class="section-title">我的预约</text>
                <text class="section-more" @click="navigateTo('/pages/appointment/list')">全部 ›</text>
            </view>
            <view class="card appointment-card" v-if="latestAppointment">
                <view class="appointment-header">
                    <text class="status-tag">待就诊</text>
                    <text class="date">{{ latestAppointment.date }}</text>
                </view>
                <view class="appointment-body">
                    <text class="dept">{{ latestAppointment.dept }}</text>
                    <text class="doctor">{{ latestAppointment.doctor }}</text>
                    <text class="time">{{ latestAppointment.time }}</text>
                </view>
            </view>
            <view class="card empty-card" v-else>
                <text class="empty-text">暂无预约记录</text>
                <button class="btn-primary" @click="navigateTo('/pages/register/index')">去挂号</button>
            </view>
        </view>

        <view class="section">
            <view class="section-header">
                <text class="section-title">待办提醒</text>
            </view>
            <view class="card todo-card" v-if="todoList.length > 0">
                <view class="todo-item" v-for="(item, index) in todoList" :key="index">
                    <text class="todo-icon">{{ item.icon }}</text>
                    <view class="todo-content">
                        <text class="todo-title">{{ item.title }}</text>
                        <text class="todo-desc">{{ item.desc }}</text>
                    </view>
                    <text class="todo-arrow">›</text>
                </view>
            </view>
            <view class="card empty-card" v-else>
                <text class="empty-text">暂无待办事项</text>
            </view>
        </view>

        <view class="section">
            <view class="section-header">
                <text class="section-title">健康科普</text>
                <text class="section-more">更多 ›</text>
            </view>
            <view class="article-list">
                <view class="article-item" v-for="(item, index) in articles" :key="index">
                    <image class="article-thumb" :src="item.image" mode="aspectFill" />
                    <view class="article-content">
                        <text class="article-title">{{ item.title }}</text>
                        <text class="article-desc">{{ item.desc }}</text>
                        <view class="article-meta">
                            <text class="article-source">{{ item.source }}</text>
                            <text class="article-views">{{ item.views }}阅读</text>
                        </view>
                    </view>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            banners: [
                { title: '智慧就医 服务升级', desc: '预约挂号、报告查询、在线缴费一站式服务', bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
                { title: '电子就诊码 全流程通行', desc: '院内挂号、缴费、取药一码通行', bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
                { title: '关注健康 定期体检', desc: '在线预约体检套餐，报告随时查看', bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
            ],
            latestAppointment: {
                date: '2024-06-15 周六',
                dept: '内科门诊',
                doctor: '张主任医师',
                time: '上午 09:00-09:30'
            },
            todoList: [
                { icon: '💊', title: '待取药', desc: '您有1张处方待取药，请前往门诊药房', type: 'pharmacy' },
                { icon: '💰', title: '待缴费', desc: '您有1笔门诊费用待支付，金额：¥156.00', type: 'payment' }
            ],
            articles: [
                { title: '高血压患者夏季注意事项', desc: '夏季血压波动大，高血压患者需注意这些事项...', source: '心内科', views: '2.3k', image: 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=200&h=150&fit=crop' },
                { title: '糖尿病饮食指南', desc: '科学饮食，控制血糖，糖尿病患者饮食全攻略...', source: '内分泌科', views: '1.8k', image: 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=200&h=150&fit=crop' },
                { title: '儿童健康体检全解析', desc: '不同年龄段儿童体检项目及注意事项...', source: '儿科', views: '3.1k', image: 'https://images.unsplash.com/photo-1488521787991-ed7bbaae773c?w=200&h=150&fit=crop' }
            ]
        }
    },
    computed: {
        currentPatient() {
            return this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        }
    },
    onShow() {
        this.checkLogin()
    },
    methods: {
        checkLogin() {
            const token = this.$store.state.token || uni.getStorageSync('token')
            if (!token) {
                uni.navigateTo({
                    url: '/pages/login/index'
                })
                return
            }
            if (!this.currentPatient) {
                uni.navigateTo({
                    url: '/pages/login/phone'
                })
            }
        },

        handleSwitchPatient() {
            uni.navigateTo({
                url: '/pages/family/switch'
            })
        },

        handleQrcode() {
            if (!this.currentPatient) return
            uni.navigateTo({
                url: '/pages/family/qrcode?patientId=' + this.currentPatient.id + '&name=' + this.currentPatient.name
            })
        },

        handleSearch() {
            uni.showToast({
                title: '搜索功能开发中',
                icon: 'none'
            })
        },

        navigateTo(url) {
            if (url.includes('/register/') || url.includes('/payment/') || url.includes('/report/') ||
                url.includes('/pharmacy/') || url.includes('/doctor/') || url.includes('/dept/') ||
                url.includes('/appointment/')) {
                uni.showToast({
                    title: '功能开发中',
                    icon: 'none'
                })
                return
            }
            uni.navigateTo({ url })
        }
    }
}
</script>

<style scoped>
.home-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 40rpx;
}

.header {
    background: linear-gradient(180deg, #1989fa 0%, #1989fa 100%);
    padding: 24rpx 24rpx 48rpx;
    color: #fff;
}

.patient-switch {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;
}

.patient-info {
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 0;
}

.avatar {
    width: 72rpx;
    height: 72rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
    font-size: 32rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16rpx;
    flex-shrink: 0;
}

.avatar-female {
    background: rgba(255, 107, 157, 0.3);
}

.info {
    flex: 1;
    min-width: 0;
}

.name-row {
    display: flex;
    align-items: center;
    margin-bottom: 4rpx;
}

.name {
    font-size: 30rpx;
    font-weight: bold;
    margin-right: 8rpx;
}

.arrow {
    font-size: 20rpx;
    opacity: 0.8;
}

.patient-no {
    font-size: 22rpx;
    opacity: 0.8;
}

.qrcode-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8rpx 16rpx;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 12rpx;
    font-size: 22rpx;
    margin-left: 16rpx;
}

.qrcode-icon {
    font-size: 32rpx;
    margin-bottom: 4rpx;
}

.search-bar {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 40rpx;
    padding: 16rpx 24rpx;
}

.search-icon {
    font-size: 28rpx;
    margin-right: 12rpx;
}

.search-placeholder {
    font-size: 26rpx;
    opacity: 0.8;
}

.banner {
    padding: 0 24rpx;
    margin-top: -24rpx;
    margin-bottom: 24rpx;
}

.banner-swiper {
    height: 200rpx;
    border-radius: 16rpx;
    overflow: hidden;
}

.banner-item {
    height: 100%;
    border-radius: 16rpx;
    padding: 32rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
    color: #fff;
}

.banner-title {
    font-size: 32rpx;
    font-weight: bold;
    margin-bottom: 8rpx;
}

.banner-desc {
    font-size: 24rpx;
    opacity: 0.9;
}

.quick-entry {
    margin: 0 24rpx 24rpx;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx 16rpx;
}

.entry-grid {
    display: flex;
    flex-wrap: wrap;
}

.entry-item {
    width: 25%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16rpx 0;
}

.entry-icon {
    width: 88rpx;
    height: 88rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 40rpx;
    margin-bottom: 12rpx;
}

.icon-1 { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.icon-2 { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.icon-3 { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.icon-4 { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.icon-5 { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.icon-6 { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }
.icon-7 { background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%); }
.icon-8 { background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%); }

.entry-text {
    font-size: 24rpx;
    color: #333;
}

.section {
    margin: 0 24rpx 24rpx;
}

.section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16rpx;
}

.section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
}

.section-more {
    font-size: 26rpx;
    color: #1989fa;
}

.card {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
}

.appointment-card {
    border-left: 8rpx solid #07c160;
}

.appointment-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16rpx;
}

.status-tag {
    font-size: 24rpx;
    color: #07c160;
    background: rgba(7, 193, 96, 0.1);
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.date {
    font-size: 24rpx;
    color: #666;
}

.appointment-body {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
}

.dept {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
}

.doctor {
    font-size: 26rpx;
    color: #666;
}

.time {
    font-size: 26rpx;
    color: #1989fa;
}

.empty-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 48rpx 24rpx;
}

.empty-text {
    font-size: 26rpx;
    color: #999;
    margin-bottom: 24rpx;
}

.btn-primary {
    width: 240rpx;
    height: 72rpx;
    line-height: 72rpx;
    background: #1989fa;
    color: #fff;
    border-radius: 36rpx;
    font-size: 26rpx;
    padding: 0;
    border: none;
}

.todo-card {
    padding: 0 24rpx;
}

.todo-item {
    display: flex;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
}

.todo-item:last-child {
    border-bottom: none;
}

.todo-icon {
    font-size: 40rpx;
    margin-right: 16rpx;
    flex-shrink: 0;
}

.todo-content {
    flex: 1;
    min-width: 0;
}

.todo-title {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
    display: block;
    margin-bottom: 4rpx;
}

.todo-desc {
    font-size: 24rpx;
    color: #999;
}

.todo-arrow {
    font-size: 36rpx;
    color: #ccc;
}

.article-list {
    display: flex;
    flex-direction: column;
    gap: 16rpx;
}

.article-item {
    display: flex;
    background: #fff;
    border-radius: 16rpx;
    padding: 20rpx;
}

.article-thumb {
    width: 180rpx;
    height: 135rpx;
    border-radius: 12rpx;
    margin-right: 20rpx;
    flex-shrink: 0;
}

.article-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.article-title {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.article-desc {
    font-size: 24rpx;
    color: #999;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.article-meta {
    display: flex;
    justify-content: space-between;
    font-size: 22rpx;
    color: #999;
}

.article-source {
    color: #1989fa;
}
</style>
