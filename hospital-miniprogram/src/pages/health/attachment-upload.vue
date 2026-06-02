<template>
    <view class="upload-container">
        <view class="upload-area" @click="handleChooseFile">
            <view class="upload-icon">📎</view>
            <view class="upload-text">点击选择文件</view>
            <view class="upload-tip">支持 JPG、PNG、PDF 格式，单文件不超过 10MB</view>
        </view>

        <view class="preview-area" v-if="fileUrl">
            <image
                v-if="isImage"
                :src="fileUrl"
                mode="aspectFit"
                class="preview-image"
            />
            <view v-else class="preview-file">
                <text class="file-icon">📄</text>
                <text class="file-name">{{ form.fileName }}</text>
                <text class="file-size">{{ form.fileSize }}</text>
            </view>
            <view class="remove-btn" @click="handleRemove">
                <text>×</text>
            </view>
        </view>

        <view class="form">
            <view class="form-item">
                <view class="label">资料分类 <text class="required">*</text></view>
                <picker :range="categories" range-key="label" @change="onCategoryChange">
                    <view class="picker-text">
                        {{ currentCategoryLabel || '请选择分类' }}
                    </view>
                </picker>
            </view>

            <view class="form-item">
                <view class="label">文件名称</view>
                <input
                    class="input"
                    placeholder="请输入文件名称（选填）"
                    v-model="form.fileName"
                />
            </view>

            <view class="form-item">
                <view class="label">检查日期</view>
                <picker mode="date" :value="form.examDate" @change="onDateChange">
                    <view class="picker-text">
                        {{ form.examDate || '请选择检查日期' }}
                    </view>
                </picker>
            </view>

            <view class="form-item">
                <view class="label">检查科室</view>
                <input
                    class="input"
                    placeholder="请输入检查科室（选填）"
                    v-model="form.examDept"
                />
            </view>

            <view class="form-item">
                <view class="label">检查医生</view>
                <input
                    class="input"
                    placeholder="请输入检查医生（选填）"
                    v-model="form.examDoctor"
                />
            </view>

            <view class="form-item">
                <view class="label">描述</view>
                <textarea
                    class="textarea"
                    placeholder="请输入资料描述（选填）"
                    v-model="form.description"
                    maxlength="200"
                />
            </view>
        </view>

        <button
            class="btn-submit"
            :disabled="!canSubmit || loading"
            @click="handleSubmit"
        >
            {{ loading ? '上传中...' : '上传' }}
        </button>
    </view>
</template>

<script>
import { saveAttachment } from '../../api/health.js'

export default {
    data() {
        return {
            patientId: null,
            fileUrl: '',
            fileType: '',
            isImage: false,
            categories: [
                { value: 'CT', label: 'CT检查' },
                { value: '验血', label: '验血报告' },
                { value: '体检', label: '体检报告' },
                { value: '病历', label: '病历资料' },
                { value: '其他', label: '其他资料' }
            ],
            currentCategoryIndex: -1,
            form: {
                patientId: null,
                category: '',
                fileType: '',
                fileName: '',
                fileUrl: '',
                fileSize: '',
                description: '',
                examDate: '',
                examDept: '',
                examDoctor: ''
            },
            loading: false
        }
    },
    computed: {
        canSubmit() {
            return this.fileUrl && this.form.category !== ''
        },
        currentCategoryLabel() {
            const item = this.categories.find(c => c.value === this.form.category)
            return item ? item.label : ''
        }
    },
    onLoad(options) {
        this.patientId = options.patientId
        this.form.patientId = options.patientId
    },
    methods: {
        handleChooseFile() {
            uni.chooseImage({
                count: 1,
                sizeType: ['compressed', 'original'],
                sourceType: ['album', 'camera'],
                success: (res) => {
                    const tempFile = res.tempFiles[0]
                    this.fileUrl = tempFile.path
                    this.fileType = 'image'
                    this.isImage = true
                    this.form.fileType = 'image'
                    this.form.fileUrl = tempFile.path
                    this.form.fileSize = this.formatFileSize(tempFile.size)
                    if (!this.form.fileName) {
                        this.form.fileName = 'IMG_' + Date.now() + '.jpg'
                    }

                    uni.showToast({
                        title: '已选择图片',
                        icon: 'success'
                    })
                }
            })
        },

        formatFileSize(bytes) {
            if (bytes < 1024) return bytes + 'B'
            if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + 'KB'
            return (bytes / (1024 * 1024)).toFixed(1) + 'MB'
        },

        handleRemove() {
            this.fileUrl = ''
            this.fileType = ''
            this.isImage = false
            this.form.fileType = ''
            this.form.fileUrl = ''
            this.form.fileSize = ''
        },

        onCategoryChange(e) {
            this.currentCategoryIndex = e.detail.value
            this.form.category = this.categories[e.detail.value].value
        },

        onDateChange(e) {
            this.form.examDate = e.detail.value
        },

        async handleSubmit() {
            if (!this.canSubmit || this.loading) return

            this.loading = true
            uni.showLoading({ title: '上传中...' })

            try {
                const res = await saveAttachment(this.form)
                if (res.code === 200) {
                    uni.hideLoading()
                    uni.showToast({
                        title: '上传成功',
                        icon: 'success'
                    })

                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1500)
                }
            } catch (e) {
                uni.hideLoading()
                uni.showToast({
                    title: e.message || '上传失败',
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
.upload-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 24rpx;
    padding-bottom: 160rpx;
    box-sizing: border-box;
}

.upload-area {
    background: #fff;
    border: 2rpx dashed #1989fa;
    border-radius: 16rpx;
    padding: 80rpx 48rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 24rpx;
}

.upload-icon {
    font-size: 80rpx;
    margin-bottom: 16rpx;
}

.upload-text {
    font-size: 30rpx;
    color: #1989fa;
    font-weight: bold;
    margin-bottom: 12rpx;
}

.upload-tip {
    font-size: 24rpx;
    color: #999;
}

.preview-area {
    position: relative;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

.preview-image {
    max-width: 100%;
    max-height: 500rpx;
    border-radius: 12rpx;
}

.preview-file {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 48rpx;
}

.file-icon {
    font-size: 80rpx;
    margin-bottom: 16rpx;
}

.file-name {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 8rpx;
    max-width: 400rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.file-size {
    font-size: 24rpx;
    color: #999;
}

.remove-btn {
    position: absolute;
    top: 12rpx;
    right: 12rpx;
    width: 48rpx;
    height: 48rpx;
    background: rgba(0, 0, 0, 0.6);
    color: #fff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32rpx;
    line-height: 1;
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

.required {
    color: #ee0a24;
    margin-right: 4rpx;
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

.picker-text {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    height: 56rpx;
    line-height: 56rpx;
}

.picker-text:empty::before {
    content: '请选择';
    color: #999;
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
