<template>
    <view class="confirm-container">
        <view class="header">
            <view class="header-title">确认挂号</view>
            <view class="header-subtitle">请确认挂号信息</view>
        </view>

        <view class="content">
            <view class="section">
                <view class="section-title">就诊信息</view>
                <view class="info-row">
                    <text class="info-label">就诊科室</text>
                    <text class="info-value">{{ displayDepartmentName }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊医生</text>
                    <text class="info-value">{{ registerData.doctorName }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊日期</text>
                    <text class="info-value">{{ displayDateText }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">就诊时段</text>
                    <text class="info-value">{{ registerData.period === 'morning' ? '8:00-12:00' : '14:00-17:30' }}</text>
                </view>
                <view class="info-row">
                    <text class="info-label">挂号费用</text>
                    <text class="info-value price">¥{{ displayFee }}</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title-row">
                    <text class="section-title">选择就诊人</text>
                    <text class="add-btn" @click="addPatient">+ 添加</text>
                </view>
                <view class="patient-list">
                    <view 
                        class="patient-item" 
                        v-for="patient in patients" 
                        :key="patient.id"
                        :class="{ selected: selectedPatientId === patient.id }"
                        @click="selectPatient(patient.id)"
                    >
                        <view class="patient-radio">
                            <view class="radio-dot" v-if="selectedPatientId === patient.id"></view>
                        </view>
                        <view class="patient-info">
                            <view class="patient-name-row">
                                <text class="patient-name">{{ patient.name }}</text>
                                <text class="patient-relation" v-if="patient.relation">{{ patient.relation }}</text>
                                <text class="patient-tag default" v-if="patient.isDefault">默认</text>
                            </view>
                            <view class="patient-id-card">身份证：{{ patient.idCard }}</view>
                        </view>
                    </view>
                    <view class="empty-patient" v-if="patients.length === 0">
                        <text class="empty-icon">👤</text>
                        <text class="empty-text">暂无就诊人</text>
                        <text class="add-patient-btn" @click="addPatient">去添加</text>
                    </view>
                </view>
            </view>

            <view class="section notice">
                <view class="notice-title">
                    <text class="notice-icon">⚠️</text>
                    挂号须知
                </view>
                <view class="notice-content">
                    <text>1. 请在就诊前30分钟到达医院取号</text>
                    <text>2. 如需取消预约，请在就诊前24小时操作</text>
                    <text>3. 逾期未取号将视为爽约，影响您的挂号信用</text>
                    <text>4. 请携带有效身份证件和医保卡就诊</text>
                </view>
            </view>
        </view>

        <view class="footer">
            <view class="total-info">
                <text class="total-label">挂号费用</text>
                <text class="total-price">¥{{ displayFee }}</text>
            </view>
            <button class="btn-confirm" @click="handleConfirm" :disabled="!selectedPatientId || submitting">
                {{ submitting ? '提交中...' : '确认挂号' }}
            </button>
        </view>
    </view>
</template>

<script>
import { createRegistration } from '../../api/registration.js'
import { getFamilyList } from '../../api/family.js'

export default {
    data() {
        return {
            submitting: false,
            registerData: {},
            patients: [],
            selectedPatientId: null
        }
    },
    computed: {
        displayDepartmentName() {
            return this.registerData.departmentName || this.registerData.department || ''
        },
        displayFee() {
            const fee = this.registerData.fee || this.registerData.consultationFee || this.registerData.registrationFee || 0
            return fee
        },
        displayDateText() {
            const dateText = this.registerData.dateText || ''
            const periodText = this.registerData.period === 'morning' ? '上午' : '下午'
            return dateText ? `${dateText} ${periodText}` : `${this.registerData.date || ''} ${periodText}`
        }
    },
    onLoad(options) {
        if (options.data) {
            try {
                this.registerData = JSON.parse(decodeURIComponent(options.data))
            } catch (e) {
                console.error('解析数据失败', e)
            }
        }
        this.loadPatients()
    },
    onShow() {
        this.loadPatients()
    },
    methods: {
        async loadPatients() {
            try {
                const wechatUser = this.$store.state.wechatUser || uni.getStorageSync('wechatUser')
                if (!wechatUser || !wechatUser.id) {
                    return
                }

                const res = await getFamilyList(wechatUser.id)
                if (res.code === 200 && res.data) {
                    const familyList = res.data.map(item => ({
                        ...item,
                        id: item.patientId || item.id
                    }))

                    this.$store.commit('SET_FAMILY_LIST', familyList)
                    uni.setStorageSync('familyList', familyList)
                    uni.setStorageSync('familyMembers', familyList)

                    this.patients = familyList

                    const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
                    if (currentPatient) {
                        const matched = familyList.find(p => p.id === currentPatient.id || p.patientId === currentPatient.id)
                        this.selectedPatientId = matched ? matched.id : null
                    }

                    if (!this.selectedPatientId && this.patients.length > 0) {
                        const defaultPatient = this.patients.find(p => p.isDefault) || this.patients[0]
                        this.selectedPatientId = defaultPatient.id
                    }
                }
            } catch (e) {
                console.error('加载就诊人列表失败', e)
                const familyList = this.$store.state.familyList || uni.getStorageSync('familyList') || uni.getStorageSync('familyMembers') || []
                if (familyList.length > 0) {
                    this.patients = familyList
                    if (this.patients.length > 0) {
                        const defaultPatient = this.patients.find(p => p.isDefault) || this.patients[0]
                        this.selectedPatientId = defaultPatient.id
                    }
                }
            }
        },

        selectPatient(id) {
            this.selectedPatientId = id
        },

        addPatient() {
            uni.navigateTo({
                url: '/pages/family/add'
            })
        },

        async handleConfirm() {
            if (!this.selectedPatientId) {
                uni.showToast({
                    title: '请选择就诊人',
                    icon: 'none'
                })
                return
            }

            const patient = this.patients.find(p => p.id === this.selectedPatientId)
            if (!patient) {
                uni.showToast({
                    title: '就诊人信息错误',
                    icon: 'none'
                })
                return
            }

            this.submitting = true
            try {
                const res = await createRegistration({
                    patientId: patient.id,
                    patientName: patient.name,
                    patientGender: patient.gender,
                    patientAge: this.calculateAge(patient.birthday),
                    departmentId: this.registerData.departmentId,
                    departmentName: this.registerData.departmentName,
                    doctorId: this.registerData.doctorId,
                    doctorName: this.registerData.doctorName,
                    scheduleId: this.registerData.scheduleId,
                    registrationType: 1,
                    registrationFee: this.registerData.fee,
                    visitDate: this.registerData.date,
                    visitTimeSlot: this.registerData.period === 'morning' ? '上午' : '下午',
                    remark: ''
                })

                if (res.code === 200) {
                    uni.navigateTo({
                        url: '/pages/register/pay?data=' + encodeURIComponent(JSON.stringify({
                            registrationId: res.data.id,
                            registrationNo: res.data.registrationNo,
                            fee: this.registerData.fee,
                            ...this.registerData
                        }))
                    })
                } else {
                    uni.showToast({
                        title: res.message || res.msg || '挂号失败',
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
        },

        calculateAge(birthday) {
            if (!birthday) return 0
            const birthDate = new Date(birthday)
            const today = new Date()
            let age = today.getFullYear() - birthDate.getFullYear()
            const monthDiff = today.getMonth() - birthDate.getMonth()
            if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
                age--
            }
            return age
        }
    }
}
</script>

<style scoped>
.confirm-container {
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

.section-title-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16rpx;
}

.add-btn {
    font-size: 26rpx;
    color: #1989fa;
}

.info-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
}

.info-row:last-child {
    border-bottom: none;
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
    font-size: 30rpx;
    font-weight: bold;
}

.patient-list {
    margin-top: 8rpx;
}

.patient-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
}

.patient-item:last-child {
    border-bottom: none;
}

.patient-radio {
    width: 36rpx;
    height: 36rpx;
    border: 2rpx solid #ddd;
    border-radius: 50%;
    margin-right: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.patient-item.selected .patient-radio {
    border-color: #1989fa;
}

.radio-dot {
    width: 20rpx;
    height: 20rpx;
    background: #1989fa;
    border-radius: 50%;
}

.patient-info {
    flex: 1;
}

.patient-name-row {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.patient-name {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
    margin-right: 12rpx;
}

.patient-relation {
    font-size: 22rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 2rpx 10rpx;
    border-radius: 6rpx;
    margin-right: 8rpx;
}

.patient-tag {
    font-size: 20rpx;
    padding: 2rpx 8rpx;
    border-radius: 6rpx;
}

.patient-tag.default {
    color: #52c41a;
    background: #f6ffed;
}

.patient-id-card {
    font-size: 22rpx;
    color: #999;
}

.empty-patient {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40rpx 0;
}

.empty-icon {
    font-size: 60rpx;
    margin-bottom: 16rpx;
}

.empty-text {
    font-size: 26rpx;
    color: #999;
    margin-bottom: 16rpx;
}

.add-patient-btn {
    font-size: 26rpx;
    color: #1989fa;
    padding: 12rpx 32rpx;
    border: 2rpx solid #1989fa;
    border-radius: 32rpx;
}

.notice {
    background: #fffbe6;
}

.notice-title {
    display: flex;
    align-items: center;
    font-size: 26rpx;
    font-weight: bold;
    color: #874d00;
    margin-bottom: 12rpx;
}

.notice-icon {
    margin-right: 8rpx;
    font-size: 24rpx;
}

.notice-content {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
}

.notice-content text {
    font-size: 24rpx;
    color: #874d00;
    line-height: 1.6;
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

.total-info {
    display: flex;
    align-items: baseline;
}

.total-label {
    font-size: 26rpx;
    color: #666;
    margin-right: 8rpx;
}

.total-price {
    font-size: 36rpx;
    color: #ff6b6b;
    font-weight: bold;
}

.btn-confirm {
    width: 280rpx;
    height: 72rpx;
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    border-radius: 36rpx;
    font-size: 28rpx;
    border: none;
    line-height: 72rpx;
}

.btn-confirm[disabled] {
    opacity: 0.5;
}
</style>
