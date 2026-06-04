<template>
    <view class="success-container">
        <view class="success-icon">
            <text class="icon">✅</text>
        </view>
        <view class="success-title">挂号成功</view>
        <view class="success-subtitle">请按时就诊，祝您早日康复</view>

        <view class="section">
            <view class="info-card">
                <view class="info-header">
                    <text class="info-icon">📋</text>
                    <text class="info-title">挂号信息</text>
                </view>
                <view class="info-row">
                    <text class="info-label">挂号单号</text>
                    <text class="info-value">{{ successData.orderNo || '-' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊科室</text>
                    <text class="info-value">{{ successData.departmentName }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊医生</text>
                    <text class="info-value">{{ successData.doctorName }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊日期</text>
                    <text class="info-value">{{ successData.dateText }} {{ successData.period === 'morning' ? '上午' : '下午' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊时段</text>
                    <text class="info-value">{{ successData.period === 'morning' ? '8:00-12:00' : '14:00-17:30' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊楼层</text>
                    <text class="info-value">{{ successData.floor || '3楼' }} {{ successData.room || '305诊室' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">挂号费用</text>
                    <text class="info-value price">¥{{ successData.fee }}</text>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="tip-card">
                <view class="tip-header">
                    <text class="tip-icon">💡</text>
                    <text class="tip-title">就诊提醒</text>
                </view>
                <view class="tip-list">
                    <text class="tip-item">1. 请在就诊前30分钟到达医院取号签到</text>
                    <text class="tip-item">2. 请携带有效身份证件和医保卡</text>
                    <text class="tip-item">3. 如需取消预约，请在就诊前24小时操作</text>
                    <text class="tip-item">4. 首次就诊请提前到服务台建档</text>
                </view>
            </view>
        </view>

        <view class="btn-group">
            <button class="btn-primary" @click="goToDetail">
                查看订单
            </button>
            <button class="btn-secondary" @click="goHome">
                返回首页
            </button>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            successData: {}
        }
    },
    onLoad(options) {
        if (options.data) {
            try {
                this.successData = JSON.parse(decodeURIComponent(options.data))
            } catch (e) {
                console.error('解析数据失败', e)
            }
        }
    },
    methods: {
        goToDetail() {
            uni.redirectTo({
                url: '/pages/appointment/detail?id=' + this.successData.registrationId
            })
        },
        goHome() {
            uni.switchTab({
                url: '/pages/index/index'
            })
        }
    }
}
</script>

<style scoped>
.success-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 48rpx 24rpx;
    box-sizing: border-box;
}

.success-icon {
    text-align: center;
    margin-bottom: 24rpx;
}

.icon {
    font-size: 120rpx;
}

.success-title {
    text-align: center;
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 12rpx;
}

.success-subtitle {
    text-align: center;
    font-size: 26rpx;
    color: #666;
    margin-bottom: 40rpx;
}

.section {
    margin-bottom: 24rpx;
}

.info-card, .tip-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
}

.info-header, .tip-header {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;
    padding-bottom: 16rpx;
    border-bottom: 1rpx solid #f5f5f5;
}

.info-icon, .tip-icon {
    font-size: 28rpx;
    margin-right: 8rpx;
}

.info-title, .tip-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
}

.info-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14rpx 0;
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

.info-value.price {
    color: #ff6b6b;
    font-size: 28rpx;
    font-weight: bold;
}

.tip-list {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
}

.tip-item {
    font-size: 24rpx;
    color: #666;
    line-height: 1.6;
}

.btn-group {
    margin-top: 40rpx;
}

.btn-primary {
    width: 100%;
    height: 88rpx;
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    border-radius: 44rpx;
    font-size: 30rpx;
    margin-bottom: 20rpx;
    border: none;
}

.btn-secondary {
    width: 100%;
    height: 88rpx;
    background: #fff;
    color: #1989fa;
    border-radius: 44rpx;
    font-size: 30rpx;
    border: 2rpx solid #1989fa;
}
</style>
