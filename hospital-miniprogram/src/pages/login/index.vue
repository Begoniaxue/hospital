<template>
    <view class="login-container">
        <view class="login-header">
            <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
            <view class="title">智慧医院</view>
            <view class="subtitle">让就医更简单、更便捷</view>
        </view>

        <view class="login-content">
            <view class="tip">
                <text class="tip-icon">🔒</text>
                <text>登录即表示同意《用户协议》和《隐私政策》</text>
            </view>

            <button class="btn-wechat" @click="handleWechatLogin">
                <text class="wechat-icon">💬</text>
                微信一键登录
            </button>

            <view class="mock-login" v-if="isH5">
                <view class="divider">
                    <text class="divider-text">H5 测试登录</text>
                </view>
                <button class="btn-mock" @click="handleMockLogin">
                    <text class="mock-icon">🔓</text>
                    Mock 账号登录
                </button>
                <view class="mock-tip">
                    <text>登录后自动创建测试患者，可直接体验所有功能</text>
                </view>
            </view>

            <view class="other-login" v-if="false">
                <text class="other-text">其他登录方式</text>
                <view class="phone-login" @click="handlePhoneLogin">
                    手机号登录
                </view>
            </view>
        </view>
    </view>
</template>

<script>
import { wechatLogin } from '../../api/auth.js'

export default {
    data() {
        return {
            loading: false,
            isH5: false
        }
    },
    onLoad() {
        // #ifdef H5
        this.isH5 = true
        // #endif
    },
    methods: {
        async handleWechatLogin() {
            // #ifdef H5
            this.handleMockLogin()
            return
            // #endif

            if (this.loading) return
            this.loading = true

            uni.showLoading({ title: '登录中...' })

            try {
                const [userInfoRes, loginRes] = await Promise.all([
                    new Promise((resolve, reject) => {
                        uni.getUserProfile({
                            desc: '用于完善用户资料',
                            success: resolve,
                            fail: reject
                        })
                    }),
                    new Promise((resolve, reject) => {
                        uni.login({
                            provider: 'weixin',
                            success: resolve,
                            fail: reject
                        })
                    })
                ])

                const loginData = {
                    code: loginRes.code,
                    nickName: userInfoRes.userInfo.nickName,
                    avatarUrl: userInfoRes.userInfo.avatarUrl,
                    gender: userInfoRes.userInfo.gender
                }

                const res = await wechatLogin(loginData)

                if (res.code === 200) {
                    const { token, wechatUser } = res.data

                    this.$store.commit('SET_TOKEN', token)
                    this.$store.commit('SET_WECHAT_USER', wechatUser)

                    uni.setStorageSync('token', token)
                    uni.setStorageSync('wechatUser', wechatUser)

                    uni.hideLoading()

                    if (wechatUser.currentPatientId) {
                        await this.$store.dispatch('getCurrentPatient', wechatUser.currentPatientId)
                        uni.switchTab({
                            url: '/pages/index/index'
                        })
                    } else {
                        uni.showToast({
                            title: '请先完成实名认证',
                            icon: 'none'
                        })
                        setTimeout(() => {
                            uni.navigateTo({
                                url: '/pages/login/phone'
                            })
                        }, 1500)
                    }
                }
            } catch (e) {
                console.error('登录失败', e)
                uni.hideLoading()
                if (e.errMsg && e.errMsg.includes('cancel')) {
                    uni.showToast({
                        title: '已取消登录',
                        icon: 'none'
                    })
                } else {
                    this.handleMockLogin()
                }
            } finally {
                this.loading = false
            }
        },

        async handleMockLogin() {
            if (this.loading) return
            this.loading = true
            uni.showLoading({ title: '登录中...' })
            try {
                const loginData = {
                    code: 'mock_code_' + Date.now(),
                    nickName: '测试用户',
                    avatarUrl: 'https://thirdwx.qlogo.cn/mmopen/vi_32/default_avatar',
                    gender: 1
                }

                const res = await wechatLogin(loginData)

                if (res.code === 200) {
                    const { token, wechatUser } = res.data

                    this.$store.commit('SET_TOKEN', token)
                    this.$store.commit('SET_WECHAT_USER', wechatUser)

                    uni.setStorageSync('token', token)
                    uni.setStorageSync('wechatUser', wechatUser)

                    uni.hideLoading()

                    if (wechatUser.currentPatientId) {
                        await this.$store.dispatch('getCurrentPatient', wechatUser.currentPatientId)
                        uni.showToast({
                            title: '登录成功',
                            icon: 'success'
                        })
                        setTimeout(() => {
                            uni.switchTab({
                                url: '/pages/index/index'
                            })
                        }, 500)
                    } else {
                        uni.showToast({
                            title: '请先完成实名认证',
                            icon: 'none'
                        })
                        setTimeout(() => {
                            uni.navigateTo({
                                url: '/pages/login/phone'
                            })
                        }, 1500)
                    }
                }
            } catch (e) {
                console.error('Mock登录失败', e)
                uni.hideLoading()
                uni.showToast({
                    title: '登录失败，请检查后端服务',
                    icon: 'none'
                })
            } finally {
                this.loading = false
            }
        },

        handlePhoneLogin() {
            uni.navigateTo({
                url: '/pages/login/phone'
            })
        }
    }
}
</script>

<style scoped>
.login-container {
    min-height: 100vh;
    background: linear-gradient(180deg, #1989fa 0%, #f5f5f5 60%);
    display: flex;
    flex-direction: column;
    padding: 120rpx 60rpx 60rpx;
    box-sizing: border-box;
}

.login-header {
    text-align: center;
    margin-bottom: 120rpx;
}

.logo {
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 32rpx;
}

.title {
    font-size: 48rpx;
    font-weight: bold;
    color: #fff;
    margin-bottom: 16rpx;
}

.subtitle {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.85);
}

.login-content {
    background: #fff;
    border-radius: 24rpx;
    padding: 48rpx;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.tip {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    color: #999;
    margin-bottom: 48rpx;
}

.tip-icon {
    margin-right: 8rpx;
    font-size: 28rpx;
}

.btn-wechat {
    width: 100%;
    height: 96rpx;
    background: #07c160;
    color: #fff;
    border-radius: 48rpx;
    font-size: 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
}

.btn-wechat:active {
    opacity: 0.85;
}

.wechat-icon {
    margin-right: 16rpx;
    font-size: 36rpx;
}

.mock-login {
    margin-top: 48rpx;
    text-align: center;
}

.divider {
    display: flex;
    align-items: center;
    margin-bottom: 32rpx;
}

.divider::before,
.divider::after {
    content: '';
    flex: 1;
    height: 1px;
    background: #eee;
}

.divider-text {
    padding: 0 24rpx;
    font-size: 24rpx;
    color: #999;
}

.btn-mock {
    width: 100%;
    height: 96rpx;
    background: #ff6b6b;
    color: #fff;
    border-radius: 48rpx;
    font-size: 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
}

.btn-mock:active {
    opacity: 0.85;
}

.mock-icon {
    margin-right: 16rpx;
    font-size: 36rpx;
}

.mock-tip {
    margin-top: 24rpx;
    font-size: 24rpx;
    color: #999;
    line-height: 1.6;
}

.other-login {
    margin-top: 48rpx;
    text-align: center;
}

.other-text {
    font-size: 26rpx;
    color: #999;
    display: block;
    margin-bottom: 24rpx;
}

.phone-login {
    color: #1989fa;
    font-size: 28rpx;
}
</style>
