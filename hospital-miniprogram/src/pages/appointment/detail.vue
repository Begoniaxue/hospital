<template>
    <view class="detail-container">
        <view class="header">
            <view class="header-title">挂号详情</view>
            <view class="status-badge" :class="getStatusClass(detail.status)">{{ getStatusText(detail.status) }}</view>
        </view>

        <view class="content" v-if="!loading">
            <view class="section">
                <view class="info-card">
                    <view class="doctor-info">
                        <image class="doctor-avatar" :src="detail.doctorAvatar || '/static/default-avatar.png'" mode="aspectFill" />
                        <view class="doctor-detail">
                            <view class="doctor-row">
                                <text class="doctor-name">{{ detail.doctorName }}</text>
                                <text class="doctor-title">{{ detail.doctorTitle }}</text>
                            </view>
                            <text class="dept-name">{{ detail.departmentName }}</text>
                            <text class="doctor-skill" v-if="detail.doctorSkill">{{ detail.doctorSkill }}</text>
                        </view>
                    </view>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📅</text>
                    就诊信息
                </view>
                <view class="info-row">
                    <text class="info-label">就诊日期</text>
                    <text class="info-value">{{ detail.visitDate }} {{ detail.period === 'morning' ? '上午' : '下午' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊时段</text>
                    <text class="info-value">{{ detail.period === 'morning' ? '8:00-12:00' : '14:00-17:30' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊地点</text>
                    <text class="info-value highlight">{{ detail.floor || '3楼' }} {{ detail.room || '305诊室' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">挂号单号</text>
                    <text class="info-value">{{ detail.orderNo }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊序号</text>
                    <text class="info-value highlight">{{ detail.serialNumber || 'A012' }}</text>
                </view>
            </view>

            <view class="section" v-if="queueInfo">
                <view class="queue-card" @click="goToQueue">
                    <view class="queue-left">
                        <text class="queue-label">当前叫号</text>
                        <text class="queue-number">{{ queueInfo.currentNumber || 'A008' }}</text>
                    </view>
                    <view class="queue-divider"></view>
                    <view class="queue-right">
                        <text class="queue-label">前方等待</text>
                        <text class="queue-wait">{{ queueInfo.waitCount || 4 }}人</text>
                    </view>
                    <text class="queue-arrow">›</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">👤</text>
                    就诊人信息
                </view>
                <view class="info-row">
                    <text class="info-label">姓名</text>
                    <text class="info-value">{{ detail.patientName }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">身份证号</text>
                    <text class="info-value">{{ detail.idCard }}</text>
                </view>
                <view class="info-row" v-if="detail.phone">
                    <text class="info-label">联系电话</text>
                    <text class="info-value">{{ detail.phone }}</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">💰</text>
                    费用信息
                </view>
                <view class="info-row">
                    <text class="info-label">挂号费用</text>
                    <text class="info-value price">¥{{ detail.fee }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">支付方式</text>
                    <text class="info-value">{{ getPayMethodText(detail.payMethod) }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">支付时间</text>
                    <text class="info-value">{{ detail.payTime }}</text>
                </view>
                <view class="view-bill" @click="viewBill">
                    <text>查看费用明细</text>
                    <text class="arrow">›</text>
                </view>
            </view>

            <view class="section" v-if="detail.status === 'pending' || detail.status === 'checked_in'">
                <view class="action-list">
                    <view class="action-item" @click="showQRCode">
                        <text class="action-icon">📱</text>
                        <view class="action-info">
                            <text class="action-name">出示电子就诊码</text>
                            <text class="action-desc">签到候诊使用</text>
                        </view>
                        <text class="arrow">›</text>
                    </view>
                    <view class="action-item" @click="handleCheckin" v-if="detail.status === 'pending' && detail.canCheckin">
                        <text class="action-icon">✅</text>
                        <view class="action-info">
                            <text class="action-name">签到候诊</text>
                            <text class="action-desc">已到达医院，点击签到</text>
                        </view>
                        <text class="arrow">›</text>
                    </view>
                    <view class="action-item" @click="viewReceipt">
                        <text class="action-icon">🧾</text>
                        <view class="action-info">
                            <text class="action-name">查看挂号小票</text>
                            <text class="action-desc">可查看电子小票</text>
                        </view>
                        <text class="arrow">›</text>
                    </view>
                    <view class="action-item danger" @click="handleCancel" v-if="detail.status === 'pending' && detail.canCancel">
                        <text class="action-icon">❌</text>
                        <view class="action-info">
                            <text class="action-name">取消预约</text>
                            <text class="action-desc" v-if="detail.isFreeCancel">就诊前24小时，免费取消</text>
                            <text class="action-desc" v-else>距就诊不足24小时，取消将扣费</text>
                        </view>
                        <text class="arrow">›</text>
                    </view>
                </view>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
import { getRegistrationDetail, cancelRegistration, checkinRegistration, getQueueInfo } from '../../api/registration.js'

export default {
    data() {
        return {
            loading: false,
            id: '',
            detail: {},
            queueInfo: null
        }
    },
    onLoad(options) {
        this.id = options.id
        this.loadDetail()
    },
    methods: {
        async loadDetail() {
            this.loading = true
            try {
                const res = await getRegistrationDetail(this.id)
                if (res.code === 200) {
                    this.detail = res.data || {}
                    if (this.detail.status === 'pending' || this.detail.status === 'checked_in') {
                        this.loadQueueInfo()
                    }
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

        async loadQueueInfo() {
            try {
                const res = await getQueueInfo(this.id)
                if (res.code === 200) {
                    this.queueInfo = res.data
                }
            } catch (e) {
                console.error(e)
            }
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

        getPayMethodText(method) {
            const texts = {
                'wechat': '微信余额',
                'bank': '银行卡',
                'insurance': '医保支付'
            }
            return texts[method] || method
        },

        goToQueue() {
            uni.navigateTo({
                url: '/pages/appointment/queue?registrationId=' + this.id + '&doctorId=' + this.detail.doctorId
            })
        },

        showQRCode() {
            uni.showToast({
                title: '功能开发中',
                icon: 'none'
            })
        },

        async handleCheckin() {
            uni.showModal({
                title: '签到确认',
                content: '确定要签到候诊吗？',
                success: async (res) => {
                    if (res.confirm) {
                        try {
                            const result = await checkinRegistration(this.id)
                            if (result.code === 200) {
                                uni.showToast({
                                    title: '签到成功',
                                    icon: 'success'
                                })
                                this.loadDetail()
                            } else {
                                uni.showToast({
                                    title: result.msg || '签到失败',
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

        viewReceipt() {
            uni.showToast({
                title: '功能开发中',
                icon: 'none'
            })
        },

        viewBill() {
            uni.showToast({
                title: '功能开发中',
                icon: 'none'
            })
        },

        async handleCancel() {
            uni.showModal({
                title: '取消预约',
                content: this.detail.isFreeCancel 
                    ? '确定要取消该预约吗？就诊前24小时取消免费。' 
                    : '距就诊不足24小时，取消预约将产生费用。确定要取消吗？',
                success: async (res) => {
                    if (res.confirm) {
                        try {
                            const result = await cancelRegistration({
                                registrationId: this.id,
                                reason: '用户主动取消'
                            })
                            if (result.code === 200) {
                                uni.showToast({
                                    title: '取消成功',
                                    icon: 'success'
                                })
                                setTimeout(() => {
                                    uni.navigateBack()
                                }, 1500)
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
        }
    }
}
</script>

<style scoped>
.detail-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 48rpx;
    box-sizing: border-box;
}

.header {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    padding: 48rpx 32rpx;
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.header-title {
    font-size: 36rpx;
    font-weight: bold;
}

.status-badge {
    font-size: 24rpx;
    padding: 8rpx 20rpx;
    border-radius: 24rpx;
}

.status-pending {
    background: rgba(255, 255, 255, 0.2);
}

.status-checked {
    background: #52c41a;
}

.status-completed {
    background: #8c8c8c;
}

.status-cancelled, .status-refunded {
    background: #ff4d4f;
}

.content {
    padding: 24rpx;
    margin-top: -16rpx;
}

.section {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
}

.info-card {
    border-radius: 12rpx;
}

.doctor-info {
    display: flex;
    align-items: center;
}

.doctor-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    background: #f5f5f5;
    margin-right: 20rpx;
    flex-shrink: 0;
}

.doctor-detail {
    flex: 1;
}

.doctor-row {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.doctor-name {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-right: 12rpx;
}

.doctor-title {
    font-size: 24rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.dept-name {
    font-size: 26rpx;
    color: #666;
    margin-bottom: 8rpx;
    display: block;
}

.doctor-skill {
    font-size: 22rpx;
    color: #999;
    line-height: 1.5;
}

.section-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 16rpx;
    display: flex;
    align-items: center;
}

.title-icon {
    margin-right: 8rpx;
    font-size: 28rpx;
}

.info-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
}

.info-row:last-child {
    border-bottom: none;
}

.info-label {
    font-size: 26rpx;
    color: #666;
}

.info-value {
    font-size: 26rpx;
    color: #333;
    font-weight: 500;
}

.info-value.highlight {
    color: #1989fa;
}

.info-value.price {
    color: #ff6b6b;
    font-size: 28rpx;
    font-weight: bold;
}

.queue-card {
    display: flex;
    align-items: center;
    background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
    border-radius: 12rpx;
    padding: 24rpx;
}

.queue-left, .queue-right {
    flex: 1;
    text-align: center;
}

.queue-divider {
    width: 1rpx;
    height: 60rpx;
    background: rgba(25, 137, 250, 0.2);
}

.queue-label {
    display: block;
    font-size: 22rpx;
    color: #666;
    margin-bottom: 8rpx;
}

.queue-number {
    font-size: 36rpx;
    font-weight: bold;
    color: #1989fa;
}

.queue-wait {
    font-size: 36rpx;
    font-weight: bold;
    color: #ff6b6b;
}

.queue-arrow {
    font-size: 28rpx;
    color: #1989fa;
    margin-left: 16rpx;
}

.view-bill {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 16rpx;
    margin-top: 8rpx;
    border-top: 1rpx solid #f5f5f5;
    font-size: 26rpx;
    color: #1989fa;
}

.arrow {
    font-size: 28rpx;
    color: #ccc;
}

.action-list {
    margin-top: 8rpx;
}

.action-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
}

.action-item:last-child {
    border-bottom: none;
}

.action-icon {
    font-size: 36rpx;
    margin-right: 16rpx;
    width: 48rpx;
    text-align: center;
}

.action-info {
    flex: 1;
}

.action-name {
    display: block;
    font-size: 28rpx;
    color: #333;
    margin-bottom: 4rpx;
}

.action-desc {
    display: block;
    font-size: 22rpx;
    color: #999;
}

.action-item.danger .action-name {
    color: #ff4d4f;
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
