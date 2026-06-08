<template>
    <view class="submit-complaint-container">
        <view class="header">
            <view class="header-title">投诉建议</view>
            <view class="header-subtitle">您的反馈是我们进步的动力</view>
        </view>

        <view class="content">
            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📋</text>
                    反馈类型
                </view>
                <view class="radio-group">
                    <view 
                        class="radio-item" 
                        v-for="item in typeOptions" 
                        :key="item.value"
                        :class="{ active: form.type === item.value }"
                        @click="form.type = item.value"
                    >
                        <view class="radio-circle">
                            <view class="radio-inner" v-if="form.type === item.value"></view>
                        </view>
                        <text class="radio-label">{{ item.label }}</text>
                    </view>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">🎯</text>
                    投诉对象
                </view>
                <view class="radio-group">
                    <view 
                        class="radio-item" 
                        v-for="item in targetOptions" 
                        :key="item.value"
                        :class="{ active: form.targetType === item.value }"
                        @click="selectTargetType(item.value)"
                    >
                        <view class="radio-circle">
                            <view class="radio-inner" v-if="form.targetType === item.value"></view>
                        </view>
                        <text class="radio-label">{{ item.label }}</text>
                    </view>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">🏥</text>
                    选择科室
                </view>
                <picker 
                    mode="selector" 
                    :range="departmentList" 
                    range-key="name"
                    :value="departmentIndex"
                    @change="onDepartmentChange"
                >
                    <view class="picker-item">
                        <text class="picker-text" :class="{ placeholder: !form.departmentId }">
                            {{ departmentIndex >= 0 ? departmentList[departmentIndex].name : '请选择科室' }}
                        </text>
                        <text class="picker-arrow">›</text>
                    </view>
                </picker>
            </view>

            <view class="section" v-if="form.targetType === 'doctor'">
                <view class="section-title">
                    <text class="title-icon">👨‍⚕️</text>
                    选择医生
                    <text class="title-optional">（可选）</text>
                </view>
                <picker 
                    mode="selector" 
                    :range="doctorList" 
                    range-key="name"
                    :value="doctorIndex"
                    @change="onDoctorChange"
                >
                    <view class="picker-item">
                        <text class="picker-text" :class="{ placeholder: !form.doctorId }">
                            {{ doctorIndex >= 0 ? doctorList[doctorIndex].name : '请选择医生' }}
                        </text>
                        <text class="picker-arrow">›</text>
                    </view>
                </picker>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📝</text>
                    标题
                </view>
                <input 
                    class="text-input" 
                    v-model="form.title"
                    placeholder="请输入标题"
                    :maxlength="50"
                />
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">💬</text>
                    问题描述
                </view>
                <textarea 
                    class="content-input" 
                    v-model="form.content"
                    placeholder="请详细描述您的问题或建议（最多500字）"
                    :maxlength="500"
                />
                <view class="char-count">{{ form.content.length }}/500</view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📷</text>
                    上传图片
                    <text class="title-optional">（最多6张）</text>
                </view>
                <view class="image-list">
                    <view class="image-item" v-for="(img, idx) in images" :key="idx">
                        <image class="image-preview" :src="img" mode="aspectFill" />
                        <view class="image-delete" @click="deleteImage(idx)">
                            <text class="delete-icon">×</text>
                        </view>
                    </view>
                    <view class="image-upload" v-if="images.length < 6" @click="chooseImage">
                        <text class="upload-icon">+</text>
                        <text class="upload-text">上传图片</text>
                    </view>
                </view>
            </view>
        </view>

        <view class="footer">
            <button class="submit-btn" :disabled="submitting" @click="submitComplaint">
                {{ submitting ? '提交中...' : '提交' }}
            </button>
        </view>
    </view>
</template>

<script>
import { submitComplaint } from '../../api/profile.js'

