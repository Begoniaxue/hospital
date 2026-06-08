<template>
    <view class="message-settings-container">
        <view class="header">
            <view class="header-title">消息设置</view>
            <view class="header-subtitle">管理您的通知偏好</view>
        </view>

        <view class="content" v-if="!loading">
            <view class="settings-list">
                <view class="setting-item">
                    <view class="setting-info">
                        <text class="setting-icon">📅</text>
                        <view class="setting-text">
                            <text class="setting-title">预约挂号通知</text>
                            <text class="setting-desc">预约成功、就诊提醒等</text>
                        </view>
                    </view>
                    <switch 
                        :checked="settings.appointment" 
                        color="#1989fa"
                        @change="toggleSetting('appointment')"
                    />
                </view>

                <view class="setting-item">
                    <view class="setting-info">
                        <text class="setting-icon">💰</text>
                        <view class="setting-text">
                            <text class="setting-title">缴费提醒通知</text>
                            <text class="setting-desc">待缴费、缴费成功等提醒</text>
                        </view>
                    </view>
                    <switch 
                        :checked="settings.payment" 
                        color="#1989fa"
                        @change="toggleSetting('payment')"
                    />
                </view>

                <view class="setting-item">
                    <view class="setting-info">
                        <text class="setting-icon">📋</text>
                        <view class="setting-text">
                            <text class="setting-title">报告领取通知</text>
                            <text class="setting-desc">检查报告出具提醒</text>
                        </view>
                    </view>
                    <switch 
                        :checked="settings.report" 
                        color="#1989fa"
                        @change="toggleSetting('report')"
                    />
                </view>

                <view class="setting-item">
                    <view class="setting-info">
                        <text class="setting-icon">📢</text>
                        <view class="setting-text">
                            <text class="setting-title">系统公告通知</text>
                            <text class="setting-desc">医院公告、停诊通知等</text>
                        </view>
                    </view>
                    <switch 
                        :checked="settings.announcement" 
                        color="#1989fa"
                        @change="toggleSetting('announcement')"
                    />
                </view>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>

        <view class="footer">
            <button class="save-btn" :disabled="saving" @click="saveSettings">
                {{ saving ? '保存中...' : '保存设置' }}
            </button>
        </view>
    </view>
</template>

<script>
import { getMessageSettings, saveMessageSettings } from '../../api/profile.js'

export default {
    data() {
        return {
            loading: false,
            saving: false,
            patientId: '',
            settings: {
                appointment: true,
                payment: true,
                report: true,
                announcement: true
            }
        }
    },
    onLoad() {
        const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        if (currentPatient) {
            this.patientId = currentPatient.id
            this.loadSettings()
        } else {
            uni.showToast({
                title: '请先完成实名认证',
                icon: 'none'
            })
        }
    },
    methods: {
        async loadSettings() {
            this.loading = true
            try {
                const res = await getMessageSettings(this.patientId)
                if (res.code === 200 && res.data) {
                    this.settings = {
                        appointment: res.data.appointment !== false,
                        payment: res.data.payment !== false,
                        report: res.data.report !== false,
                        announcement: res.data.announcement !== false
                    }
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

        toggleSetting(key) {
            this.settings[key] = !this.settings[key]
        },

        async saveSettings() {
            this.saving = true
            try {
                const res = await saveMessageSettings(this.patientId, this.settings)
                if (res.code === 200) {
                    uni.showToast({
                        title: '保存成功',
                        icon: 'success'
                    })
                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1500)
                } else {
                    uni.showToast({
                        title: res.msg || '保存失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                uni.showToast({
                    title: '网络错误，请稍后重试',
                    icon: 'none'
                })
            } finally {
                this.saving = false
            }
        }
    }
}
</script>

<style scoped>
.message-settings-container {
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

.settings-list {
    background: #fff;
    border-radius: 16rpx;
    overflow: hidden;
}

.setting-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 28rpx 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
}

.setting-item:last-child {
    border-bottom: none;
}

.setting-info {
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 0;
}

.setting-icon {
    font-size: 40rpx;
    margin-right: 20rpx;
    width: 56rpx;
    text-align: center;
    flex-shrink: 0;
}

.setting-text {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
}

.setting-title {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
    margin-bottom: 4rpx;
}

.setting-desc {
    font-size: 22rpx;
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

.save-btn {
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

.save-btn[disabled] {
    opacity: 0.6;
}
</style>
