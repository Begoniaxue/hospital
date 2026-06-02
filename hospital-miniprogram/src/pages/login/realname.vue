<template>
    <view class="realname-container">
        <view class="header">
            <view class="title">实名认证</view>
            <view class="subtitle">
                实名信息将同步至医院 HIS 系统，用于挂号、缴费、取药等就医全流程
            </view>
        </view>

        <view class="form">
            <view class="form-item">
                <view class="label">姓名 <text class="required">*</text></view>
                <input
                    class="input"
                    placeholder="请输入真实姓名"
                    v-model="form.name"
                />
            </view>

            <view class="form-item">
                <view class="label">身份证号 <text class="required">*</text></view>
                <input
                    class="input"
                    placeholder="请输入18位身份证号"
                    maxlength="18"
                    v-model="form.idCard"
                />
            </view>

            <view class="form-item">
                <view class="label">性别</view>
                <view class="gender-group">
                    <view
                        class="gender-item"
                        :class="{ active: form.gender === 1 }"
                        @click="form.gender = 1"
                    >
                        男
                    </view>
                    <view
                        class="gender-item"
                        :class="{ active: form.gender === 0 }"
                        @click="form.gender = 0"
                    >
                        女
                    </view>
                </view>
            </view>

            <view class="form-item">
                <view class="label">出生日期</view>
                <picker
                    mode="date"
                    :value="form.birthday"
                    @change="onDateChange"
                >
                    <view class="picker-text">
                        {{ form.birthday || '请选择出生日期' }}
                    </view>
                </picker>
            </view>

            <view class="form-item">
                <view class="label">联系电话</view>
                <input
                    class="input"
                    type="number"
                    placeholder="请输入联系电话"
                    v-model="form.phone"
                    disabled
                />
            </view>

            <view class="form-item">
                <view class="label">现住址</view>
                <input
                    class="input"
                    placeholder="请输入现住址（选填）"
                    v-model="form.address"
                />
            </view>
        </view>

        <view class="notice">
            <view class="notice-title">📋 温馨提示</view>
            <view class="notice-item">• 请如实填写身份信息，确保就医权益</view>
            <view class="notice-item">• 已有就诊记录的患者将自动匹配原有档案</view>
            <view class="notice-item">• 新患者将自动在 HIS 系统生成就诊档案</view>
            <view class="notice-item">• 身份证号等敏感信息后端加密存储，不缓存本地</view>
        </view>

        <button
            class="btn-submit"
            :disabled="!canSubmit || loading"
            @click="handleSubmit"
        >
            {{ loading ? '提交中...' : '提交认证' }}
        </button>
    </view>
</template>

<script>
import { realNameAuth } from '../../api/auth.js'
import { validateIdCard } from '../../utils/index.js'

export default {
    data() {
        return {
            form: {
                wechatUserId: null,
                name: '',
                idCard: '',
                gender: 1,
                birthday: '',
                phone: '',
                address: ''
            },
            loading: false
        }
    },
    computed: {
        canSubmit() {
            return this.form.name.trim() !== '' && validateIdCard(this.form.idCard)
        }
    },
    onLoad(options) {
        if (options.phone) {
            this.form.phone = options.phone
        }
        const wechatUser = this.$store.state.wechatUser || uni.getStorageSync('wechatUser')
        if (wechatUser) {
            this.form.wechatUserId = wechatUser.id
            if (!this.form.phone && wechatUser.phone) {
                this.form.phone = wechatUser.phone
            }
        }

        if (!this.form.wechatUserId) {
            uni.showToast({
                title: '请先登录',
                icon: 'none'
            })
            setTimeout(() => {
                uni.reLaunch({ url: '/pages/login/index' })
            }, 1500)
        }
    },
    methods: {
        onDateChange(e) {
            this.form.birthday = e.detail.value
            if (this.form.idCard && this.form.idCard.length === 18 && !this.form.birthday) {
                const year = this.form.idCard.substring(6, 10)
                const month = this.form.idCard.substring(10, 12)
                const day = this.form.idCard.substring(12, 14)
                this.form.birthday = `${year}-${month}-${day}`
            }
            if (this.form.idCard && this.form.idCard.length === 18) {
                const genderCode = parseInt(this.form.idCard.substring(16, 17))
                this.form.gender = genderCode % 2 === 1 ? 1 : 0
            }
        },

        async handleSubmit() {
            if (!this.canSubmit || this.loading) return

            this.loading = true
            uni.showLoading({ title: '认证中...' })

            try {
                const res = await realNameAuth(this.form)

                if (res.code === 200) {
                    const { patient } = res.data
                    const wechatUser = this.$store.state.wechatUser || uni.getStorageSync('wechatUser')
                    wechatUser.currentPatientId = patient.id
                    this.$store.commit('SET_WECHAT_USER', wechatUser)
                    this.$store.commit('SET_CURRENT_PATIENT', patient)
                    this.$store.commit('UPDATE_CURRENT_PATIENT_ID', patient.id)
                    uni.setStorageSync('wechatUser', wechatUser)
                    uni.setStorageSync('currentPatient', patient)

                    uni.hideLoading()
                    uni.showToast({
                        title: res.data.message,
                        icon: 'success'
                    })

                    setTimeout(() => {
                        uni.switchTab({
                            url: '/pages/index/index'
                        })
                    }, 1500)
                }
            } catch (e) {
                uni.hideLoading()
                console.error('实名认证失败', e)
                uni.showToast({
                    title: e.message || '认证失败，请检查信息是否正确',
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
.realname-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 32rpx;
    padding-bottom: 160rpx;
    box-sizing: border-box;
}

.header {
    margin-bottom: 32rpx;
}

.title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 16rpx;
}

.subtitle {
    font-size: 26rpx;
    color: #999;
    line-height: 1.6;
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
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}

.form-item:last-child {
    border-bottom: none;
}

.label {
    width: 160rpx;
    font-size: 28rpx;
    color: #333;
    flex-shrink: 0;
}

.required {
    color: #ee0a24;
    margin-right: 4rpx;
}

.input {
    flex: 1;
    font-size: 30rpx;
    color: #333;
    height: 56rpx;
    line-height: 56rpx;
}

.input[disabled] {
    color: #999;
    background: transparent;
}

.gender-group {
    display: flex;
    gap: 24rpx;
    flex: 1;
}

.gender-item {
    flex: 1;
    height: 72rpx;
    line-height: 72rpx;
    text-align: center;
    border: 2rpx solid #e0e0e0;
    border-radius: 12rpx;
    font-size: 28rpx;
    color: #666;
}

.gender-item.active {
    border-color: #1989fa;
    color: #1989fa;
    background: rgba(25, 137, 250, 0.05);
}

.picker-text {
    flex: 1;
    font-size: 30rpx;
    color: #333;
    height: 56rpx;
    line-height: 56rpx;
}

.picker-text:empty::before {
    content: '请选择出生日期';
    color: #999;
}

.notice {
    background: #fffbe6;
    border-radius: 12rpx;
    padding: 24rpx;
    margin-bottom: 48rpx;
}

.notice-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #d48806;
    margin-bottom: 16rpx;
}

.notice-item {
    font-size: 24rpx;
    color: #874d00;
    line-height: 2;
}

.btn-submit {
    position: fixed;
    left: 32rpx;
    right: 32rpx;
    bottom: 60rpx;
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