export default {
    data() {
        return {
            submitting: false,
            form: {
                type: 'complaint',
                targetType: 'department',
                departmentId: '',
                doctorId: '',
                title: '',
                content: ''
            },
            typeOptions: [
                { label: '投诉', value: 'complaint' },
                { label: '建议', value: 'suggestion' }
            ],
            targetOptions: [
                { label: '科室', value: 'department' },
                { label: '医生', value: 'doctor' }
            ],
            departmentList: [
                { id: '1', name: '内科' },
                { id: '2', name: '外科' },
                { id: '3', name: '儿科' },
                { id: '4', name: '妇产科' },
                { id: '5', name: '眼科' },
                { id: '6', name: '耳鼻喉科' },
                { id: '7', name: '口腔科' },
                { id: '8', name: '皮肤科' },
                { id: '9', name: '骨科' },
                { id: '10', name: '中医科' }
            ],
            departmentIndex: -1,
            doctorList: [
                { id: '101', name: '张医生' },
                { id: '102', name: '李医生' },
                { id: '103', name: '王医生' },
                { id: '104', name: '赵医生' },
                { id: '105', name: '刘医生' }
            ],
            doctorIndex: -1,
            images: [],
            patientId: ''
        }
    },
    onLoad() {
        const currentPatient = this.$store.state.currentPatient || uni.getStorageSync('currentPatient')
        if (currentPatient) {
            this.patientId = currentPatient.id
        } else {
            uni.showToast({
                title: '请先完成实名认证',
                icon: 'none'
            })
        }
    },
    methods: {
        selectTargetType(type) {
            this.form.targetType = type
            if (type === 'department') {
                this.form.doctorId = ''
                this.doctorIndex = -1
            }
        },

        onDepartmentChange(e) {
            this.departmentIndex = e.detail.value
            this.form.departmentId = this.departmentList[this.departmentIndex].id
        },

        onDoctorChange(e) {
            this.doctorIndex = e.detail.value
            this.form.doctorId = this.doctorList[this.doctorIndex].id
        },

        chooseImage() {
            const remaining = 6 - this.images.length
            uni.chooseImage({
                count: remaining,
                success: (res) => {
                    this.images = this.images.concat(res.tempFilePaths)
                },
                fail: () => {
                    uni.showToast({
                        title: '选择图片失败',
                        icon: 'none'
                    })
                }
            })
        },

        deleteImage(idx) {
            this.images.splice(idx, 1)
        },

        async submitComplaint() {
            if (!this.form.title.trim()) {
                uni.showToast({
                    title: '请输入标题',
                    icon: 'none'
                })
                return
            }
            if (!this.form.content.trim()) {
                uni.showToast({
                    title: '请输入问题描述',
                    icon: 'none'
                })
                return
            }
            if (!this.form.departmentId) {
                uni.showToast({
                    title: '请选择科室',
                    icon: 'none'
                })
                return
            }
            if (!this.patientId) {
                uni.showToast({
                    title: '请先完成实名认证',
                    icon: 'none'
                })
                return
            }

            this.submitting = true
            try {
                const res = await submitComplaint({
                    patientId: this.patientId,
                    type: this.form.type,
                    targetType: this.form.targetType,
                    departmentId: this.form.departmentId,
                    doctorId: this.form.doctorId,
                    title: this.form.title,
                    content: this.form.content,
                    images: this.images
                })
                if (res.code === 200) {
                    uni.showToast({
                        title: '提交成功',
                        icon: 'success'
                    })
                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1500)
                } else {
                    uni.showToast({
                        title: res.msg || '提交失败',
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
.submit-complaint-container {
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
    display: flex;
    align-items: center;
}

.title-icon {
    margin-right: 8rpx;
    font-size: 28rpx;
}

.title-optional {
    font-size: 24rpx;
    color: #999;
    font-weight: normal;
    margin-left: 4rpx;
}

.radio-group {
    display: flex;
    gap: 32rpx;
}

.radio-item {
    display: flex;
    align-items: center;
    padding: 12rpx 0;
}

.radio-circle {
    width: 36rpx;
    height: 36rpx;
    border: 2rpx solid #ddd;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 12rpx;
    transition: all 0.2s;
}

.radio-item.active .radio-circle {
    border-color: #1989fa;
}

.radio-inner {
    width: 20rpx;
    height: 20rpx;
    background: #1989fa;
    border-radius: 50%;
}

.radio-label {
    font-size: 28rpx;
    color: #333;
}

.radio-item.active .radio-label {
    color: #1989fa;
    font-weight: bold;
}

.picker-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
}

.picker-item:last-child {
    border-bottom: none;
}

.picker-text {
    font-size: 28rpx;
    color: #333;
}

.picker-text.placeholder {
    color: #999;
}

.picker-arrow {
    font-size: 32rpx;
    color: #ccc;
}

.text-input {
    width: 100%;
    height: 80rpx;
    background: #f8f8f8;
    border-radius: 12rpx;
    padding: 0 20rpx;
    font-size: 28rpx;
    color: #333;
    box-sizing: border-box;
}

.content-input {
    width: 100%;
    min-height: 240rpx;
    background: #f8f8f8;
    border-radius: 12rpx;
    padding: 20rpx;
    font-size: 26rpx;
    color: #333;
    line-height: 1.6;
    box-sizing: border-box;
}

.char-count {
    text-align: right;
    font-size: 22rpx;
    color: #999;
    margin-top: 8rpx;
}

.image-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
}

.image-item {
    position: relative;
    width: 160rpx;
    height: 160rpx;
}

.image-preview {
    width: 100%;
    height: 100%;
    border-radius: 12rpx;
    background: #f5f5f5;
}

.image-delete {
    position: absolute;
    top: -12rpx;
    right: -12rpx;
    width: 40rpx;
    height: 40rpx;
    background: rgba(0, 0, 0, 0.6);
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

.delete-icon {
    font-size: 28rpx;
    color: #fff;
    line-height: 1;
}

.image-upload {
    width: 160rpx;
    height: 160rpx;
    background: #f8f8f8;
    border-radius: 12rpx;
    border: 2rpx dashed #ddd;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.upload-icon {
    font-size: 48rpx;
    color: #ccc;
    line-height: 1;
    margin-bottom: 8rpx;
}

.upload-text {
    font-size: 22rpx;
    color: #999;
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
