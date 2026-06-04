<template>
    <view class="add-container">
        <view class="form">
            <view class="form-item">
                <view class="label">姓名 <text class="required">*</text></view>
                <input
                    class="input"
                    placeholder="请输入就诊人真实姓名"
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
                    @blur="onIdCardBlur"
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
                <view class="label">手机号 <text class="required">*</text></view>
                <input
                    class="input"
                    type="number"
                    placeholder="请输入联系电话"
                    maxlength="11"
                    v-model="form.phone"
                />
            </view>

            <view class="form-item">
                <view class="label">与本人关系 <text class="required">*</text></view>
                <picker
                    :range="relationList"
                    range-key="label"
                    @change="onRelationChange"
                >
                    <view class="picker-text">
                        {{ currentRelationLabel || '请选择关系' }}
                    </view>
                </picker>
            </view>
        </view>

        <view class="notice">
            <text>🔒 身份信息将用于医院 HIS 系统建档，请确保信息真实有效</text>
        </view>

        <button
            class="btn-submit"
            :disabled="!canSubmit || loading"
            @click="handleSubmit"
        >
            {{ loading ? '提交中...' : '确认添加' }}
        </button>
    </view>
</template>

<script>
import { addFamilyMember, getFamilyList } from '../../api/family.js'

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
                relation: ''
            },
            relationList: [
                { value: '父亲', label: '父亲' },
                { value: '母亲', label: '母亲' },
                { value: '配偶', label: '配偶' },
                { value: '儿子', label: '儿子' },
                { value: '女儿', label: '女儿' },
                { value: '子女', label: '子女' },
                { value: '其他', label: '其他' }
            ],
            currentRelationIndex: -1,
            loading: false
        }
    },
    computed: {
        canSubmit() {
            const nameValid = this.form.name.trim() !== ''
            const idCardValid = this.validateIdCardLocal(this.form.idCard)
            const phoneValid = this.validatePhoneLocal(this.form.phone)
            const relationValid = this.form.relation !== ''
            return nameValid && idCardValid && phoneValid && relationValid
        },
        currentRelationLabel() {
            const item = this.relationList.find(r => r.value === this.form.relation)
            return item ? item.label : ''
        }
    },
    onLoad() {
        const wechatUser = this.$store.state.wechatUser || uni.getStorageSync('wechatUser')
        if (wechatUser) {
            this.form.wechatUserId = wechatUser.id
        }
    },
    methods: {
        validateIdCardLocal(idCard) {
            const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
            return reg.test(idCard)
        },
        validatePhoneLocal(phone) {
            const reg = /^1[3-9]\d{9}$/
            return reg.test(phone)
        },
        onIdCardBlur() {
            if (this.form.idCard && this.form.idCard.length === 18) {
                const year = this.form.idCard.substring(6, 10)
                const month = this.form.idCard.substring(10, 12)
                const day = this.form.idCard.substring(12, 14)
                this.form.birthday = `${year}-${month}-${day}`

                const genderCode = parseInt(this.form.idCard.substring(16, 17))
                this.form.gender = genderCode % 2 === 1 ? 1 : 0
            }
        },

        onDateChange(e) {
            this.form.birthday = e.detail.value
        },

        onRelationChange(e) {
            this.currentRelationIndex = e.detail.value
            this.form.relation = this.relationList[e.detail.value].value
        },

        async handleSubmit() {
            if (!this.canSubmit || this.loading) return

            this.loading = true
            uni.showLoading({ title: '提交中...' })

            try {
                const res = await addFamilyMember(this.form)

                if (res.code === 200) {
                    uni.hideLoading()
                    uni.showToast({
                        title: res.data.message || '添加成功',
                        icon: 'success'
                    })

                    if (res.data && res.data.family) {
                        const newFamily = {
                            ...res.data.family,
                            id: res.data.family.patientId || res.data.family.id
                        }

                        if (res.data.patient) {
                            this.$store.commit('SET_CURRENT_PATIENT', res.data.patient)
                            uni.setStorageSync('currentPatient', res.data.patient)
                            this.$store.commit('UPDATE_CURRENT_PATIENT_ID', res.data.patient.id)
                        }

                        try {
                            const familyRes = await getFamilyList(this.form.wechatUserId)
                            if (familyRes.code === 200 && familyRes.data) {
                                const familyList = familyRes.data.map(item => ({
                                    ...item,
                                    id: item.patientId || item.id
                                }))
                                this.$store.commit('SET_FAMILY_LIST', familyList)
                                uni.setStorageSync('familyList', familyList)
                                uni.setStorageSync('familyMembers', familyList)
                            }
                        } catch (e) {
                            console.error('刷新就诊人列表失败', e)
                        }
                    }

                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1500)
                }
            } catch (e) {
                uni.hideLoading()
                uni.showToast({
                    title: e.message || '添加失败',
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
.add-container {
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
    content: '请选择';
    color: #999;
}

.notice {
    background: #e6f7ff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 48rpx;
    font-size: 24rpx;
    color: #1890ff;
    line-height: 1.5;
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
