<template>
    <view class="appointment-list-container">
        <view class="header">
            <view class="header-title">我的挂号</view>
            <view class="header-subtitle">查看您的预约记录</view>
        </view>

        <view class="tab-bar">
            <view 
                class="tab-item" 
                v-for="tab in tabs" 
                :key="tab.value"
                :class="{ active: activeTab === tab.value }"
                @click="switchTab(tab.value)"
            >
                {{ tab.label }}
            </view>
        </view>

        <view class="content" v-if="!loading">
            <view class="order-list" v-if="orders.length > 0">
                <view 
                    class="order-card" 
                    v-for="order in orders" 
                    :key="order.id"
                    @click="goToDetail(order.id)"
                >
                    <view class="order-header">
                        <text class="order-no">{{ order.orderNo }}</text>
                        <text class="order-status" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</text>
                    </view>
                    <view class="order-body">
                        <image class="doctor-avatar" :src="order.doctorAvatar || '/static/default-avatar.png'" mode="aspectFill" />
                        <view class="order-info">
                            <view class="doctor-row">
                                <text class="doctor-name">{{ order.doctorName }}</text>
                                <text class="doctor-title">{{ order.doctorTitle }}</text>
                            </view>
                            <view class="dept-name">{{ order.departmentName }}</view>
                            <view class="visit-time">
                                <text class="time-icon">📅</text>
                                {{ order.visitDate }} {{ order.period === 'morning' ? '上午' : '下午' }}
                            </view>
                            <view class="visit-location" v-if="order.floor && order.room">
                                <text class="location-icon">📍</text>
                                {{ order.floor }} {{ order.room }}
                            </view>
                        </view>
                        <text class="arrow">›</text>
                    </view>
                    <view class="order-footer">
                        <text class="order-fee">¥{{ order.fee }}</text>
                        <view class="order-actions">
                            <button 
                                class="action-btn secondary" 
                                v-if="order.status === 'pending' && order.canCancel"
                                @click.stop="cancelOrder(order)"
                            >
                                取消预约
                            </button>
                            <button 
                                class="action-btn primary" 
                                v-if="order.status === 'pending'"
                                @click.stop="goToQueue(order)"
                            >
                                查看排队
                            </button>
                            <button 
                                class="action-btn primary" 
                                v-if="order.status === 'checked_in'"
                                @click.stop="goToQueue(order)"
                            >
                                候诊中
                            </button>
                            <button 
                                class="action-btn secondary" 
                                v-if="order.status === 'completed'"
                                @click.stop="viewReceipt(order)"
                            >
                                查看小票
                            </button>
                        </view>
                    </view>
                </view>
            </view>
            <view class="empty-box" v-else>
                <text class="empty-icon">📋</text>
                <text class="empty-text">暂无{{ getStatusText(activeTab) }}记录</text>
                <button class="btn-go-register" @click="goToRegister">
                    去挂号
                </button>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
import { getPatientRegistrations, cancelRegistration } from '../../api/registration.js'

