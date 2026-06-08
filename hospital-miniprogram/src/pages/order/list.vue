<template>
    <view class="order-list-container">
        <view class="header">
            <view class="header-title">我的订单</view>
            <view class="header-subtitle">查看您的所有订单记录</view>
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

        <scroll-view 
            class="content" 
            scroll-y 
            :refresher-enabled="true"
            :refresher-triggered="refreshing"
            @refresherrefresh="onRefresh"
            @scrolltolower="onLoadMore"
        >
            <view class="order-list" v-if="!loading && orders.length > 0">
                <view 
                    class="order-card" 
                    v-for="order in orders" 
                    :key="order.type + '-' + order.id"
                    @click="viewDetail(order)"
                >
                    <view class="order-header">
                        <text class="order-no">订单号：{{ order.orderNo }}</text>
                        <text class="order-status" :class="getStatusClass(order.type, order.status)">{{ order.statusName }}</text>
                    </view>
                    <view class="order-body">
                        <view class="order-info">
                            <view class="order-type">
                                <text class="type-icon">{{ getTypeIcon(order.type) }}</text>
                                <text class="type-text">{{ getTypeText(order.type) }}</text>
                            </view>
                            <view class="order-amount">
                                <text class="amount-label">金额</text>
                                <text class="amount-value">¥{{ order.amount }}</text>
                            </view>
                            <view class="order-time">
                                <text class="time-icon">🕒</text>
                                {{ formatTime(order.createTime) }}
                            </view>
                        </view>
                        <text class="arrow">›</text>
                    </view>
                </view>
            </view>
            <view class="empty-box" v-else-if="!loading">
                <text class="empty-icon">📋</text>
                <text class="empty-text">暂无{{ getTypeText(activeTab) }}订单</text>
            </view>

            <view class="loading-box" v-if="loading">
                <text class="loading-text">加载中...</text>
            </view>

            <view class="no-more" v-if="!loading && orders.length > 0 && noMore">
                <text class="no-more-text">没有更多了</text>
            </view>
        </scroll-view>
    </view>
</template>

<script>
import { getOrders } from '../../api/profile.js'

export default {
    data() {
        return {
            loading: false,
            refreshing: false,
            noMore: false,
            page: 0,
            pageSize: 20,
            activeTab: 'all',
            tabs: [
                { label: '全部', value: 'all' },
                { label: '挂号', value: 'registration' },
                { label: '缴费', value: 'settlement' },
                { label: '处方', value: 'prescription' },
                { label: '住院预缴', value: 'deposit' }
            ],
            orders: [],
            patientId: ''
        }
    },
    onLoad(options) {
        if (options && options.type) {
            this.activeTab = options.type
        }
        this.initPatient()
    },
    onShow() {
        if (this.patientId) {
            this.onRefresh()
        }
    },
    methods: {
        initPatient() {
            const currentPatient = this.$store && this.$store.state && this.$store.state.currentPatient
                ? this.$store.state.currentPatient
                : uni.getStorageSync('currentPatient')
            if (currentPatient && currentPatient.id) {
                this.patientId = currentPatient.id
                this.loadOrders()
            } else {
                this.patientId = 1
                uni.setStorageSync('currentPatient', { id: 1, name: '张三', phone: '13800138000' })
                this.loadOrders()
            }
        },

        async loadOrders() {
            this.loading = true
            try {
                const type = this.activeTab
                const res = await getOrders(this.patientId, type)
                if (res.code === 200) {
                    const list = (res.data && res.data.list) || res.data || []
                    if (this.page === 0) {
                        this.orders = list
                    } else {
                        this.orders = this.orders.concat(list)
                    }
                    const total = (res.data && res.data.total) || list.length
                    this.noMore = this.orders.length >= total || list.length < this.pageSize
                } else {
                    uni.showToast({
                        title: res.msg || res.message || '加载失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                console.error('加载订单失败:', e)
                uni.showToast({
                    title: '网络错误，请稍后重试',
                    icon: 'none'
                })
            } finally {
                this.loading = false
                this.refreshing = false
            }
        },

        switchTab(tab) {
            this.activeTab = tab
            this.page = 0
            this.noMore = false
            this.loadOrders()
        },

        onRefresh() {
            this.page = 0
            this.noMore = false
            this.refreshing = true
            this.loadOrders()
        },

        onLoadMore() {
            if (this.noMore || this.loading) return
            this.page++
            this.loadOrders()
        },

        getTypeIcon(type) {
            const icons = {
                'registration': '🏥',
                'settlement': '💳',
                'prescription': '💊',
                'deposit': '🛏️'
            }
            return icons[type] || '📋'
        },

        getTypeText(type) {
            const texts = {
                'all': '全部',
                'registration': '挂号',
                'settlement': '缴费',
                'prescription': '处方',
                'deposit': '住院预缴'
            }
            return texts[type] || type
        },

        getStatusClass(type, status) {
            if (type === 'registration') {
                if (status === 0) return 'status-pending-pay'
                if (status === 1) return 'status-paid'
                if (status === 2) return 'status-completed'
                if (status === 3) return 'status-cancelled'
                if (status === 4) return 'status-refunded'
            }
            if (type === 'settlement') {
                if (status === 0) return 'status-pending-pay'
                if (status === 1) return 'status-paid'
                if (status === 2) return 'status-refunded'
            }
            if (type === 'prescription') {
                if (status === 0) return 'status-pending-pay'
                if (status === 1) return 'status-paid'
                if (status === 2) return 'status-pending-pay'
                if (status === 3) return 'status-completed'
            }
            if (type === 'deposit') {
                if (status === 0) return 'status-pending-pay'
                if (status === 1) return 'status-paid'
                if (status === 2) return 'status-refunded'
            }
            return ''
        },

        formatTime(timeStr) {
            if (!timeStr) return ''
            const t = String(timeStr).replace('T', ' ')
            return t.substring(0, 16)
        },

        viewDetail(order) {
            uni.showToast({
                title: '订单号：' + order.orderNo,
                icon: 'none'
            })
        }
    }
}
</script>

<style scoped>
.order-list-container {
    min-height: 100vh;
    background: #f5f5f5;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
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
    flex: 1;
    padding: 0 24rpx 24rpx;
    box-sizing: border-box;
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

.status-pending-pay {
    color: #faad14;
    background: #fffbe6;
}

.status-paid {
    color: #1989fa;
    background: #e6f7ff;
}

.status-completed {
    color: #52c41a;
    background: #f6ffed;
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

.order-info {
    flex: 1;
    min-width: 0;
}

.order-type {
    display: flex;
    align-items: center;
    margin-bottom: 12rpx;
}

.type-icon {
    font-size: 28rpx;
    margin-right: 8rpx;
}

.type-text {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
}

.order-amount {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.amount-label {
    font-size: 24rpx;
    color: #999;
    margin-right: 12rpx;
}

.amount-value {
    font-size: 32rpx;
    color: #ff6b6b;
    font-weight: bold;
}

.order-time {
    display: flex;
    align-items: center;
    font-size: 22rpx;
    color: #999;
}

.time-icon {
    margin-right: 6rpx;
    font-size: 20rpx;
}

.arrow {
    font-size: 28rpx;
    color: #ccc;
    margin-left: 12rpx;
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
}

.loading-box {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 48rpx 0;
}

.loading-text {
    font-size: 26rpx;
    color: #999;
}

.no-more {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 32rpx 0;
}

.no-more-text {
    font-size: 24rpx;
    color: #ccc;
}
</style>
