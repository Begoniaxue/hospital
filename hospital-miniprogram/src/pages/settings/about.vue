<template>
    <view class="about-container">
        <view class="header">
            <view class="header-title">关于医院</view>
            <view class="header-subtitle">了解我们的医院</view>
        </view>

        <view class="content" v-if="!loading">
            <view class="hospital-card">
                <image class="hospital-logo" :src="aboutData.logo || '/static/hospital-logo.png'" mode="aspectFit" />
                <text class="hospital-name">{{ aboutData.name || '智慧医院' }}</text>
                <text class="hospital-level" v-if="aboutData.level">{{ aboutData.level }}</text>
            </view>

            <view class="info-section" v-if="aboutData.intro">
                <view class="section-title">
                    <text class="title-icon">🏥</text>
                    医院简介
                </view>
                <view class="section-content">
                    <text class="content-text">{{ aboutData.intro }}</text>
                </view>
            </view>

            <view class="info-list">
                <view class="info-item" v-if="aboutData.address">
                    <text class="info-icon">📍</text>
                    <view class="info-content">
                        <text class="info-label">医院地址</text>
                        <text class="info-value">{{ aboutData.address }}</text>
                    </view>
                </view>

                <view class="info-item" v-if="aboutData.phone" @click="callPhone">
                    <text class="info-icon">📞</text>
                    <view class="info-content">
                        <text class="info-label">联系电话</text>
                        <text class="info-value link">{{ aboutData.phone }}</text>
                    </view>
                    <text class="info-arrow">›</text>
                </view>

                <view class="info-item" v-if="aboutData.website" @click="openWebsite">
                    <text class="info-icon">🌐</text>
                    <view class="info-content">
                        <text class="info-label">官方网站</text>
                        <text class="info-value link">{{ aboutData.website }}</text>
                    </view>
                    <text class="info-arrow">›</text>
                </view>

                <view class="info-item" v-if="aboutData.workTime">
                    <text class="info-icon">⏰</text>
                    <view class="info-content">
                        <text class="info-label">工作时间</text>
                        <text class="info-value">{{ aboutData.workTime }}</text>
                    </view>
                </view>
            </view>

            <view class="version-info">
                <text class="version-text">当前版本 v1.0.0</text>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
import { getAbout } from '../../api/profile.js'

export default {
    data() {
        return {
            loading: false,
            aboutData: {
                name: '',
                logo: '',
                level: '',
                intro: '',
                address: '',
                phone: '',
                website: '',
                workTime: ''
            }
        }
    },
    onLoad() {
        this.loadAbout()
    },
    methods: {
        async loadAbout() {
            this.loading = true
            try {
                const res = await getAbout()
                if (res.code === 200 && res.data) {
                    this.aboutData = {
                        name: res.data.name || '智慧医院',
                        logo: res.data.logo || '',
                        level: res.data.level || '',
                        intro: res.data.intro || '智慧医院是一家集医疗、教学、科研、预防、保健、康复为一体的现代化综合性医院。医院秉承"以患者为中心"的服务理念，致力于为广大患者提供优质、高效、便捷的医疗服务。',
                        address: res.data.address || '',
                        phone: res.data.phone || '',
                        website: res.data.website || '',
                        workTime: res.data.workTime || ''
                    }
                } else {
                    this.aboutData.intro = '智慧医院是一家集医疗、教学、科研、预防、保健、康复为一体的现代化综合性医院。医院秉承"以患者为中心"的服务理念，致力于为广大患者提供优质、高效、便捷的医疗服务。'
                }
            } catch (e) {
                this.aboutData.intro = '智慧医院是一家集医疗、教学、科研、预防、保健、康复为一体的现代化综合性医院。医院秉承"以患者为中心"的服务理念，致力于为广大患者提供优质、高效、便捷的医疗服务。'
            } finally {
                this.loading = false
            }
        },

        callPhone() {
            if (this.aboutData.phone) {
                uni.makePhoneCall({
                    phoneNumber: this.aboutData.phone,
                    fail: () => {
                        uni.showToast({
                            title: '拨号失败',
                            icon: 'none'
                        })
                    }
                })
            }
        },

        openWebsite() {
            if (this.aboutData.website) {
                uni.setClipboardData({
                    data: this.aboutData.website,
                    success: () => {
                        uni.showToast({
                            title: '网址已复制',
                            icon: 'success'
                        })
                    }
                })
            }
        }
    }
}
</script>

<style scoped>
.about-container {
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

.hospital-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 48rpx 32rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 16rpx;
}

.hospital-logo {
    width: 140rpx;
    height: 140rpx;
    border-radius: 24rpx;
    background: #e6f7ff;
    margin-bottom: 20rpx;
}

.hospital-name {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 8rpx;
}

.hospital-level {
    font-size: 24rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 4rpx 16rpx;
    border-radius: 6rpx;
}

.info-section {
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

.section-content {
    padding: 8rpx 0;
}

.content-text {
    font-size: 26rpx;
    color: #666;
    line-height: 1.8;
}

.info-list {
    background: #fff;
    border-radius: 16rpx;
    overflow: hidden;
    margin-bottom: 32rpx;
}

.info-item {
    display: flex;
    align-items: center;
    padding: 28rpx 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
}

.info-item:last-child {
    border-bottom: none;
}

.info-icon {
    font-size: 36rpx;
    margin-right: 20rpx;
    width: 48rpx;
    text-align: center;
    flex-shrink: 0;
}

.info-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

.info-label {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 4rpx;
}

.info-value {
    font-size: 28rpx;
    color: #333;
}

.info-value.link {
    color: #1989fa;
}

.info-arrow {
    font-size: 32rpx;
    color: #ccc;
    margin-left: 12rpx;
}

.version-info {
    display: flex;
    justify-content: center;
    padding: 24rpx 0;
}

.version-text {
    font-size: 24rpx;
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
