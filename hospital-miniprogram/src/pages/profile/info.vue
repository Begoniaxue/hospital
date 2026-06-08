<template>
    <view class="info-container" v-if="patient">
        <view class="header-card">
            <view class="avatar" :class="{ 'avatar-female': patient.gender === 0 }">
                {{ patient.name.substring(0, 1) }}
            </view>
            <view class="info">
                <view class="name">{{ patient.name }}</view>
                <view class="meta">
                    <text>{{ getGenderText(patient.gender) }}</text>
                    <text class="divider">|</text>
                    <text>{{ patient.age }}岁</text>
                </view>
                <view class="patient-no" v-if="patient.patientNo">
                    就诊号: {{ patient.patientNo }}
                </view>
            </view>
            <view class="edit-icon" @click="handleEdit">
                <text>✏️</text>
            </view>
        </view>

        <view class="form">
            <view class="form-item">
                <view class="label">姓名</view>
                <view class="value">{{ patient.name }}</view>
            </view>

            <view class="form-item">
                <view class="label">性别</view>
                <view class="value">{{ getGenderText(patient.gender) }}</view>
            </view>

            <view class="form-item">
                <view class="label">年龄</view>
                <view class="value">{{ patient.age }}岁</view>
            </view>

            <view class="form-item">
                <view class="label">身份证号</view>
                <view class="value">{{ maskIdCard(patient.idCard) }}</view>
            </view>

            <view class="form-item">
                <view class="label">手机号</view>
                <view class="value">{{ maskPhone(patient.phone) }}</view>
            </view>

            <view class="form-item">
                <view class="label">现住址</view>
                <view class="value">{{ patient.address || '未填写' }}</view>
            </view>

            <view class="form-item">
                <view class="label">过敏史</view>
                <view class="value text-danger">{{ patient.allergyHistory || '未填写' }}</view>
            </view>

            <view class="form-item">
                <view class="label">既往病史</view>
                <view class="value">{{ patient.medicalHistory || '未填写' }}</view>
            </view>

            <view class="form-item">
                <view class="label">紧急联系人</view>
                <view class="value">{{ patient.emergencyContact || '未填写' }}</view>
            </view>

            <view class="form-item">
                <view class="label">紧急联系电话</view>
                <view class="value">{{ maskPhone(patient.emergencyPhone) || '未填写' }}</view>
            </view>
        </view>

        <view class="notice">
            <text class="notice-icon">ℹ️</text>
            <text>如需修改姓名、身份证号等核心信息，请携带有效证件到医院窗口办理。其他信息可自行修改，修改后将实时同步至 HIS 系统。</text>
        </view>

        <button class="btn-edit" @click="handleEdit">
            编辑信息
        </button>
    </view>
</template>

<script>
import { getPatientInfo } from '../../api/health.js'
import { getGenderText, maskIdCard, maskPhone } from '../../utils/index.js'

export default {
    data() {
        return {
            patient: null
        }
    },
    computed: {
        patientId() {
            return this.$store.state.wechatUser?.currentPatientId
                || uni.getStorageSync('wechatUser')?.currentPatientId
        }
    },
    onShow() {
        this.loadPatientInfo()
    },
    methods: {
        getGenderText,
        maskIdCard,
        maskPhone,

        async loadPatientInfo() {
            if (!this.patientId) return

            try {
                const res = await getPatientInfo(this.patientId)
                if (res.code === 200) {
                    this.patient = res.data
                }
            } catch (e) {
                console.error('加载患者信息失败', e)
            }
        },

        handleEdit() {
            uni.navigateTo({
                url: '/pages/profile/info-edit?patientId=' + this.patientId
            })
        }
    }
}
</script>

<style scoped>
.info-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 24rpx;
    padding-bottom: 160rpx;
    box-sizing: border-box;
}

.header-card {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    border-radius: 16rpx;
    padding: 40rpx;
    margin-bottom: 24rpx;
    display: flex;
    align-items: center;
    color: #fff;
    position: relative;
}

.avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
    font-size: 48rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 24rpx;
    flex-shrink: 0;
}

.avatar-female {
    background: rgba(255, 107, 157, 0.3);
}

.info {
    flex: 1;
}

.name {
    font-size: 40rpx;
    font-weight: bold;
    margin-bottom: 12rpx;
}

.meta {
    font-size: 26rpx;
    opacity: 0.9;
    margin-bottom: 8rpx;
}

.divider {
    margin: 0 12rpx;
    opacity: 0.5;
}

.patient-no {
    font-size: 24rpx;
    opacity: 0.85;
}

.edit-icon {
    font-size: 36rpx;
    padding: 12rpx;
}

.form {
    background: #fff;
    border-radius: 16rpx;
    padding: 0 32rpx;
    margin-bottom: 24rpx;
}

.form-item {
    display: flex;
    padding: 28rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
}

.form-item:last-child {
    border-bottom: none;
}

.label {
    width: 160rpx;
    font-size: 28rpx;
    color: #999;
    flex-shrink: 0;
}

.value {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    text-align: right;
}

.text-danger {
    color: #ee0a24;
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

.btn-edit {
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

.btn-edit:active {
    opacity: 0.85;
}
</style>
