<template>
    <view class="record-container">
        <view class="patient-card" v-if="patient">
            <view class="patient-info">
                <view class="avatar" :class="{ 'avatar-female': patient.gender === 0 }">
                    {{ patient.name.substring(0, 1) }}
                </view>
                <view class="info">
                    <view class="name">{{ patient.name }}</view>
                    <view class="meta">
                        <text>{{ getGenderText(patient.gender) }}</text>
                        <text class="divider">|</text>
                        <text>{{ patient.age }}岁</text>
                        <text class="divider" v-if="patient.patientNo">|</text>
                        <text v-if="patient.patientNo">{{ patient.patientNo }}</text>
                    </view>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="section-header">
                <text class="section-title">健康信息</text>
                <text class="edit-btn" @click="handleEdit">编辑</text>
            </view>

            <view class="card">
                <view class="info-item">
                    <text class="label">过敏史</text>
                    <text class="value" :class="{ 'text-danger': record?.allergyHistory }">{{ record?.allergyHistory || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">既往病史</text>
                    <text class="value">{{ record?.medicalHistory || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">慢病史</text>
                    <text class="value">{{ record?.chronicDisease || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">手术史</text>
                    <text class="value">{{ record?.operationHistory || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">药物禁忌</text>
                    <text class="value" :class="{ 'text-danger': record?.drugContraindication }">{{ record?.drugContraindication || '未填写' }}</text>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="section-header">
                <text class="section-title">体格信息</text>
            </view>

            <view class="grid-card">
                <view class="grid-item">
                    <text class="grid-label">血型</text>
                    <text class="grid-value">{{ record?.bloodType || '-' }}</text>
                </view>
                <view class="grid-item">
                    <text class="grid-label">身高</text>
                    <text class="grid-value">{{ record?.height ? record.height + 'cm' : '-' }}</text>
                </view>
                <view class="grid-item">
                    <text class="grid-label">体重</text>
                    <text class="grid-value">{{ record?.weight ? record.weight + 'kg' : '-' }}</text>
                </view>
                <view class="grid-item">
                    <text class="grid-label">BMI</text>
                    <text class="grid-value">{{ bmi }}</text>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="section-header">
                <text class="section-title">生活习惯</text>
            </view>

            <view class="card">
                <view class="info-item">
                    <text class="label">婚姻状况</text>
                    <text class="value">{{ record?.maritalStatus || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">职业</text>
                    <text class="value">{{ record?.occupation || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">吸烟情况</text>
                    <text class="value">{{ record?.smokingStatus || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">饮酒情况</text>
                    <text class="value">{{ record?.drinkingStatus || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">运动习惯</text>
                    <text class="value">{{ record?.exerciseHabit || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">饮食习惯</text>
                    <text class="value">{{ record?.dietaryHabit || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">睡眠习惯</text>
                    <text class="value">{{ record?.sleepHabit || '未填写' }}</text>
                </view>
                <view class="info-item">
                    <text class="label">排便习惯</text>
                    <text class="value">{{ record?.defecationHabit || '未填写' }}</text>
                </view>
            </view>
        </view>

        <view class="section">
            <view class="section-header">
                <text class="section-title">健康附件</text>
                <text class="edit-btn" @click="goToAttachment">查看全部</text>
            </view>

            <view class="attachment-preview" v-if="attachments.length > 0">
                <view
                    class="attachment-item"
                    v-for="item in attachments.slice(0, 3)"
                    :key="item.id"
                    @click="previewAttachment(item)"
                >
                    <image
                        v-if="item.fileType === 'image'"
                        :src="item.fileUrl"
                        mode="aspectFill"
                        class="attachment-thumb"
                    />
                    <view v-else class="attachment-file">
                        <text class="file-icon">📄</text>
                        <text class="file-name">{{ item.fileName }}</text>
                    </view>
                    <view class="attachment-meta">
                        <text class="category-tag">{{ getCategoryText(item.category) }}</text>
                        <text class="date">{{ item.examDate }}</text>
                    </view>
                </view>
            </view>

            <view class="empty-attachment" v-else>
                <text class="empty-icon">📎</text>
                <text class="empty-text">暂无健康附件</text>
                <text class="empty-tip">上传检查单、病历等资料，方便医生了解您的健康状况</text>
            </view>
        </view>

        <view class="notice">
            <text class="notice-icon">💡</text>
            <text>以上信息将在您就诊时提供给医生参考，有助于医生更准确地判断病情。过敏史等重要信息会在医生开处方时弹窗提醒。</text>
        </view>
    </view>
</template>

<script>
import { getHealthRecord, getAttachmentList, getPatientInfo } from '../../api/health.js'
import { getGenderText, getCategoryText } from '../../utils/index.js'

export default {
    data() {
        return {
            patient: null,
            record: null,
            attachments: [],
            loading: false
        }
    },
    computed: {
        patientId() {
            return this.$store.state.wechatUser?.currentPatientId
                || uni.getStorageSync('wechatUser')?.currentPatientId
        },
        bmi() {
            if (this.record?.height && this.record?.weight) {
                const h = parseFloat(this.record.height) / 100
                const w = parseFloat(this.record.weight)
                const bmi = (w / (h * h)).toFixed(1)
                return bmi
            }
            return '-'
        }
    },
    onShow() {
        this.loadData()
    },
    methods: {
        getGenderText,
        getCategoryText,

        async loadData() {
            if (!this.patientId) {
                uni.showToast({
                    title: '请先完成实名认证',
                    icon: 'none'
                })
                setTimeout(() => {
                    uni.navigateTo({ url: '/pages/login/phone' })
                }, 1500)
                return
            }

            this.loading = true
            try {
                const [patientRes, recordRes, attachmentRes] = await Promise.all([
                    getPatientInfo(this.patientId),
                    getHealthRecord(this.patientId),
                    getAttachmentList(this.patientId)
                ])

                if (patientRes.code === 200) {
                    this.patient = patientRes.data
                }
                if (recordRes.code === 200) {
                    this.record = recordRes.data
                }
                if (attachmentRes.code === 200) {
                    this.attachments = attachmentRes.data
                }
            } catch (e) {
                console.error('加载健康档案失败', e)
            } finally {
                this.loading = false
            }
        },

        handleEdit() {
            uni.navigateTo({
                url: '/pages/health/record-edit?patientId=' + this.patientId
            })
        },

        goToAttachment() {
            uni.navigateTo({
                url: '/pages/health/attachment?patientId=' + this.patientId
            })
        },

        previewAttachment(item) {
            if (item.fileType === 'image') {
                uni.previewImage({
                    urls: [item.fileUrl]
                })
            } else {
                uni.showToast({
                    title: 'PDF预览功能开发中',
                    icon: 'none'
                })
            }
        }
    }
}
</script>

<style scoped>
.record-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 24rpx;
    padding-bottom: 48rpx;
    box-sizing: border-box;
}

.patient-card {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
    color: #fff;
}

.patient-info {
    display: flex;
    align-items: center;
}

.avatar {
    width: 96rpx;
    height: 96rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
    font-size: 40rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 24rpx;
}

.avatar-female {
    background: rgba(255, 107, 157, 0.3);
}

.info {
    flex: 1;
}

.name {
    font-size: 36rpx;
    font-weight: bold;
    margin-bottom: 8rpx;
}

.meta {
    font-size: 24rpx;
    opacity: 0.9;
}

.divider {
    margin: 0 12rpx;
    opacity: 0.5;
}

.section {
    margin-bottom: 24rpx;
}

.section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16rpx;
}

.section-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
}

.edit-btn {
    font-size: 26rpx;
    color: #1989fa;
}

.card {
    background: #fff;
    border-radius: 16rpx;
    padding: 0 32rpx;
}

.info-item {
    display: flex;
    padding: 28rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
}

.info-item:last-child {
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
}

.text-danger {
    color: #ee0a24;
}

.grid-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    display: flex;
    flex-wrap: wrap;
}

.grid-item {
    width: 50%;
    padding: 24rpx 0;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.grid-label {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 8rpx;
}

.grid-value {
    font-size: 36rpx;
    font-weight: bold;
    color: #1989fa;
}

.attachment-preview {
    display: flex;
    gap: 16rpx;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
}

.attachment-item {
    flex: 1;
    max-width: 210rpx;
}

.attachment-thumb {
    width: 100%;
    height: 210rpx;
    border-radius: 12rpx;
    margin-bottom: 12rpx;
}

.attachment-file {
    width: 100%;
    height: 210rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-bottom: 12rpx;
}

.file-icon {
    font-size: 60rpx;
    margin-bottom: 8rpx;
}

.file-name {
    font-size: 20rpx;
    color: #666;
    text-align: center;
    padding: 0 8rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 100%;
}

.attachment-meta {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.category-tag {
    font-size: 20rpx;
    color: #1989fa;
    background: rgba(25, 137, 250, 0.1);
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    margin-bottom: 4rpx;
}

.date {
    font-size: 20rpx;
    color: #999;
}

.empty-attachment {
    background: #fff;
    border-radius: 16rpx;
    padding: 60rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.empty-icon {
    font-size: 80rpx;
    margin-bottom: 16rpx;
}

.empty-text {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 8rpx;
}

.empty-tip {
    font-size: 24rpx;
    color: #999;
    text-align: center;
    line-height: 1.5;
}

.notice {
    display: flex;
    align-items: flex-start;
    background: #fffbe6;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-top: 24rpx;
    font-size: 24rpx;
    color: #874d00;
    line-height: 1.6;
}

.notice-icon {
    margin-right: 12rpx;
    flex-shrink: 0;
}
</style>
