<template>
    <view class="pay-container">
        <view class="header">
            <view class="header-title">挂号支付</view>
            <view class="header-subtitle">请完成支付以确认挂号</view>
        </view>

        <view class="content">
            <view class="amount-section">
                <view class="amount-label">支付金额</view>
                <view class="amount-value">¥{{ payData.fee }}</view>
                <view class="order-no">订单号：{{ payData.orderNo }}</view>
            </view>

            <view class="section">
                <view class="section-title">挂号信息</view>
                <view class="info-row">
                    <text class="info-label">就诊科室</text>
                    <text class="info-value">{{ payData.departmentName }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊医生</text>
                    <text class="info-value">{{ payData.doctorName }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊日期</text>
                    <text class="info-value">{{ payData.dateText }} {{ payData.period === 'morning' ? '上午' : '下午' }}</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">费用明细</view>
                <view class="fee-row">
                    <text class="fee-label">挂号费</text>
                    <text class="fee-value">¥{{ payData.fee }}</text>
                </view>
                <view class="fee-row total">
                    <text class="fee-label">应付金额</text>
                    <text class="fee-value">¥{{ payData.fee }}</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">选择支付方式</view>
                <view class="pay-method-list">
                    <view 
                        class="pay-method-item" 
                        v-for="method in payMethods" 
                        :key="method.id"
                        :class="{ selected: selectedPayMethod === method.id }"
                        @click="selectPayMethod(method.id)"
                    >
                        <text class="method-icon">{{ method.icon }}</text>
                        <view class="method-info">
                            <text class="method-name">{{ method.name }}</text>
                            <text class="method-desc" v-if="method.desc">{{ method.desc }}</text>
                        </view>
                        <view class="method-radio">
                            <view class="radio-dot" v-if="selectedPayMethod === method.id"></view>
                        </view>
                    </view>
                </view>
            </view>
        </view>

        <view class="footer">
            <view class="footer-info">
                <text class="footer-label">应付：</text>
                <text class="footer-price">¥{{ payData.fee }}</text>
            </view>
            <button class="btn-pay" @click="handlePay" :disabled="paying">
                {{ paying ? '支付中...' : '立即支付' }}
            </button>
        </view>
    </view>
</template>

<script>
import { mockPayRegistration, payRegistration } from '../../api/registration.js'

export default {
    data() {
        return {
            paying: false,
            payData: {},
            selectedPayMethod: 'wechat',
            useMock: true,
            payMethods: [
                {
                    id: 'wechat',
                    name: '微信余额',
                    icon: '💚',
                    desc: '微信钱包支付'
                },
                {
                    id: 'bank',
                    name: '银行卡',
                    icon: '💳',
                    desc: '储蓄卡/信用卡支付'
                },
                {
                    id: 'insurance',
                    name: '医保支付',
                    icon: '🏥',
                    desc: '医保个人账户支付'
                }
            ]
        }
    },
    onLoad(options) {
        if (options.data) {
            try {
                this.payData = JSON.parse(decodeURIComponent(options.data))
            } catch (e) {
                console.error('解析数据失败', e)
            }
        }
    },
    methods: {
        selectPayMethod(id) {
            this.selectedPayMethod = id
        },

        async handlePay() {
            if (!this.selectedPayMethod) {
                uni.showToast({
                    title: '请选择支付方式',
                    icon: 'none'
                })
                return
            }

            this.paying = true
            try {
                const res = await mockPayRegistration({
                    registrationId: this.payData.registrationId,
                    payMethod: this.selectedPayMethod,
                    amount: this.payData.fee
                })
                
                if (res.code === 200) {
                    uni.showToast({
                        title: '支付成功',
                        icon: 'success'
                    })
                    setTimeout(() => {
                        uni.redirectTo({
                            url: '/pages/register/success?data=' + encodeURIComponent(JSON.stringify({
                                ...this.payData,
                                ...res.data,
                                orderNo: res.data.registrationNo || this.payData.orderNo
                            }))
                        })
                    }, 500)
                } else {
                    uni.showToast({
                        title: res.msg || res.message || '支付失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                console.error('支付错误:', e)
                uni.showToast({
                    title: e.message || '网络错误，请稍后重试',
                    icon: 'none'
                })
            } finally {
                this.paying = false
            }
        }
    }
}
</script>

<style scoped>
.pay-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 140rpx;
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

.amount-section {
    background: #fff;
    border-radius: 16rpx;
    padding: 40rpx 24rpx;
    text-align: center;
    margin-bottom: 16rpx;
}

.amount-label {
    font-size: 26rpx;
    color: #666;
    margin-bottom: 12rpx;
}

.amount-value {
    font-size: 64rpx;
    font-weight: bold;
    color: #ff6b6b;
    margin-bottom: 16rpx;
}

.order-no {
    font-size: 22rpx;
    color: #999;
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
}

.info-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12rpx 0;
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

.fee-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12rpx 0;
}

.fee-row.total {
    border-top: 1rpx solid #f5f5f5;
    margin-top: 12rpx;
    padding-top: 20rpx;
}

.fee-label {
    font-size: 26rpx;
    color: #666;
}

.fee-row.total .fee-label {
    font-weight: bold;
    color: #333;
}

.fee-value {
    font-size: 26rpx;
    color: #333;
    font-weight: 500;
}

.fee-row.total .fee-value {
    font-size: 30rpx;
    color: #ff6b6b;
    font-weight: bold;
}

.pay-method-list {
    margin-top: 8rpx;
}

.pay-method-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
}

.pay-method-item:last-child {
    border-bottom: none;
}

.method-icon {
    font-size: 40rpx;
    margin-right: 16rpx;
    width: 60rpx;
    text-align: center;
}

.method-info {
    flex: 1;
}

.method-name {
    display: block;
    font-size: 28rpx;
    color: #333;
    margin-bottom: 4rpx;
}

.method-desc {
    display: block;
    font-size: 22rpx;
    color: #999;
}

.method-radio {
    width: 36rpx;
    height: 36rpx;
    border: 2rpx solid #ddd;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.pay-method-item.selected .method-radio {
    border-color: #1989fa;
}

.radio-dot {
    width: 20rpx;
    height: 20rpx;
    background: #1989fa;
    border-radius: 50%;
}

.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: #fff;
    padding: 20rpx 24rpx;
    box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.08);
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.footer-info {
    display: flex;
    align-items: baseline;
}

.footer-label {
    font-size: 26rpx;
    color: #666;
}

.footer-price {
    font-size: 36rpx;
    color: #ff6b6b;
    font-weight: bold;
}

.btn-pay {
    width: 280rpx;
    height: 72rpx;
    background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
    color: #fff;
    border-radius: 36rpx;
    font-size: 28rpx;
    border: none;
    line-height: 72rpx;
}

.btn-pay[disabled] {
    opacity: 0.5;
}
</style>
