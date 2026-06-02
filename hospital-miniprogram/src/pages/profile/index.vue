<template>
    <view class="profile-container">
        <view class="header-card">
            <view class="user-info" @click="checkLogin">
                <view class="avatar" :class="{ 'avatar-female': currentPatient?.gender === 0 }">
                    {{ currentPatient?.name?.substring(0, 1) || wechatUser?.nickname?.substring(0, 1) || '用' }}
                </view>
                <view class="info" v-if="currentPatient">
                    <view class="name-row">
                        <text class="name">{{ currentPatient.name }}</text>
                        <text class="real-name-tag">已实名</text>
                    </view>
                    <view class="phone">{{ maskPhone(currentPatient.phone) }}</view>
                </view>
                <view class="info" v-else-if="wechatUser">
                    <view class="name-row">
                        <text class="name">{{ wechatUser.nickname }}</text>
                    </view>
                    <view class="phone">点击完成实名认证</view>
                </view>
                <view class="info" v-else>
                    <view class="name-row">
                        <text class="name">未登录</text>
                    </view>
                    <view class="phone">点击登录</view>
                </view>
                <text class="arrow">›</text>
            </view>

            <view class="quick-actions">
                <view class="action-item" @click="navigateTo('/pages/family/list')">
                    <text class="action-icon">👨‍👩‍👧‍👦</text>
                    <text class="action-text">就诊人管理</text>
                </view>
                <view class="action-item" @click="navigateTo('/pages/health/record')">
                    <text class="action-icon">📂</text>
                    <text class="action-text">健康档案</text>
                </view>
                <view class="action-item" @click="navigateTo('/pages/profile/info')">
                    <text class="action-icon">👤</text>
                    <text class="action-text">个人信息</text>
                </view>
            </view>
        </view>

        <view class="order-stats">
            <view class="stats-header">
                <text class="stats-title">我的订单</text>
                <text class="stats-more" @click="handleAllOrders">全部订单 ›</text>
            </view>
            <view class="stats-grid">
                <view class="stats-item" @click="handleOrder('appointment')">
                    <text class="stats-icon">📅</text>
                    <text class="stats-text">预约挂号</text>
                </view>
                <view class="stats-item" @click="handleOrder('payment')">
                    <text class="stats-icon">💰</text>
                    <text class="stats-text">待缴费</text>
                </view>
                <view class="stats-item" @click="handleOrder('pharmacy')">
                    <text class="stats-icon">💊</text>
                    <text class="stats-text">待取药</text>
                </view>
                <view class="stats-item" @click="handleOrder('report')">
                    <text class="stats-icon">📋</text>
                    <text class="stats-text">查报告</text>
                </view>
                <view class="stats-item" @click="handleOrder('refund')">
                    <text class="stats-icon">↩️</text>
                    <text class="stats-text">退号退款</text>
                </view>
            </view>
        </view>

        <view class="menu-section">
            <view class="menu-item" @click="navigateTo('/pages/family/qrcode')">
                <text class="menu-icon">📱</text>
                <text class="menu-text">我的就诊码</text>
                <text class="menu-arrow">›</text>
            </view>
            <view class="menu-item" @click="navigateTo('/pages/health/attachment')">
                <text class="menu-icon">📎</text>
                <text class="menu-text">健康附件</text>
                <text class="menu-arrow">›</text>
            </view>
            <view class="menu-item" @click="handleMenu('card')">
                <text class="menu-icon">🪪</text>
                <text class="menu-text">我的就诊卡</text>
                <text class="menu-arrow">›</text>
            </view>
            <view class="menu-item" @click="handleMenu('insurance')">
                <text class="menu-icon">🏥</text>
                <text class="menu-text">医保绑定</text>
                <text class="menu-arrow">›</text>
            </view>
            <view class="menu-item" @click="handleMenu('invoice')">
                <text class="menu-icon">📄</text>
                <text class="menu-text">电子发票</text>
                <text class="menu-arrow">›</text>
            </view>
        </view>

        <view class="menu-section">
            <view class="menu-item" @click="handleMenu('address')">
                <text class="menu-icon">📍</text>
                <text class="menu-text">收货地址</text>
                <text class="menu-arrow">›</text>
            </view>
            <view class="menu-item" @click="handleMenu('feedback')">
                <text class="menu-icon">💬</text>
                <text class="menu-text">意见反馈</text>
                <text class="menu-arrow">›</text>
            </view>
            <view class="menu-item" @click="handleMenu('about')">
                <text class="menu-icon">ℹ️</text>
                <text class="menu-text">关于我们</text>
                <text class="menu-arrow">›</text>
            </view>
            <view class="menu-item" @click="handleMenu('settings')">
                <text class="menu-icon">⚙️</text>
                <text class="menu-text">设置</text>
                <text class="menu-arrow">›</text>
            </view>
        </view>

        <view class="footer" v-if="isLoggedIn">
            <button class="btn-logout" @click="handleLogout">退出登录</button>
            <text class="version">版本号 v1.0.0</text>
        </view>
        <view class="footer" v-else>
            <text class="version">版本号 v1.0.0</text>
        </view>
    </view>
