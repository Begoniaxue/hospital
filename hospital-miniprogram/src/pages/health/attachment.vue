<template>
    <view class="attachment-container">
        <view class="category-tabs">
            <view
                class="tab-item"
                :class="{ active: currentCategory === '' }"
                @click="switchCategory('')"
            >
                全部
            </view>
            <view
                class="tab-item"
                :class="{ active: currentCategory === 'CT' }"
                @click="switchCategory('CT')"
            >
                CT检查
            </view>
            <view
                class="tab-item"
                :class="{ active: currentCategory === '验血' }"
                @click="switchCategory('验血')"
            >
                验血报告
            </view>
            <view
                class="tab-item"
                :class="{ active: currentCategory === '体检' }"
                @click="switchCategory('体检')"
            >
                体检报告
            </view>
            <view
                class="tab-item"
                :class="{ active: currentCategory === '病历' }"
                @click="switchCategory('病历')"
            >
                病历资料
            </view>
        </view>

        <view class="attachment-list" v-if="attachments.length > 0">
            <view
                class="attachment-item"
                v-for="item in attachments"
                :key="item.id"
                @click="previewAttachment(item)"
            >
                <view class="item-left">
                    <image
                        v-if="item.fileType === 'image'"
                        :src="item.fileUrl"
                        mode="aspectFill"
                        class="thumb"
                    />
                    <view v-else class="file-thumb">
                        <text class="file-icon">📄</text>
                        <text class="file-ext">{{ item.fileType?.toUpperCase() }}</text>
                    </view>
                </view>
                <view class="item-center">
                    <view class="file-name">{{ item.fileName }}</view>
                    <view class="file-meta">
                        <text class="category-tag">{{ getCategoryText(item.category) }}</text>
                        <text class="date" v-if="item.examDate">{{ item.examDate }}</text>
                    </view>
                    <view class="file-info" v-if="item.examDept || item.examDoctor">
                        <text v-if="item.examDept">{{ item.examDept }}</text>
                        <text class="divider" v-if="item.examDept && item.examDoctor">·</text>
                        <text v-if="item.examDoctor">{{ item.examDoctor }}</text>
                    </view>
                    <view class="file-desc" v-if="item.description">{{ item.description }}</view>
                </view>
                <view class="item-right">
                    <view class="delete-btn" @click.stop="handleDelete(item)">
                        <text>删除</text>
                    </view>
                    <view class="arrow">›</view>
                </view>
            </view>
        </view>

        <view class="empty" v-else>
            <text class="empty-icon">📎</text>
            <text class="empty-text">暂无{{ getCategoryText(currentCategory) }}资料</text>
            <text class="empty-tip">上传检查单、病历等资料，方便医生了解您的健康状况</text>
        </view>

        <button class="btn-upload" @click="handleUpload">
            <text class="upload-icon">+</text>
            上传资料
        </button>
    </view>
</template>

<script>
import { getAttachmentList, deleteAttachment } from '../../api/health.js'
import { getCategoryText } from '../../utils/index.js'

export default {
    data() {
        return {
            patientId: null,
            currentCategory: '',
            attachments: [],
            loading: false
        }
    },
    computed: {
        getCategoryText() {
            return (category) => {
                const map = {
                    '': '健康附件',
                    'CT': 'CT检查',
                    '验血': '验血报告',
                    '体检': '体检报告',
                    '病历': '病历资料',
                    '其他': '其他资料'
                }
                return map[category] || category
            }
        }
    },
    onLoad(options) {
        this.patientId = options.patientId
        this.loadAttachments()
    },
    methods: {
        async loadAttachments() {
            if (!this.patientId) return

            this.loading = true
            try {
                const res = await getAttachmentList(this.patientId, this.currentCategory)
                if (res.code === 200) {
                    this.attachments = res.data
                }
            } catch (e) {
                console.error('加载附件列表失败', e)
            } finally {
                this.loading = false
            }
        },

        switchCategory(category) {
            this.currentCategory = category
            this.loadAttachments()
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
        },

        handleDelete(item) {
            uni.showModal({
                title: '确认删除',
                content: '确定要删除该附件吗？',
                success: async (res) => {
                    if (res.confirm) {
                        try {
                            const result = await deleteAttachment(item.id)
                            if (result.code === 200) {
                                uni.showToast({
                                    title: '删除成功',
                                    icon: 'success'
                                })
                                this.loadAttachments()
                            }
                        } catch (e) {
                            console.error('删除失败', e)
                        }
                    }
                }
            })
        },

        handleUpload() {
            uni.navigateTo({
                url: '/pages/health/attachment-upload?patientId=' + this.patientId
            })
        }
    }
}
</script>

<style scoped>
.attachment-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 160rpx;
    box-sizing: border-box;
}

.category-tabs {
    display: flex;
    background: #fff;
    padding: 0 16rpx;
    overflow-x: auto;
    white-space: nowrap;
    position: sticky;
    top: 0;
    z-index: 10;
}

.tab-item {
    flex-shrink: 0;
    padding: 28rpx 24rpx;
    font-size: 28rpx;
    color: #666;
    position: relative;
}

.tab-item.active {
    color: #1989fa;
    font-weight: bold;
}

.tab-item.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 40rpx;
    height: 6rpx;
    background: #1989fa;
    border-radius: 3rpx;
}

.attachment-list {
    padding: 24rpx;
}

.attachment-item {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
}

.item-left {
    margin-right: 24rpx;
    flex-shrink: 0;
}

.thumb {
    width: 120rpx;
    height: 120rpx;
    border-radius: 12rpx;
}

.file-thumb {
    width: 120rpx;
    height: 120rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.file-icon {
    font-size: 48rpx;
    margin-bottom: 4rpx;
}

.file-ext {
    font-size: 20rpx;
    color: #999;
    font-weight: bold;
}

.item-center {
    flex: 1;
    min-width: 0;
}

.file-name {
    font-size: 30rpx;
    color: #333;
    font-weight: 500;
    margin-bottom: 12rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.file-meta {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.category-tag {
    font-size: 22rpx;
    color: #1989fa;
    background: rgba(25, 137, 250, 0.1);
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    margin-right: 12rpx;
}

.date {
    font-size: 24rpx;
    color: #999;
}

.file-info {
    font-size: 24rpx;
    color: #666;
    margin-bottom: 8rpx;
}

.divider {
    margin: 0 8rpx;
    color: #ddd;
}

.file-desc {
    font-size: 24rpx;
    color: #999;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.item-right {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    margin-left: 16rpx;
}

.delete-btn {
    font-size: 24rpx;
    color: #ee0a24;
    margin-right: 16rpx;
    padding: 8rpx 16rpx;
    border: 1rpx solid #ee0a24;
    border-radius: 8rpx;
}

.arrow {
    font-size: 36rpx;
    color: #ccc;
}

.empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 48rpx;
}

.empty-icon {
    font-size: 120rpx;
    margin-bottom: 24rpx;
}

.empty-text {
    font-size: 32rpx;
    color: #333;
    margin-bottom: 12rpx;
}

.empty-tip {
    font-size: 26rpx;
    color: #999;
    text-align: center;
    line-height: 1.5;
}

.btn-upload {
    position: fixed;
    left: 32rpx;
    right: 32rpx;
    bottom: 60rpx;
    height: 96rpx;
    background: #1989fa;
    color: #fff;
    border-radius: 48rpx;
    font-size: 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
}

.btn-upload:active {
    opacity: 0.85;
}

.upload-icon {
    font-size: 40rpx;
    margin-right: 8rpx;
    font-weight: bold;
}
</style>
