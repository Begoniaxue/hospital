<template>
    <view class="guide-container">
        <view class="header">
            <view class="header-title">智能导诊</view>
            <view class="header-subtitle">AI智能辅助，快速定位科室</view>
        </view>

        <view class="section">
            <view class="section-title">
                <text class="title-icon">🤒</text>
                选择您的症状
            </view>
            <view class="search-box">
                <input 
                    class="search-input" 
                    placeholder="搜索症状，如：头痛、发热" 
                    v-model="searchKeyword"
                    @input="handleSearch"
                    confirm-type="search"
                />
                <text class="search-icon">🔍</text>
            </view>
            <view class="symptom-list" v-if="loading">
                <view class="loading-text">加载中...</view>
            </view>
            <view class="symptom-list" v-else-if="searchResults.length > 0">
                <view class="symptom-tag" 
                    v-for="item in searchResults" 
                    :key="item.id"
                    :class="{ active: selectedSymptoms.includes(item.id) }"
                    @click="toggleSymptom(item.id)"
                >
                    {{ item.name }}
                </view>
            </view>
            <view class="symptom-list" v-else>
                <view class="symptom-category" v-for="category in symptomCategories" :key="category.id">
                    <view class="category-name">{{ category.name }}</view>
                    <view class="symptom-tags">
                        <view class="symptom-tag" 
                            v-for="item in category.children" 
                            :key="item.id"
                            :class="{ active: selectedSymptoms.includes(item.id) }"
                            @click="toggleSymptom(item.id)"
                        >
                            {{ item.name }}
                        </view>
                    </view>
                </view>
            </view>
            <view class="selected-tip" v-if="selectedSymptoms.length > 0">
                已选择 {{ selectedSymptoms.length }} 个症状
            </view>
        </view>

        <view class="section">
            <view class="section-title">
                <text class="title-icon">🤖</text>
                AI智能问诊
            </view>
            <view class="ai-input-box">
                <textarea 
                    class="ai-input" 
                    placeholder="请描述您的症状，如：最近三天一直头痛，伴有轻微恶心..."
                    v-model="aiSymptomText"
                    maxlength="500"
                />
                <view class="char-count">{{ aiSymptomText.length }}/500</view>
            </view>
        </view>

        <view class="btn-group">
            <button class="btn-primary" @click="handleDiagnose" :disabled="selectedSymptoms.length === 0 && !aiSymptomText">
                开始导诊
            </button>
            <button class="btn-secondary" @click="handleAiDiagnose" :disabled="!aiSymptomText">
                AI智能分诊
            </button>
        </view>

        <view class="tip-box">
            <text class="tip-icon">💡</text>
            <text class="tip-text">智能导诊仅供参考，具体诊断请以医生为准</text>
        </view>
    </view>
</template>

<script>
import { getSymptomList, searchSymptoms, diagnose, aiDiagnose } from '../../api/symptom.js'