export default {
    data() {
        return {
            loading: false,
            activeTab: 'pending',
            tabs: [
                { label: '待就诊', value: 'pending' },
                { label: '已完成', value: 'completed' },
                { label: '已取消', value: 'cancelled' },
                { label: '已退费', value: 'refunded' }
            ],
            orders: [],
            patientId: ''
        }
    },
    onLoad() {
        const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        if (currentPatient) {
            this.patientId = currentPatient.id
            this.loadOrders()
        } else {
            uni.showToast({
                title: '请先完成实名认证',
                icon: 'none'
            })
        }
    },
    onShow() {
        if (this.patientId) {
            this.loadOrders()
        }
    },
    methods: {
        async loadOrders() {
            this.loading = true
            try {
                const status = this.activeTab === 'pending' ? '' : this.activeTab
                const res = await getPatientRegistrations(this.patientId, status, 0, 20)
                if (res.code === 200) {
                    this.orders = res.data || []
                } else {
                    uni.showToast({
                        title: res.msg || '加载失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                uni.showToast({
                    title: '网络错误，请稍后重试',
                    icon: 'none'
                })
            } finally {
                this.loading = false
            }
        },

        switchTab(tab) {
            this.activeTab = tab
            this.loadOrders()
        },

        getStatusClass(status) {
            const classes = {
                'pending': 'status-pending',
                'checked_in': 'status-checked',
                'completed': 'status-completed',
                'cancelled': 'status-cancelled',
                'refunded': 'status-refunded'
            }
            return classes[status] || ''
        },

        getStatusText(status) {
            const texts = {
                'pending': '待就诊',
                'checked_in': '候诊中',
                'completed': '已完成',
                'cancelled': '已取消',
                'refunded': '已退费'
            }
            return texts[status] || status
        },

        goToDetail(id) {
            uni.navigateTo({
                url: '/pages/appointment/detail?id=' + id
            })
        },

        async cancelOrder(order) {
            uni.showModal({
                title: '取消预约',
                content: order.canCancel 
                    ? '确定要取消该预约吗？就诊前24小时取消免费。' 
                    : '距就诊不足24小时，取消预约将产生费用。确定要取消吗？',
                success: async (res) => {
                    if (res.confirm) {
                        try {
                            const result = await cancelRegistration({
                                registrationId: order.id,
                                reason: '用户主动取消'
                            })
                            if (result.code === 200) {
                                uni.showToast({
                                    title: '取消成功',
                                    icon: 'success'
                                })
                                this.loadOrders()
                            } else {
                                uni.showToast({
                                    title: result.msg || '取消失败',
                                    icon: 'none'
                                })
                            }
                        } catch (e) {
                            uni.showToast({
                                title: '网络错误，请稍后重试',
                                icon: 'none'
                            })
                        }
                    }
                }
            })
        },

        goToQueue(order) {
            uni.navigateTo({
                url: '/pages/appointment/queue?registrationId=' + order.id + '&doctorId=' + order.doctorId
            })
        },

        viewReceipt(order) {
            uni.showToast({
                title: '功能开发中',
                icon: 'none'
            })
        },

        goToRegister() {
            uni.navigateTo({
                url: '/pages/register/index'
            })
        }
    }
}
</script>

<style scoped>
.appointment-list-container {
    min-height: 100vh;
    background: #f5f5f5;
    box-sizing: border-box;
}

.header {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    padding: 48rpx 32rpx;
    color: #fff;
}

.header-title {
    font-size: 40rpx;
    font-weight: bold;
    margin-bottom: 8rpx;
}

.header-subtitle {
    font-size: 26rpx;
    opacity: 0.9;
}

.tab-bar {
    display: flex;
    background: #fff;
    margin: -24rpx 24rpx 24rpx;
    border-radius: 16rpx;
    padding: 8rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.tab-item {
    flex: 1;
    height: 72rpx;
    line-height: 72rpx;
    text-align: center;
    font-size: 26rpx;
    color: #666;
    border-radius: 12rpx;
    transition: all 0.2s;
}

.tab-item.active {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    font-weight: bold;
}

.content {
    padding: 0 24rpx 24rpx;
}

.order-list {
    margin-top: 16rpx;
}

.order-card {
    background: #fff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    overflow: hidden;
}

.order-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 24rpx;
    border-bottom: 1rpx solid #f5f5f5;
}

.order-no {
    font-size: 24rpx;
    color: #999;
}

.order-status {
    font-size: 24rpx;
    padding: 4rpx 16rpx;
    border-radius: 8rpx;
}

.status-pending {
    color: #1989fa;
    background: #e6f7ff;
}

.status-checked {
    color: #52c41a;
    background: #f6ffed;
}

.status-completed {
    color: #8c8c8c;
    background: #f5f5f5;
}

.status-cancelled, .status-refunded {
    color: #ff4d4f;
    background: #fff1f0;
}

.order-body {
    display: flex;
    align-items: center;
    padding: 24rpx;
}

.doctor-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50rpx;
    background: #f5f5f5;
    margin-right: 20rpx;
    flex-shrink: 0;
}

.order-info {
    flex: 1;
    min-width: 0;
}

.doctor-row {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.doctor-name {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-right: 12rpx;
}

.doctor-title {
    font-size: 22rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 2rpx 10rpx;
    border-radius: 6rpx;
}

.dept-name {
    font-size: 24rpx;
    color: #666;
    margin-bottom: 8rpx;
}

.visit-time, .visit-location {
    display: flex;
    align-items: center;
    font-size: 22rpx;
    color: #999;
    margin-bottom: 4rpx;
}

.time-icon, .location-icon {
    margin-right: 6rpx;
    font-size: 20rpx;
}

.arrow {
    font-size: 28rpx;
    color: #ccc;
    margin-left: 12rpx;
}

.order-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16rpx 24rpx;
    background: #fafafa;
}

.order-fee {
    font-size: 28rpx;
    color: #ff6b6b;
    font-weight: bold;
}

.order-actions {
    display: flex;
    gap: 12rpx;
}

.action-btn {
    height: 56rpx;
    padding: 0 24rpx;
    border-radius: 28rpx;
    font-size: 24rpx;
    line-height: 56rpx;
    border: none;
}

.action-btn.primary {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
}

.action-btn.secondary {
    background: #fff;
    color: #666;
    border: 1rpx solid #ddd;
}

.empty-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
}

.empty-icon {
    font-size: 100rpx;
    margin-bottom: 20rpx;
}

.empty-text {
    font-size: 28rpx;
    color: #999;
    margin-bottom: 32rpx;
}

.btn-go-register {
    width: 240rpx;
    height: 72rpx;
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    border-radius: 36rpx;
    font-size: 26rpx;
    border: none;
    line-height: 72rpx;
}

.loading-box {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
}

.loading-text {
    font-size: 26rpx;
    color: #999;
}
</style>
