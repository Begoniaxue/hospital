<template>
    <view class="phone-container">
        <view class="header">
            <view class="title">手机号验证</view>
            <view class="subtitle">为了您的账号安全，请验证手机号</view>
        </view>

        <view class="form">
            <view class="form-item">
                <view class="label">手机号</view>
                <view class="input-wrapper">
                    <text class="prefix">+86</text>
                    <input
                        class="input"
                        type="number"
                        maxlength="11"
                        placeholder="请输入手机号"
                        v-model="form.phone"
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
                        :disabled="countdown > 0 || !form.phone"
                        @click="handleSendCode"
                    >
                        {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
                    </button>
                </view>
            </view>
        </view>

        <view class="privacy">
            <text>📞 我们将严格保护您的隐私，手机号仅用于身份验证</text>
        </view>

        <button
            class="btn-submit"
            :disabled="!canSubmit || loading"
            @click="handleSubmit"
        >
            {{ loading ? '验证中...' : '下一步' }}
        </button>
    </view>
</template>

<script>
import { sendSmsCode, verifySmsCode } from '../../api/auth.js'
import { validatePhone } from '../../utils/index.js'

export default {
    data() {
        return {
            form: {
                phone: '',
                code: ''
            },
            countdown: 0,
            timer: null,
            loading: false
        }
    },
    computed: {
        canSubmit() {
            return validatePhone(this.form.phone) && this.form.code.length === 6
        }
    },
    onUnload() {
        if (this.timer) {
            clearInterval(this.timer)
        }
    },
    methods: {
        async handleSendCode() {
            if (!validatePhone(this.form.phone)) {
                uni.showToast({
                    title: '请输入正确的手机号',
                    icon: 'none'
                })
                return
            }

            try {
                const res = await sendSmsCode({
                    phone: this.form.phone,
                    smsType: 'LOGIN'
                })

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
                }
            } catch (e) {
                console.error('发送验证码失败', e)
            }
        },

        async handleSubmit() {
            if (!this.canSubmit || this.loading) return

            this.loading = true
            uni.showLoading({ title: '验证中...' })

            try {
                const res = await verifySmsCode({
                    phone: this.form.phone,
                    code: this.form.code,
                    smsType: 'LOGIN'
                })

                if (res.code === 200) {
                    const wechatUser = this.$store.state.wechatUser || uni.getStorageSync('wechatUser')
                    if (wechatUser) {
                        wechatUser.phone = this.form.phone
                        this.$store.commit('SET_WECHAT_USER', wechatUser)
                        uni.setStorageSync('wechatUser', wechatUser)
                    }

                    uni.hideLoading()
                    uni.navigateTo({
                        url: '/pages/login/realname?phone=' + this.form.phone
                    })
                }
            } catch (e) {
                console.error('验证码验证失败', e)
                uni.hideLoading()
                uni.showToast({
                    title: '验证码错误或已过期',
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
.phone-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 48rpx;
    box-sizing: border-box;
}

.header {
    margin-bottom: 64rpx;
}

.title {
    font-size: 40rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 16rpx;
}

.subtitle {
    font-size: 28rpx;
    color: #999;
}

.form {
    background: #fff;
    border-radius: 16rpx;
    padding: 0 32rpx;
    margin-bottom: 32rpx;
}

.form-item {
    padding: 32rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
}

.form-item:last-child {
    border-bottom: none;
}

.label {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 16rpx;
}

.input-wrapper {
    display: flex;
    align-items: center;
}

.prefix {
    font-size: 32rpx;
    color: #333;
    margin-right: 24rpx;
}

.input {
    flex: 1;
    font-size: 32rpx;
    color: #333;
    height: 64rpx;
    line-height: 64rpx;
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
}

.code-btn[disabled] {
    color: #999;
}

.privacy {
    font-size: 24rpx;
    color: #999;
    text-align: center;
    margin-bottom: 48rpx;
}

.btn-submit {
    width: 100%;
    height: 96rpx;
    background: #1989fa;
    color: #fff;
    border-radius: 48rpx;
    font-size: 32rpx;
    border: none;
}

.btn-submit[disabled] {
    background: #ccc;
}

.btn-submit:active:not([disabled]) {
    opacity: 0.85;
}
</style>
