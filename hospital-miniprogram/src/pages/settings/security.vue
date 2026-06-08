<template>
    <view class="security-container">
        <view class="header">
            <view class="header-title">账号安全</view>
            <view class="header-subtitle">保护您的账号安全</view>
        </view>

        <view class="content">
            <view class="security-list">
                <view class="security-item" @click="goToChangePhone">
                    <view class="security-info">
                        <text class="security-icon">📱</text>
                        <view class="security-text">
                            <text class="security-title">绑定手机号</text>
                            <text class="security-desc">{{ maskedPhone }}</text>
                        </view>
                    </view>
                    <view class="security-action">
                        <text class="action-text">修改</text>
                        <text class="action-arrow">›</text>
                    </view>
                </view>
            </view>

            <view class="tip-box">
                <text class="tip-icon">🔒</text>
                <text class="tip-text">请妥善保管您的账号信息，定期更换密码以保障账号安全。</text>
            </view>
        </view>
    </view>
</template>

<script>
import { maskPhone } from '../../utils/index.js'

export default {
    data() {
        return {
            phone: ''
        }
    },
    computed: {
        maskedPhone() {
            return maskPhone(this.phone) || '未绑定'
        }
    },
    onLoad() {
        const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        if (currentPatient) {
            this.phone = currentPatient.phone || ''
        } else {
            uni.showToast({
                title: '请先完成实名认证',
                icon: 'none'
            })
        }
    },
    methods: {
        goToChangePhone() {
            uni.navigateTo({
                url: '/pages/settings/phone'
            })
        }
    }
}
</script>

<style scoped>
.security-container {
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

.security-list {
    background: #fff;
    border-radius: 16rpx;
    overflow: hidden;
    margin-bottom: 24rpx;
}

.security-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 28rpx 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
}

.security-item:last-child {
    border-bottom: none;
}

.security-info {
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 0;
}

.security-icon {
    font-size: 40rpx;
    margin-right: 20rpx;
    width: 56rpx;
    text-align: center;
    flex-shrink: 0;
}

.security-text {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

.security-title {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
    margin-bottom: 4rpx;
}

.security-desc {
    font-size: 24rpx;
    color: #999;
}

.security-action {
    display: flex;
    align-items: center;
    flex-shrink: 0;
}

.action-text {
    font-size: 26rpx;
    color: #1989fa;
    margin-right: 4rpx;
}

.action-arrow {
    font-size: 32rpx;
    color: #ccc;
}

.tip-box {
    display: flex;
    align-items: flex-start;
    background: #fffbe6;
    border-radius: 12rpx;
    padding: 20rpx;
}

.tip-icon {
    font-size: 28rpx;
    margin-right: 12rpx;
    flex-shrink: 0;
    margin-top: 2rpx;
}

.tip-text {
    font-size: 24rpx;
    color: #ad6800;
    line-height: 1.6;
    flex: 1;
}
</style>
