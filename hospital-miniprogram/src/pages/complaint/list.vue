<template>
    <view class="complaint-list-container">
        <view class="header">
            <view class="header-title">投诉建议</view>
            <view class="header-subtitle">查看您的投诉和建议记录</view>
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
                        <text class="complaint-time">{{ item.createTime }}</text>
                    </view>
                    <view class="complaint-reply" v-if="item.reply">
                        <view class="reply-label">
                            <text class="reply-icon">💬</text>
                            <text class="reply-title">管理员回复</text>
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
        const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        if (currentPatient) {
            this.patientId = currentPatient.id
            this.loadComplaints()
        } else {
            uni.showToast({
                title: '请先完成实名认证',
                icon: 'none'
            })
        }
    },
    onShow() {
        if (this.patientId) {
            this.loadComplaints()
        }
    },
    methods: {
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
                'complaint': 'type-complaint',
                'suggestion': 'type-suggestion'
            }
            return classes[type] || ''
        },

        getTypeText(type) {
            const texts = {
                'complaint': '投诉',
                'suggestion': '建议'
            }
            return texts[type] || type
        },

        getStatusClass(status) {
            const classes = {
                'pending': 'status-pending',
                'processing': 'status-processing',
                'completed': 'status-completed',
                'closed': 'status-closed'
            }
            return classes[status] || ''
        },

        getStatusText(status) {
            const texts = {
                'pending': '待处理',
                'processing': '处理中',
                'completed': '已处理',
                'closed': '已关闭'
            }
            return texts[status] || status
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
