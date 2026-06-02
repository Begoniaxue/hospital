<template>
    <view class="service-container">
        <view class="section">
            <view class="section-title">门诊服务</view>
            <view class="service-grid">
                <view class="service-item" @click="handleService('register')">
                    <view class="service-icon">📅</view>
                    <text class="service-text">预约挂号</text>
                </view>
                <view class="service-item" @click="handleService('payment')">
                    <view class="service-icon">💳</view>
                    <text class="service-text">门诊缴费</text>
                </view>
                <view class="service-item" @click="handleService('report')">
                    <view class="service-icon">📋</view>
                    <text class="service-text">报告查询</text>
                </view>
                <view class="service-item" @click="handleService('pharmacy')">
                    <view class="service-icon">💊</view>
                    <text class="service-text">药房取药</text>
                </view>
                <view class="service-item" @click="handleService('queue')">
                    <view class="service-icon">⏰</view>
                    <text class="service-text">叫号查询</text>
                </view>
                <view class="service-item" @click="handleService('checkin')">
                    <view class="service-icon">✅</view>
                    <text class="service-text">签到候诊</text>
                </view>
                <view class="service-item" @click="handleService('prescription')">
                    <view class="service-icon">🧾</view>
                    <text class="service-text">处方查询</text>
                </view>
                <view class="service-item" @click="handleService('bill')">
                    <view class="service-icon">📊</view>
                    <text class="service-text">费用明细</text>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="section-title">住院服务</view>
            <view class="service-grid">
                <view class="service-item" @click="handleService('admission')">
                    <view class="service-icon">🏨</view>
                    <text class="service-text">住院登记</text>
                </view>
                <view class="service-item" @click="handleService('inpatient-payment')">
                    <view class="service-icon">💰</view>
                    <text class="service-text">住院充值</text>
                </view>
                <view class="service-item" @click="handleService('inpatient-bill')">
                    <view class="service-icon">📄</view>
                    <text class="service-text">一日清单</text>
                </view>
                <view class="service-item" @click="handleService('discharge')">
                    <view class="service-icon">🚪</view>
                    <text class="service-text">出院结算</text>
                </view>
                <view class="service-item" @click="handleService('nurse-call')">
                    <view class="service-icon">👩‍⚕️</view>
                    <text class="service-text">护士呼叫</text>
                </view>
                <view class="service-item" @click="handleService('nurse-score')">
                    <view class="service-icon">⭐</view>
                    <text class="service-text">护理评价</text>
                </view>
                <view class="service-item" @click="handleService('visiting')">
                    <view class="service-icon">👨‍👩‍👧</view>
                    <text class="service-text">探视预约</text>
                </view>
                <view class="service-item" @click="handleService('meal')">
                    <view class="service-icon">🍱</view>
                    <text class="service-text">营养订餐</text>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="section-title">便民服务</view>
            <view class="service-grid">
                <view class="service-item" @click="handleService('health-card')">
                    <view class="service-icon">🪪</view>
                    <text class="service-text">电子健康卡</text>
                </view>
                <view class="service-item" @click="handleService('medical-insurance')">
                    <view class="service-icon">🏥</view>
                    <text class="service-text">医保凭证</text>
                </view>
                <view class="service-item" @click="handleService('navigation')">
                    <view class="service-icon">🧭</view>
                    <text class="service-text">院内导航</text>
                </view>
                <view class="service-item" @click="handleService('parking')">
                    <view class="service-icon">🅿️</view>
                    <text class="service-text">停车缴费</text>
                </view>
                <view class="service-item" @click="handleService('complaint')">
                    <view class="service-icon">📝</view>
                    <text class="service-text">投诉建议</text>
                </view>
                <view class="service-item" @click="handleService('consult')">
                    <view class="service-icon">💬</view>
                    <text class="service-text">在线咨询</text>
                </view>
                <view class="service-item" @click="handleService('appointment-list')">
                    <view class="service-icon">📆</view>
                    <text class="service-text">我的预约</text>
                </view>
                <view class="service-item" @click="handleService('more')">
                    <view class="service-icon">📌</view>
                    <text class="service-text">更多服务</text>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="section-title">健康管理</view>
            <view class="service-grid">
                <view class="service-item" @click="navigateTo('/pages/health/record')">
                    <view class="service-icon">📂</view>
                    <text class="service-text">健康档案</text>
                </view>
                <view class="service-item" @click="navigateTo('/pages/health/attachment')">
                    <view class="service-icon">📎</view>
                    <text class="service-text">健康附件</text>
                </view>
                <view class="service-item" @click="handleService('physical-exam')">
                    <view class="service-icon">🩺</view>
                    <text class="service-text">体检预约</text>
                </view>
                <view class="service-item" @click="handleService('vaccination')">
                    <view class="service-icon">💉</view>
                    <text class="service-text">疫苗接种</text>
                </view>
                <view class="service-item" @click="handleService('chronic-disease')">
                    <view class="service-icon">❤️</view>
                    <text class="service-text">慢病管理</text>
                </view>
                <view class="service-item" @click="handleService('health-education')">
                    <view class="service-icon">📚</view>
                    <text class="service-text">健康宣教</text>
                </view>
                <view class="service-item" @click="handleService('follow-up')">
                    <view class="service-icon">📞</view>
                    <text class="service-text">术后随访</text>
                </view>
                <view class="service-item" @click="handleService('health-reminder')">
                    <view class="service-icon">🔔</view>
                    <text class="service-text">健康提醒</text>
                </view>
            </view>
        </view>

        <view class="service-tip">
            <text class="tip-icon">💡</text>
            <text>如您在使用过程中遇到任何问题，请拨打服务热线：400-123-4567</text>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            services: {}
        }
    },
    methods: {
        handleService(type) {
            const typeMap = {
                'appointment-list': '/pages/appointment/list',
                'health-card': '/pages/family/qrcode',
                'medical-insurance': '/pages/family/qrcode'
            }

            if (typeMap[type]) {
                if (type === 'appointment-list') {
                    uni.showToast({
                        title: '功能开发中',
                        icon: 'none'
                    })
                    return
                }
                const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
                if (!currentPatient) {
                    uni.showToast({
                        title: '请先完成实名认证',
                        icon: 'none'
                    })
                    return
                }
                if (type === 'health-card' || type === 'medical-insurance') {
                    uni.navigateTo({
                        url: '/pages/family/qrcode?patientId=' + currentPatient.id + '&name=' + currentPatient.name
                    })
                    return
                }
            }

            uni.showToast({
                title: '功能开发中',
                icon: 'none'
            })
        },

        navigateTo(url) {
            uni.navigateTo({ url })
        }
    }
}
</script>

<style scoped>
.service-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 24rpx;
    padding-bottom: 48rpx;
    box-sizing: border-box;
}

.section {
    margin-bottom: 24rpx;
}

.section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 16rpx;
    padding-left: 8rpx;
    border-left: 6rpx solid #1989fa;
}

.service-grid {
    display: flex;
    flex-wrap: wrap;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx 16rpx;
}

.service-item {
    width: 25%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx 0;
}

.service-icon {
    width: 96rpx;
    height: 96rpx;
    border-radius: 24rpx;
    background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 44rpx;
    margin-bottom: 12rpx;
}

.service-text {
    font-size: 24rpx;
    color: #333;
    text-align: center;
}

.service-tip {
    display: flex;
    align-items: flex-start;
    background: #fffbe6;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-top: 24rpx;
    font-size: 24rpx;
    color: #874d00;
    line-height: 1.6;
}

.tip-icon {
    margin-right: 12rpx;
    flex-shrink: 0;
}
</style>