export default {
    data() {
        return {
            loading: false,
            searchKeyword: '',
            searchResults: [],
            symptomCategories: [],
            selectedSymptoms: [],
            aiSymptomText: ''
        }
    },
    onLoad() {
        this.loadSymptomList()
    },
    methods: {
        async loadSymptomList() {
            this.loading = true
            try {
                const res = await getSymptomList(0)
                if (res.code === 200) {
                    this.symptomCategories = res.data || []
                } else {
                    uni.showToast({
                        title: res.msg || '加载失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                uni.showToast({
                    title: '网络错误，请稍后重试',
                    icon: 'none'
                })
            } finally {
                this.loading = false
            }
        },

        async handleSearch() {
            if (!this.searchKeyword.trim()) {
                this.searchResults = []
                return
            }
            try {
                const res = await searchSymptoms(this.searchKeyword.trim())
                if (res.code === 200) {
                    this.searchResults = res.data || []
                }
            } catch (e) {
                console.error(e)
            }
        },

        toggleSymptom(id) {
            const index = this.selectedSymptoms.indexOf(id)
            if (index > -1) {
                this.selectedSymptoms.splice(index, 1)
            } else {
                this.selectedSymptoms.push(id)
            }
        },

        async handleDiagnose() {
            if (this.selectedSymptoms.length === 0) {
                uni.showToast({
                    title: '请至少选择一个症状',
                    icon: 'none'
                })
                return
            }
            uni.showLoading({ title: '分析中...' })
            try {
                const res = await diagnose(this.selectedSymptoms)
                uni.hideLoading()
                if (res.code === 200) {
                    uni.navigateTo({
                        url: '/pages/guide/result?data=' + encodeURIComponent(JSON.stringify(res.data))
                    })
                } else {
                    uni.showToast({
                        title: res.msg || '导诊失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                uni.hideLoading()
                uni.showToast({
                    title: '网络错误，请稍后重试',
                    icon: 'none'
                })
            }
        },

        async handleAiDiagnose() {
            if (!this.aiSymptomText.trim()) {
                uni.showToast({
                    title: '请描述您的症状',
                    icon: 'none'
                })
                return
            }
            uni.showLoading({ title: 'AI分析中...' })
            try {
                const res = await aiDiagnose(this.aiSymptomText.trim())
                uni.hideLoading()
                if (res.code === 200) {
                    uni.navigateTo({
                        url: '/pages/guide/result?data=' + encodeURIComponent(JSON.stringify(res.data))
                    })
                } else {
                    uni.showToast({
                        title: res.msg || 'AI分诊失败',
                        icon: 'none'
                    })
                }
            } catch (e) {
                uni.hideLoading()
                uni.showToast({
                    title: '网络错误，请稍后重试',
                    icon: 'none'
                })
            }
        }
    }
}
</script>

<style scoped>
.guide-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 48rpx;
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

.section {
    background: #fff;
    margin: 24rpx;
    border-radius: 16rpx;
    padding: 24rpx;
}

.section-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 20rpx;
    display: flex;
    align-items: center;
}

.title-icon {
    margin-right: 8rpx;
    font-size: 32rpx;
}

.search-box {
    position: relative;
    margin-bottom: 20rpx;
}

.search-input {
    width: 100%;
    height: 72rpx;
    background: #f5f5f5;
    border-radius: 36rpx;
    padding: 0 60rpx 0 24rpx;
    font-size: 26rpx;
    box-sizing: border-box;
}

.search-icon {
    position: absolute;
    right: 24rpx;
    top: 50%;
    transform: translateY(-50%);
    font-size: 28rpx;
    color: #999;
}

.loading-text {
    text-align: center;
    color: #999;
    font-size: 26rpx;
    padding: 40rpx 0;
}

.symptom-category {
    margin-bottom: 20rpx;
}

.category-name {
    font-size: 26rpx;
    color: #666;
    margin-bottom: 12rpx;
    font-weight: 500;
}

.symptom-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
}

.symptom-tag {
    padding: 12rpx 24rpx;
    background: #f5f5f5;
    border-radius: 32rpx;
    font-size: 24rpx;
    color: #666;
    border: 2rpx solid transparent;
    transition: all 0.2s;
}

.symptom-tag.active {
    background: #e6f7ff;
    color: #1989fa;
    border-color: #1989fa;
}

.selected-tip {
    margin-top: 20rpx;
    padding-top: 20rpx;
    border-top: 1rpx solid #eee;
    font-size: 24rpx;
    color: #1989fa;
    text-align: center;
}

.ai-input-box {
    position: relative;
}

.ai-input {
    width: 100%;
    height: 200rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    padding: 20rpx;
    font-size: 26rpx;
    box-sizing: border-box;
    line-height: 1.6;
}

.char-count {
    position: absolute;
    right: 16rpx;
    bottom: 16rpx;
    font-size: 22rpx;
    color: #999;
}

.btn-group {
    padding: 0 24rpx;
    margin-top: 24rpx;
}

.btn-primary {
    width: 100%;
    height: 88rpx;
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    border-radius: 44rpx;
    font-size: 30rpx;
    margin-bottom: 20rpx;
    border: none;
}

.btn-primary[disabled] {
    opacity: 0.5;
}

.btn-secondary {
    width: 100%;
    height: 88rpx;
    background: #fff;
    color: #1989fa;
    border-radius: 44rpx;
    font-size: 30rpx;
    border: 2rpx solid #1989fa;
}

.btn-secondary[disabled] {
    opacity: 0.5;
}

.tip-box {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 32rpx;
    padding: 0 24rpx;
    font-size: 22rpx;
    color: #999;
}

.tip-icon {
    margin-right: 8rpx;
}
</style>
