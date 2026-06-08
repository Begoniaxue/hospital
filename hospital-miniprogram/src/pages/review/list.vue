<template>
    <view class="review-list-container">
        <view class="header">
            <view class="header-title">我的评价</view>
            <view class="header-subtitle">查看您的就诊评价记录</view>
        </view>

        <view class="content" v-if="!loading">
            <view class="review-list" v-if="reviews.length > 0">
                <view 
                    class="review-card" 
                    v-for="review in reviews" 
                    :key="review.id"
                >
                    <view class="review-header">
                        <image class="doctor-avatar" :src="review.doctorAvatar || '/static/default-avatar.png'" mode="aspectFill" />
                        <view class="doctor-info">
                            <view class="doctor-row">
                                <text class="doctor-name">{{ review.doctorName }}</text>
                                <text class="department-name">{{ review.departmentName }}</text>
                            </view>
                            <view class="review-rating">
                                <text 
                                    class="star" 
                                    v-for="i in 5" 
                                    :key="i"
                                    :class="{ active: i <= review.rating }"
                                >★</text>
                            </view>
                        </view>
                        <text class="review-time">{{ review.createTime }}</text>
                    </view>
                    <view class="review-body">
                        <text class="review-content">{{ review.content }}</text>
                        <view class="review-images" v-if="review.images && review.images.length > 0">
                            <image 
                                class="review-image" 
                                v-for="(img, idx) in review.images" 
                                :key="idx"
                                :src="img"
                                mode="aspectFill"
                            />
                        </view>
                    </view>
                    <view class="review-reply" v-if="review.reply">
                        <view class="reply-label">
                            <text class="reply-icon">💬</text>
                            <text class="reply-title">管理员回复</text>
                        </view>
                        <text class="reply-content">{{ review.reply }}</text>
                    </view>
                </view>
            </view>
            <view class="empty-box" v-else>
                <text class="empty-icon">⭐</text>
                <text class="empty-text">暂无评价记录</text>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
import { getReviews } from '../../api/profile.js'

export default {
    data() {
        return {
            loading: false,
            reviews: [],
            patientId: ''
        }
    },
    onLoad() {
        const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        if (currentPatient) {
            this.patientId = currentPatient.id
            this.loadReviews()
        } else {
            uni.showToast({
                title: '请先完成实名认证',
                icon: 'none'
            })
        }
    },
    onShow() {
        if (this.patientId) {
            this.loadReviews()
        }
    },
    methods: {
        async loadReviews() {
            this.loading = true
            try {
                const res = await getReviews(this.patientId)
                if (res.code === 200) {
                    this.reviews = res.data || []
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
        }
    }
}
</script>

<style scoped>
.review-list-container {
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

.review-list {
    margin-top: 16rpx;
}

.review-card {
    background: #fff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    overflow: hidden;
}

.review-header {
    display: flex;
    align-items: flex-start;
    padding: 24rpx;
    border-bottom: 1rpx solid #f5f5f5;
}

.doctor-avatar {
    width: 88rpx;
    height: 88rpx;
    border-radius: 44rpx;
    background: #f5f5f5;
    margin-right: 20rpx;
    flex-shrink: 0;
}

.doctor-info {
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

.department-name {
    font-size: 24rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 2rpx 10rpx;
    border-radius: 6rpx;
}

.review-rating {
    display: flex;
    align-items: center;
}

.star {
    font-size: 28rpx;
    color: #ddd;
    margin-right: 4rpx;
}

.star.active {
    color: #ffc107;
}

.review-time {
    font-size: 22rpx;
    color: #999;
    flex-shrink: 0;
}

.review-body {
    padding: 24rpx;
}

.review-content {
    font-size: 26rpx;
    color: #333;
    line-height: 1.6;
}

.review-images {
    display: flex;
    flex-wrap: wrap;
    margin-top: 16rpx;
    gap: 12rpx;
}

.review-image {
    width: 160rpx;
    height: 160rpx;
    border-radius: 8rpx;
    background: #f5f5f5;
}

.review-reply {
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