</template>

<script>
import { maskPhone } from '../../utils/index.js'

export default {
    data() {
        return {}
    },
    computed: {
        wechatUser() {
            return this.$store.state.wechatUser || uni.getStorageSync('wechatUser')
        },
        currentPatient() {
            return this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        },
        isLoggedIn() {
            return !!(this.$store.state.token || uni.getStorageSync('token'))
        }
    },
    methods: {
        maskPhone,

        checkLogin() {
            if (!this.isLoggedIn) {
                uni.navigateTo({
                    url: '/pages/login/index'
                })
            } else if (!this.currentPatient) {
                uni.navigateTo({
                    url: '/pages/login/phone'
                })
            } else {
                uni.navigateTo({
                    url: '/pages/profile/info'
                })
            }
        },

        navigateTo(url) {
            if (!this.isLoggedIn) {
                uni.navigateTo({
                    url: '/pages/login/index'
                })
                return
            }
            if (!this.currentPatient && url !== '/pages/family/list' && url !== '/pages/login/realname') {
                uni.showToast({
                    title: '请先完成实名认证',
                    icon: 'none'
                })
                return
            }
            if (url === '/pages/family/qrcode' && this.currentPatient) {
                uni.navigateTo({
                    url: url + '?patientId=' + this.currentPatient.id + '&name=' + this.currentPatient.name
                })
                return
            }
            uni.navigateTo({ url })
        },

        handleAllOrders() {
            uni.showToast({
                title: '功能开发中',
                icon: 'none'
            })
        },

        handleOrder(type) {
            uni.showToast({
                title: '功能开发中',
                icon: 'none'
            })
        },

        handleMenu(type) {
            uni.showToast({
                title: '功能开发中',
                icon: 'none'
            })
        },

        handleLogout() {
            uni.showModal({
                title: '确认退出',
                content: '确定要退出登录吗？',
                success: (res) => {
                    if (res.confirm) {
                        this.$store.commit('LOGOUT')
                        uni.showToast({
                            title: '已退出登录',
                            icon: 'success'
                        })
                        setTimeout(() => {
                            uni.reLaunch({
                                url: '/pages/login/index'
                            })
                        }, 1500)
                    }
                }
            })
        }
    }
}
</script>

<style scoped>
.profile-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 120rpx;
}

.header-card {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    padding: 48rpx 32rpx 24rpx;
    color: #fff;
}

.user-info {
    display: flex;
    align-items: center;
    margin-bottom: 32rpx;
}

.avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
    font-size: 48rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 24rpx;
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
    margin-bottom: 8rpx;
}

.name {
    font-size: 36rpx;
    font-weight: bold;
    margin-right: 12rpx;
}

.real-name-tag {
    font-size: 20rpx;
    color: #fff;
    background: rgba(7, 193, 96, 0.8);
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.phone {
    font-size: 26rpx;
    opacity: 0.9;
}

.arrow {
    font-size: 40rpx;
    opacity: 0.8;
}

.quick-actions {
    display: flex;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 16rpx;
    padding: 24rpx 0;
}

.action-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.action-icon {
    font-size: 40rpx;
    margin-bottom: 8rpx;
}

.action-text {
    font-size: 24rpx;
}

.order-stats {
    margin: -16rpx 24rpx 24rpx;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    position: relative;
    z-index: 2;
}

.stats-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;
}

.stats-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
}

.stats-more {
    font-size: 26rpx;
    color: #1989fa;
}

.stats-grid {
    display: flex;
}

.stats-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.stats-icon {
    font-size: 44rpx;
    margin-bottom: 8rpx;
}

.stats-text {
    font-size: 24rpx;
    color: #666;
}

.menu-section {
    margin: 0 24rpx 24rpx;
    background: #fff;
    border-radius: 16rpx;
    overflow: hidden;
}

.menu-item {
    display: flex;
    align-items: center;
    padding: 28rpx 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
    border-bottom: none;
}

.menu-icon {
    font-size: 36rpx;
    margin-right: 20rpx;
    width: 48rpx;
    text-align: center;
}

.menu-text {
    flex: 1;
    font-size: 28rpx;
    color: #333;
}

.menu-arrow {
    font-size: 32rpx;
    color: #ccc;
}

.footer {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 48rpx 24rpx 24rpx;
}

.btn-logout {
    width: 100%;
    max-width: 600rpx;
    height: 88rpx;
    line-height: 88rpx;
    background: #fff;
    color: #ee0a24;
    border: 2rpx solid #ee0a24;
    border-radius: 44rpx;
    font-size: 30rpx;
    margin-bottom: 24rpx;
}

.version {
    font-size: 24rpx;
    color: #999;
}
</style>
