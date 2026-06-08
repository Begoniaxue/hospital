<template>
    <view class="submit-review-container">
        <view class="header">
            <view class="header-title">提交评价</view>
            <view class="header-subtitle">对本次就诊服务进行评价</view>
        </view>

        <view class="content">
            <view class="doctor-card">
                <image class="doctor-avatar" :src="doctorAvatar || '/static/default-avatar.png'" mode="aspectFill" />
                <view class="doctor-info">
                    <view class="doctor-row">
                        <text class="doctor-name">{{ doctorName }}</text>
                    </view>
                    <text class="department-name">{{ departmentName }}</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">⭐</text>
                    服务评分
                </view>
                <view class="rating-row">
                    <text 
                        class="star" 
                        v-for="i in 5" 
                        :key="i"
                        :class="{ active: i <= rating }"
                        @click="selectRating(i)"
                    >★</text>
                    <text class="rating-text">{{ getRatingText(rating) }}</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📝</text>
                    评价内容
                </view>
                <textarea 
                    class="review-input" 
                    v-model="content"
                    placeholder="请输入您的评价内容，分享您的就诊体验..."
                    :maxlength="500"
                />
                <view class="char-count">{{ content.length }}/500</view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📷</text>
                    上传图片
                </view>
                <view class="image-list">
                    <view class="image-item" v-for="(img, idx) in images" :key="idx">
                        <image class="image-preview" :src="img" mode="aspectFill" />
                        <view class="image-delete" @click="deleteImage(idx)">
                            <text class="delete-icon">×</text>
                        </view>
                    </view>
                    <view class="image-upload" v-if="images.length < 9" @click="chooseImage">
                        <text class="upload-icon">+</text>
                        <text class="upload-text">上传图片</text>
                    </view>
                </view>
            </view>
        </view>

        <view class="footer">
            <button class="submit-btn" :disabled="submitting" @click="submitReview">
                {{ submitting ? '提交中...' : '提交评价' }}
            </button>
        </view>
    </view>
</template>

<script>
import { submitReview } from '../../api/profile.js'

export default {
    data() {
        return {
            submitting: false,
            registrationId: '',
            doctorId: '',
            doctorName: '',
            departmentId: '',
            departmentName: '',
            doctorAvatar: '',
            rating: 5,
            content: '',
            images: [],
            patientId: ''
        }
    },
    onLoad(options) {
        this.registrationId = options.registrationId || ''
        this.doctorId = options.doctorId || ''
        this.doctorName = decodeURIComponent(options.doctorName || '')
        this.departmentId = options.departmentId || ''
        this.departmentName = decodeURIComponent(options.departmentName || '')
        this.doctorAvatar = options.doctorAvatar || ''

        const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        if (currentPatient) {
            this.patientId = currentPatient.id
        }
    },
    methods: {
        selectRating(rating) {
            this.rating = rating
        },

        getRatingText(rating) {
            const texts = {
                1: '非常差',
                2: '较差',
                3: '一般',
                4: '满意',
                5: '非常满意'
            }
            return texts[rating] || ''
        },

        chooseImage() {
            const remaining = 9 - this.images.length
            uni.chooseImage({
                count: remaining,
                success: (res) => {
                    this.images = this.images.concat(res.tempFilePaths)
                },
                fail: () => {
                    uni.showToast({
                        title: '选择图片失败',
                        icon: 'none'
                    })
                }
            })
        },

        deleteImage(idx) {
            this.images.splice(idx, 1)
        },

        async submitReview() {
            if (this.rating === 0) {
                uni.showToast({
                    title: '请选择评分',
                    icon: 'none'
                })
                return
            }
            if (!this.content.trim()) {
                uni.showToast({
                    title: '请输入评价内容',
                    icon: 'none'
                })
                return
            }
            if (!this.patientId) {
                uni.showToast({
                    title: '请先完成实名认证',
                    icon: 'none'
                })
                return
            }

            this.submitting = true
            try {
                const res = await submitReview({
                    patientId: this.patientId,
                    registrationId: this.registrationId,
                    doctorId: this.doctorId,
                    departmentId: this.departmentId,
                    rating: this.rating,
                    content: this.content,
                    images: this.images
                })
                if (res.code === 200) {
                    uni.showToast({
                        title: '评价提交成功',
                        icon: 'success'
                    })
                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1500)
                } else {
                    uni.showToast({
                        title: res.msg || '提交失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                uni.showToast({
                    title: '网络错误，请稍后重试',
                    icon: 'none'
                })
            } finally {
                this.submitting = false
            }
        }
    }
}
</script>

<style scoped>
.submit-review-container {
    min-height: 100vh;
    background: #f5f5f5;
    box-sizing: border-box;
    padding-bottom: 140rpx;
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

.doctor-card {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
}

.doctor-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50rpx;
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
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-right: 12rpx;
}

.department-name {
    font-size: 26rpx;
    color: #666;
    display: block;
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
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
}

.title-icon {
    margin-right: 8rpx;
    font-size: 28rpx;
}

.rating-row {
    display: flex;
    align-items: center;
    padding: 16rpx 0;
}

.star {
    font-size: 56rpx;
    color: #ddd;
    margin-right: 12rpx;
    transition: all 0.2s;
}

.star.active {
    color: #ffc107;
}

.rating-text {
    font-size: 26rpx;
    color: #ff6b6b;
    margin-left: 16rpx;
    font-weight: bold;
}

.review-input {
    width: 100%;
    min-height: 200rpx;
    background: #f8f8f8;
    border-radius: 12rpx;
    padding: 20rpx;
    font-size: 26rpx;
    color: #333;
    line-height: 1.6;
    box-sizing: border-box;
}

.char-count {
    text-align: right;
    font-size: 22rpx;
    color: #999;
    margin-top: 8rpx;
}

.image-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
}

.image-item {
    position: relative;
    width: 160rpx;
    height: 160rpx;
}

.image-preview {
    width: 100%;
    height: 100%;
    border-radius: 12rpx;
    background: #f5f5f5;
}

.image-delete {
    position: absolute;
    top: -12rpx;
    right: -12rpx;
    width: 40rpx;
    height: 40rpx;
    background: rgba(0, 0, 0, 0.6);
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

.delete-icon {
    font-size: 28rpx;
    color: #fff;
    line-height: 1;
}

.image-upload {
    width: 160rpx;
    height: 160rpx;
    background: #f8f8f8;
    border-radius: 12rpx;
    border: 2rpx dashed #ddd;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.upload-icon {
    font-size: 48rpx;
    color: #ccc;
    line-height: 1;
    margin-bottom: 8rpx;
}

.upload-text {
    font-size: 22rpx;
    color: #999;
}

.footer {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    background: #fff;
    padding: 20rpx 32rpx;
    padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
    box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.submit-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    border-radius: 44rpx;
    font-size: 30rpx;
    font-weight: bold;
    border: none;
}

.submit-btn[disabled] {
    opacity: 0.6;
}
</style>
