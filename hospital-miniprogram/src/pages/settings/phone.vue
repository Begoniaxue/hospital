<template>
    <view class="change-phone-container">
        <view class="header">
            <view class="header-title">修改手机号</view>
            <view class="header-subtitle">为了账号安全，请先验证原手机号</view>
        </view>

        <view class="content">
            <view class="form-section">
                <view class="form-item">
                    <view class="label">原手机号</view>
                    <view class="input-wrapper">
                        <text class="prefix">+86</text>
                        <input
                            class="input"
                            type="number"
                            maxlength="11"
                            placeholder="请输入原手机号"
                            v-model="form.oldPhone"
                        />
                    </view>
                </view>

                <view class="form-item">
                    <view class="label">验证码</view>
                    <view class="input-wrapper code-wrapper">
                        <input
                            class="input"
                            type="number"
                            maxlength="6"
                            placeholder="请输入验证码"
                            v-model="form.code"
                        />
                        <button
                            class="code-btn"
                            :disabled="countdown > 0 || !canSendCode"
                            @click="handleSendCode"
                        >
                            {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
                        </button>
                    </view>
                </view>

                <view class="form-item">
                    <view class="label">新手机号</view>
                    <view class="input-wrapper">
                        <text class="prefix">+86</text>
                        <input
                            class="input"
                            type="number"
                            maxlength="11"
                            placeholder="请输入新手机号"
                            v-model="form.newPhone"
                        />
                    </view>
                </view>
            </view>

            <view class="tip-box">
                <text class="tip-icon">🔒</text>
                <text class="tip-text">修改手机号需要验证原手机号，验证码将发送至原手机号。</text>
            </view>
        </view>

        <view class="footer">
            <button class="submit-btn" :disabled="!canSubmit || submitting" @click="handleSubmit">
                {{ submitting ? '提交中...' : '确认修改' }}
            </button>
        </view>
    </view>
</template>

<script>
import { sendSmsCode, updatePhone } from '../../api/profile.js'
import { validatePhone } from '../../utils/index.js'

export default {
    data() {
        return {
            submitting: false,
            countdown: 0,
            timer: null,
            form: {
                oldPhone: '',
                code: '',
                newPhone: ''
            }
        }
    },
    computed: {
        canSendCode() {
            return validatePhone(this.form.oldPhone)
        },
        canSubmit() {
            return validatePhone(this.form.oldPhone) 
                && validatePhone(this.form.newPhone) 
                && this.form.code.length === 6
                && this.form.oldPhone !== this.form.newPhone
        }
    },
    onUnload() {
        if (this.timer) {
            clearInterval(this.timer)
        }
    },
    methods: {
        async handleSendCode() {
            if (!validatePhone(this.form.oldPhone)) {
                uni.showToast({
                    title: '请输入正确的手机号',
                    icon: 'none'
                })
                return
            }

            try {
                const res = await sendSmsCode(this.form.oldPhone)
                if (res.code === 200) {
                    uni.showToast({
                        title: '验证码已发送',
                        icon: 'success'
                    })

                    this.countdown = 60
                    this.timer = setInterval(() => {
                        this.countdown--
                        if (this.countdown <= 0) {
                            clearInterval(this.timer)
                            this.timer = null
                        }
                    }, 1000)
                } else {
                    uni.showToast({
                        title: res.msg || '发送失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                uni.showToast({
                    title: '网络错误，请稍后重试',
                    icon: 'none'
                })
            }
        },

        async handleSubmit() {
            if (!this.canSubmit || this.submitting) return

            if (this.form.oldPhone === this.form.newPhone) {
                uni.showToast({
                    title: '新手机号不能与原手机号相同',
                    icon: 'none'
                })
                return
            }

            this.submitting = true
            try {
                const res = await updatePhone({
                    oldPhone: this.form.oldPhone,
                    newPhone: this.form.newPhone,
                    code: this.form.code
                })
                if (res.code === 200) {
                    uni.showToast({
                        title: '修改成功',
                        icon: 'success'
                    })

                    const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
                    if (currentPatient) {
                        currentPatient.phone = this.form.newPhone
                        this.$store.commit('SET_CURRENT_PATIENT', currentPatient)
                        uni.setStorageSync('currentPatient', currentPatient)
                    }

                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1500)
                } else {
                    uni.showToast({
                        title: res.msg || '修改失败',
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
.change-phone-container {
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

.form-section {
    background: #fff;
    border-radius: 16rpx;
    padding: 0 24rpx;
    margin-bottom: 24rpx;
}

.form-item {
    padding: 28rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
}

.form-item:last-child {
    border-bottom: none;
}

.label {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 16rpx;
    font-weight: 500;
}

.input-wrapper {
    display: flex;
    align-items: center;
    background: #f8f8f8;
    border-radius: 12rpx;
    padding: 0 20rpx;
    height: 80rpx;
}

.prefix {
    font-size: 30rpx;
    color: #333;
    margin-right: 20rpx;
}

.input {
    flex: 1;
    font-size: 30rpx;
    color: #333;
    height: 80rpx;
    line-height: 80rpx;
}

.code-wrapper {
    justify-content: space-between;
}

.code-btn {
    font-size: 26rpx;
    color: #1989fa;
    background: transparent;
    border: none;
    padding: 0;
    margin: 0;
    min-width: 160rpx;
    text-align: right;
    line-height: 80rpx;
    height: 80rpx;
}

.code-btn[disabled] {
    color: #999;
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
