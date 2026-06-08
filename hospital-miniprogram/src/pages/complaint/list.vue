<template>
    <view class="complaint-list-container">
        <view class="header">
            <view class="header-content">
                <view class="header-text">
                    <view class="header-title">投诉建议</view>
                    <view class="header-subtitle">查看您的投诉和建议记录</view>
                </view>
                <view class="header-action" @tap="goToSubmit">
                    <text class="action-icon">+</text>
                    <text class="action-text">新增</text>
                </view>
            </view>
        </view>

        <view class="content" v-if="!loading">
            <view class="complaint-list" v-if="complaints.length > 0">
                <view 
                    class="complaint-card" 
                    v-for="item in complaints" 
                    :key="item.id"
                >
                    <view class="complaint-header">
                        <view class="title-row">
                            <text class="type-badge" :class="getTypeClass(item.type)">{{ getTypeText(item.type) }}</text>
                            <text class="complaint-title">{{ item.title }}</text>
                        </view>
                        <text class="status-badge" :class="getStatusClass(item.status)">{{ getStatusText(item.status) }}</text>
                    </view>
                    <view class="complaint-body">
                        <text class="complaint-content">{{ item.content }}</text>
                        <view class="image-list" v-if="item.images && item.images.length > 0">
                            <image 
                                class="image-thumb" 
                                v-for="(img, imgIdx) in item.images.split(',').filter(i => i)" 
                                :key="imgIdx" 
                                :src="img"
                                mode="aspectFill"
                            />
                        </view>
                        <text class="complaint-time">{{ formatTime(item.createTime) }}</text>
                    </view>
                    <view class="complaint-reply" v-if="item.reply">
                        <view class="reply-label">
                            <text class="reply-icon">💬</text>
                            <text class="reply-title">管理员回复</text>
                            <text class="reply-time" v-if="item.replyTime">{{ formatTime(item.replyTime) }}</text>
                        </view>
                        <text class="reply-content">{{ item.reply }}</text>
                    </view>
                </view>
            </view>
            <view class="empty-box" v-else>
                <text class="empty-icon">💭</text>
                <text class="empty-text">暂无投诉建议记录</text>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
import { getComplaints } from '../../api/profile.js'

export default {
    data() {
        return {
            loading: false,
            complaints: [],
            patientId: ''
        }
    },
    onLoad() {
        const currentPatient = this.$store && this.$store.state && this.$store.state.currentPatient
            ? this.$store.state.currentPatient
            : uni.getStorageSync('currentPatient')
        if (currentPatient && currentPatient.id) {
            this.patientId = currentPatient.id
        } else {
            this.patientId = 1
            uni.setStorageSync('currentPatient', { id: 1, name: '张三', phone: '13800138000' })
        }
        this.loadComplaints()
    },
    onShow() {
        if (this.patientId) {
            this.loadComplaints()
        }
    },
    methods: {
        goToSubmit() {
            uni.navigateTo({
                url: '/pages/complaint/submit'
            })
        },

        formatTime(timeStr) {
            if (!timeStr) return ''
            const date = new Date(timeStr)
            if (isNaN(date.getTime())) return timeStr
            const year = date.getFullYear()
            const month = String(date.getMonth() + 1).padStart(2, '0')
            const day = String(date.getDate()).padStart(2, '0')
            const hours = String(date.getHours()).padStart(2, '0')
            const minutes = String(date.getMinutes()).padStart(2, '0')
            return `${year}-${month}-${day} ${hours}:${minutes}`
        },

        async loadComplaints() {
            this.loading = true
            try {
                const res = await getComplaints(this.patientId)
                if (res.code === 200) {
                    this.complaints = res.data || []
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

        getTypeClass(type) {
            const classes = {
                1: 'type-complaint',
                2: 'type-suggestion'
            }
            return classes[type] || ''
        },

        getTypeText(type) {
            const texts = {
                1: '投诉',
                2: '建议'
            }
            return texts[type] || '未知'
        },

        getStatusClass(status) {
            const classes = {
                0: 'status-pending',
                1: 'status-processing',
                2: 'status-completed',
                3: 'status-closed'
            }
            return classes[status] || ''
        },

        getStatusText(status) {
            const texts = {
                0: '待处理',
                1: '处理中',
                2: '已处理',
                3: '已关闭'
            }
            return texts[status] || '未知'
        }
    }
}
</script>

<style scoped>
.complaint-list-container {
    min-height: 100vh;
    background: #f5f5f5;
    box-sizing: border-box;
}

.header {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    padding: 48rpx 32rpx;
    color: #fff;
}

.header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.header-text {
    flex: 1;
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

.header-action {
    display: flex;
    align-items: center;
    background: rgba(255, 255, 255, 0.2);
    padding: 12rpx 24rpx;
    border-radius: 32rpx;
}

.action-icon {
    font-size: 32rpx;
    margin-right: 6rpx;
    font-weight: bold;
}

.action-text {
    font-size: 26rpx;
}

.content {
    padding: 24rpx;
    margin-top: -16rpx;
}

.complaint-list {
    margin-top: 16rpx;
}

.complaint-card {
    background: #fff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    overflow: hidden;
}

.complaint-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    padding: 24rpx;
    border-bottom: 1rpx solid #f5f5f5;
}

.title-row {
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 0;
    margin-right: 16rpx;
}

.type-badge {
    font-size: 22rpx;
    padding: 4rpx 12rpx;
    border-radius: 6rpx;
    margin-right: 12rpx;
    flex-shrink: 0;
}

.type-complaint {
    color: #ff4d4f;
    background: #fff1f0;
}

.type-suggestion {
    color: #1989fa;
    background: #e6f7ff;
}

.complaint-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.status-badge {
    font-size: 22rpx;
    padding: 4rpx 12rpx;
    border-radius: 6rpx;
    flex-shrink: 0;
}

.status-pending {
    color: #faad14;
    background: #fffbe6;
}

.status-processing {
    color: #1989fa;
    background: #e6f7ff;
}

.status-completed {
    color: #52c41a;
    background: #f6ffed;
}

.status-closed {
    color: #8c8c8c;
    background: #f5f5f5;
}

.complaint-body {
    padding: 20rpx 24rpx;
}

.complaint-content {
    font-size: 26rpx;
    color: #333;
    line-height: 1.6;
    display: block;
    margin-bottom: 12rpx;
}

.image-list {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-bottom: 12rpx;
}

.image-thumb {
    width: 160rpx;
    height: 160rpx;
    border-radius: 8rpx;
    background: #f5f5f5;
}

.complaint-time {
    font-size: 22rpx;
    color: #999;
}

.complaint-reply {
    padding: 20rpx 24rpx;
    background: #fafafa;
    border-top: 1rpx solid #f5f5f5;
}

.reply-label {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.reply-icon {
    font-size: 24rpx;
    margin-right: 6rpx;
}

.reply-title {
    font-size: 24rpx;
    color: #1989fa;
    font-weight: bold;
}

.reply-time {
    font-size: 22rpx;
    color: #999;
    margin-left: auto;
}

.reply-content {
    font-size: 24rpx;
    color: #666;
    line-height: 1.6;
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
    padding: 120rpx 0;
}

.loading-text {
    font-size: 26rpx;
    color: #999;
}
</style>
