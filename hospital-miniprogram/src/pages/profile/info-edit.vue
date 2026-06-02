<template>
    <view class="edit-container">
        <view class="form">
            <view class="form-item">
                <view class="label">手机号</view>
                <input
                    class="input"
                    type="number"
                    placeholder="请输入手机号"
                    maxlength="11"
                    v-model="form.phone"
                />
            </view>

            <view class="form-item">
                <view class="label">现住址</view>
                <input
                    class="input"
                    placeholder="请输入现住址"
                    v-model="form.address"
                />
            </view>

            <view class="form-item">
                <view class="label">过敏史</view>
                <input
                    class="input"
                    placeholder="如：青霉素、海鲜过敏等"
                    v-model="form.allergyHistory"
                />
            </view>

            <view class="form-item">
                <view class="label">既往病史</view>
                <input
                    class="input"
                    placeholder="如：高血压、糖尿病等"
                    v-model="form.medicalHistory"
                />
            </view>

            <view class="form-item">
                <view class="label">紧急联系人</view>
                <input
                    class="input"
                    placeholder="请输入紧急联系人姓名"
                    v-model="form.emergencyContact"
                />
            </view>

            <view class="form-item">
                <view class="label">紧急联系电话</view>
                <input
                    class="input"
                    type="number"
                    placeholder="请输入紧急联系电话"
                    maxlength="11"
                    v-model="form.emergencyPhone"
                />
            </view>

            <view class="form-item">
                <view class="label">备注</view>
                <textarea
                    class="textarea"
                    placeholder="请输入其他需要说明的信息"
                    v-model="form.remark"
                    maxlength="200"
                />
            </view>
        </view>

        <view class="notice">
            <text class="notice-icon">ℹ️</text>
            <text>修改后信息将实时同步至医院 HIS 系统，请确保信息准确。姓名、身份证号等核心信息如需修改，请携带有效证件到医院窗口办理。</text>
        </view>

        <button
            class="btn-submit"
            :disabled="loading"
            @click="handleSubmit"
        >
            {{ loading ? '保存中...' : '保存修改' }}
        </button>
    </view>
</template>

<script>
import { getPatientInfo, updatePatientInfo } from '../../api/health.js'

export default {
    data() {
        return {
            form: {
                id: null,
                phone: '',
                address: '',
                allergyHistory: '',
                medicalHistory: '',
                emergencyContact: '',
                emergencyPhone: '',
                remark: ''
            },
            loading: false
        }
    },
    onLoad(options) {
        this.form.id = Number(options.patientId)
        this.loadPatientInfo(options.patientId)
    },
    methods: {
        async loadPatientInfo(patientId) {
            if (!patientId) return

            try {
                const res = await getPatientInfo(patientId)
                if (res.code === 200) {
                    const data = res.data
                    this.form = {
                        id: Number(data.id),
                        phone: data.phone || '',
                        address: data.address || '',
                        allergyHistory: data.allergyHistory || '',
                        medicalHistory: data.medicalHistory || '',
                        emergencyContact: data.emergencyContact || '',
                        emergencyPhone: data.emergencyPhone || '',
                        remark: data.remark || ''
                    }
                }
            } catch (e) {
                console.error('加载患者信息失败', e)
            }
        },

        async handleSubmit() {
            this.loading = true
            uni.showLoading({ title: '保存中...' })

            try {
                const res = await updatePatientInfo(this.form)
                if (res.code === 200) {
                    uni.hideLoading()
                    uni.showToast({
                        title: '保存成功',
                        icon: 'success'
                    })

                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1500)
                }
            } catch (e) {
                uni.hideLoading()
                uni.showToast({
                    title: e.message || '保存失败',
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
.edit-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 24rpx;
    padding-bottom: 160rpx;
    box-sizing: border-box;
}

.form {
    background: #fff;
    border-radius: 16rpx;
    padding: 0 32rpx;
    margin-bottom: 24rpx;
}

.form-item {
    padding: 28rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
}

.form-item:last-child {
    border-bottom: none;
    align-items: flex-start;
}

.label {
    width: 160rpx;
    font-size: 28rpx;
    color: #333;
    flex-shrink: 0;
}

.input {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    height: 56rpx;
    line-height: 56rpx;
}

.textarea {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    min-height: 120rpx;
    line-height: 1.6;
}

.notice {
    display: flex;
    align-items: flex-start;
    background: #fffbe6;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 48rpx;
    font-size: 24rpx;
    color: #874d00;
    line-height: 1.6;
}

.notice-icon {
    margin-right: 12rpx;
    flex-shrink: 0;
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
