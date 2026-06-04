<template>
    <view class="queue-container">
        <view class="header">
            <view class="header-title">候诊排队</view>
            <view class="header-subtitle">{{ queueInfo.departmentName || '内科' }}</view>
        </view>

        <view class="content" v-if="!loading">
            <view class="current-call-card">
                <view class="call-label">当前叫号</view>
                <view class="call-number">{{ currentNumber || 'A008' }}</view>
                <view class="call-doctor">{{ queueInfo.doctorName || '张医生' }}</view>
            </view>

            <view class="my-number-card">
                <view class="my-info">
                    <text class="my-label">我的号码</text>
                    <text class="my-number">{{ queueInfo.myNumber || 'A012' }}</text>
                </view>
                <view class="my-divider"></view>
                <view class="my-info">
                    <text class="my-label">前方等待</text>
                    <text class="my-wait">{{ waitCount }}人</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">🏥</text>
                    诊室信息
                </view>
                <view class="info-row">
                    <text class="info-label">诊室位置</text>
                    <text class="info-value highlight">{{ queueInfo.floor || '3楼' }} {{ queueInfo.room || '305诊室' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊医生</text>
                    <text class="info-value">{{ queueInfo.doctorName || '张医生' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">医生职称</text>
                    <text class="info-value">{{ queueInfo.doctorTitle || '主任医师' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">预计等待</text>
                    <text class="info-value highlight">约 {{ estimatedTime }} 分钟</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📋</text>
                    排队列表
                </view>
                <view class="queue-list">
                    <view 
                        class="queue-item" 
                        v-for="(item, index) in queueList" 
                        :key="item.number"
                        :class="{ 
                            current: index === 0,
                            me: item.number === queueInfo.myNumber
                        }"
                    >
                        <view class="queue-status">
                            <view class="status-dot" v-if="index === 0"></view>
                            <text class="status-text" v-if="index === 0">就诊中</text>
                            <text class="status-number" v-else>{{ index }}</text>
                        </view>
                        <view class="queue-number">{{ item.number }}</view>
                        <view class="queue-name">{{ item.name }}</view>
                        <view class="queue-tag" v-if="item.number === queueInfo.myNumber">我的</view>
                    </view>
                </view>
            </view>

            <view class="tip-section">
                <view class="tip-title">
                    <text class="tip-icon">💡</text>
                    候诊提示
                </view>
                <view class="tip-list">
                    <text class="tip-item">1. 请在候诊区就坐，留意叫号广播</text>
                    <text class="tip-item">2. 过号请到分诊台重新排号</text>
                    <text class="tip-item">3. 请提前准备好就诊资料</text>
                    <text class="tip-item">4. 如需帮助请联系导诊护士</text>
                </view>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>

        <view class="footer">
            <button class="btn-refresh" @click="refresh">
                <text class="refresh-icon">🔄</text>
                刷新
            </button>
        </view>
    </view>
</template>

<script>
import { getQueueByRegistration, getCurrentNumber } from '../../api/queue.js'

export default {
    data() {
        return {
            loading: false,
            registrationId: '',
            doctorId: '',
            queueInfo: {},
            currentNumber: '',
            waitCount: 0,
            estimatedTime: 0,
            queueList: [],
            refreshTimer: null
        }
    },
    onLoad(options) {
        this.registrationId = options.registrationId
        this.doctorId = options.doctorId
        this.loadQueueInfo()
        this.startAutoRefresh()
    },
    onUnload() {
        this.stopAutoRefresh()
    },
    methods: {
        async loadQueueInfo() {
            this.loading = true
            try {
                const [queueRes, numberRes] = await Promise.all([
                    getQueueByRegistration(this.registrationId),
                    getCurrentNumber(this.doctorId, new Date().toISOString().split('T')[0])
                ])
                
                if (queueRes.code === 200) {
                    this.queueInfo = queueRes.data || {}
                    this.waitCount = this.queueInfo.waitCount || 0
                    this.estimatedTime = this.waitCount * 15
                    this.generateQueueList()
                }
                
                if (numberRes.code === 200) {
                    this.currentNumber = numberRes.data || ''
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

        generateQueueList() {
            const list = []
            const myNumber = this.queueInfo.myNumber || 'A012'
            const myIndex = parseInt(myNumber.substring(1))
            const currentIndex = parseInt((this.currentNumber || 'A008').substring(1))
            
            for (let i = 0; i < 10; i++) {
                const num = currentIndex + i
                const number = 'A' + String(num).padStart(3, '0')
                list.push({
                    number: number,
                    name: number === myNumber ? (this.queueInfo.patientName || '我') : '患者' + (i + 1)
                })
            }
            this.queueList = list
        },

        refresh() {
            this.loadQueueInfo()
            uni.showToast({
                title: '刷新成功',
                icon: 'success'
            })
        },

        startAutoRefresh() {
            this.refreshTimer = setInterval(() => {
                this.loadQueueInfo()
            }, 30000)
        },

        stopAutoRefresh() {
            if (this.refreshTimer) {
                clearInterval(this.refreshTimer)
                this.refreshTimer = null
            }
        }
    }
}
</script>

<style scoped>
.queue-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 120rpx;
    box-sizing: border-box;
}

.header {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    padding: 48rpx 32rpx;
    color: #fff;
    text-align: center;
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

.content {
    padding: 24rpx;
    margin-top: -16rpx;
}

.current-call-card {
    background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
    border-radius: 16rpx;
    padding: 40rpx 24rpx;
    text-align: center;
    color: #fff;
    margin-bottom: 20rpx;
}

.call-label {
    font-size: 26rpx;
    opacity: 0.9;
    margin-bottom: 16rpx;
}

.call-number {
    font-size: 80rpx;
    font-weight: bold;
    margin-bottom: 16rpx;
    letter-spacing: 8rpx;
}

.call-doctor {
    font-size: 26rpx;
    opacity: 0.9;
}

.my-number-card {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 16rpx;
    padding: 32rpx 24rpx;
    margin-bottom: 20rpx;
}

.my-info {
    flex: 1;
    text-align: center;
}

.my-label {
    display: block;
    font-size: 24rpx;
    color: #666;
    margin-bottom: 8rpx;
}

.my-number {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #1989fa;
}

.my-wait {
    display: block;
    font-size: 48rpx;
    font-weight: bold;
    color: #ff6b6b;
}

.my-divider {
    width: 1rpx;
    height: 80rpx;
    background: #e8e8e8;
}

.section {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
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
    padding: 12rpx 0;
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

.queue-list {
    margin-top: 8rpx;
}

.queue-item {
    display: flex;
    align-items: center;
    padding: 16rpx 20rpx;
    background: #f9f9f9;
    border-radius: 12rpx;
    margin-bottom: 12rpx;
}

.queue-item.current {
    background: #f6ffed;
    border: 2rpx solid #52c41a;
}

.queue-item.me {
    background: #e6f7ff;
    border: 2rpx solid #1989fa;
}

.queue-status {
    width: 48rpx;
    text-align: center;
    margin-right: 16rpx;
}

.status-dot {
    width: 16rpx;
    height: 16rpx;
    background: #52c41a;
    border-radius: 50%;
    margin: 0 auto 4rpx;
    animation: pulse 1.5s infinite;
}

@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}

.status-text {
    font-size: 20rpx;
    color: #52c41a;
}

.status-number {
    font-size: 24rpx;
    color: #999;
}

.queue-number {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    width: 120rpx;
}

.queue-item.current .queue-number {
    color: #52c41a;
}

.queue-item.me .queue-number {
    color: #1989fa;
}

.queue-name {
    flex: 1;
    font-size: 26rpx;
    color: #666;
}

.queue-tag {
    font-size: 20rpx;
    color: #1989fa;
    background: #fff;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    border: 1rpx solid #1989fa;
}

.tip-section {
    background: #fffbe6;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-top: 8rpx;
}

.tip-title {
    display: flex;
    align-items: center;
    font-size: 26rpx;
    font-weight: bold;
    color: #874d00;
    margin-bottom: 12rpx;
}

.tip-icon {
    margin-right: 8rpx;
    font-size: 24rpx;
}

.tip-list {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
}

.tip-item {
    font-size: 24rpx;
    color: #874d00;
    line-height: 1.6;
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

.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: #fff;
    padding: 20rpx 24rpx;
    box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.btn-refresh {
    width: 100%;
    height: 72rpx;
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    border-radius: 36rpx;
    font-size: 28rpx;
    border: none;
    line-height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

.refresh-icon {
    margin-right: 8rpx;
}
</style>
