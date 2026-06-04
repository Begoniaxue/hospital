<template>
    <view class="search-container">
        <view class="search-header">
            <view class="search-box">
                <text class="search-icon">🔍</text>
                <input
                    class="search-input"
                    placeholder="搜索科室、医生姓名/拼音"
                    v-model="keyword"
                    confirm-type="search"
                    @confirm="handleSearch"
                    @input="handleInput"
                    focus
                />
                <text class="clear-icon" v-if="keyword" @click="clearKeyword">✕</text>
            </view>
            <text class="cancel-btn" @click="goBack">取消</text>
        </view>

        <view class="search-content" v-if="!hasSearched">
            <view class="history-section" v-if="searchHistory.length > 0">
                <view class="section-header">
                    <text class="section-title">搜索历史</text>
                    <text class="clear-history" @click="clearHistory">清空</text>
                </view>
                <view class="tag-list">
                    <view
                        class="tag-item"
                        v-for="(item, index) in searchHistory"
                        :key="index"
                        @click="searchByHistory(item)"
                    >
                        {{ item }}
                    </view>
                </view>
            </view>

            <view class="hot-section">
                <view class="section-header">
                    <text class="section-title">🔥 热门搜索</text>
                </view>
                <view class="tag-list">
                    <view
                        class="tag-item hot"
                        v-for="(item, index) in hotSearchList"
                        :key="index"
                        @click="searchByHistory(item)"
                    >
                        <text class="hot-index" v-if="index < 3">{{ index + 1 }}</text>
                        {{ item }}
                    </view>
                </view>
            </view>
        </view>

        <view class="search-result" v-else>
            <view class="loading" v-if="loading">
                <text class="loading-text">搜索中...</text>
            </view>

            <view class="no-result" v-else-if="!loading && departmentResults.length === 0 && doctorResults.length === 0">
                <text class="no-result-icon">🔍</text>
                <text class="no-result-text">未找到相关结果</text>
                <text class="no-result-tip">试试其他关键词吧</text>
            </view>

            <view v-else>
                <view class="result-section" v-if="departmentResults.length > 0">
                    <view class="result-header">
                        <text class="result-title">科室</text>
                        <text class="result-count">{{ departmentResults.length }}个结果</text>
                    </view>
                    <view class="result-list">
                        <view
                            class="result-item"
                            v-for="item in departmentResults"
                            :key="item.id"
                            @click="goToDepartment(item)"
                        >
                            <view class="result-icon">🏥</view>
                            <view class="result-info">
                                <text class="result-name">{{ item.name }}</text>
                                <text class="result-desc" v-if="item.description">{{ item.description }}</text>
                            </view>
                            <text class="result-arrow">›</text>
                        </view>
                    </view>
                </view>

                <view class="result-section" v-if="doctorResults.length > 0">
                    <view class="result-header">
                        <text class="result-title">医生</text>
                        <text class="result-count">{{ doctorResults.length }}个结果</text>
                    </view>
                    <view class="result-list">
                        <view
                            class="result-item"
                            v-for="item in doctorResults"
                            :key="item.id"
                            @click="goToDoctorDetail(item)"
                        >
                            <image class="result-avatar" :src="item.avatar || '/static/default-avatar.png'" mode="aspectFill" />
                            <view class="result-info">
                                <view class="result-name-row">
                                    <text class="result-name">{{ item.name }}</text>
                                    <text class="result-title-tag">{{ item.title }}</text>
                                </view>
                                <text class="result-desc">{{ item.departmentName }} · {{ item.specialty }}</text>
                            </view>
                            <text class="result-arrow">›</text>
                        </view>
                    </view>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
import { searchDepartments } from '../../api/department.js'
import { searchDoctors } from '../../api/doctor.js'

export default {
    data() {
        return {
            keyword: '',
            loading: false,
            hasSearched: false,
            searchHistory: [],
            hotSearchList: ['内科', '外科', '儿科', '妇产科', '骨科', '皮肤科', '眼科', '口腔科'],
            departmentResults: [],
            doctorResults: []
        }
    },
    onLoad(options) {
        this.loadHistory()
        if (options.keyword) {
            this.keyword = decodeURIComponent(options.keyword)
            this.handleSearch()
        }
    },
    methods: {
        loadHistory() {
            try {
                const history = uni.getStorageSync('searchHistory')
                if (history) {
                    this.searchHistory = JSON.parse(history)
                }
            } catch (e) {
                this.searchHistory = []
            }
        },

        saveHistory(keyword) {
            if (!keyword) return
            const index = this.searchHistory.indexOf(keyword)
            if (index > -1) {
                this.searchHistory.splice(index, 1)
            }
            this.searchHistory.unshift(keyword)
            if (this.searchHistory.length > 10) {
                this.searchHistory = this.searchHistory.slice(0, 10)
            }
            uni.setStorageSync('searchHistory', JSON.stringify(this.searchHistory))
        },

        clearHistory() {
            uni.showModal({
                title: '提示',
                content: '确定清空搜索历史吗？',
                success: (res) => {
                    if (res.confirm) {
                        this.searchHistory = []
                        uni.removeStorageSync('searchHistory')
                        uni.showToast({
                            title: '已清空',
                            icon: 'success'
                        })
                    }
                }
            })
        },

        handleInput() {
            if (!this.keyword) {
                this.hasSearched = false
                this.departmentResults = []
                this.doctorResults = []
            }
        },

        clearKeyword() {
            this.keyword = ''
            this.hasSearched = false
            this.departmentResults = []
            this.doctorResults = []
        },

        searchByHistory(keyword) {
            this.keyword = keyword
            this.handleSearch()
        },

        async handleSearch() {
            if (!this.keyword.trim()) {
                uni.showToast({
                    title: '请输入搜索内容',
                    icon: 'none'
                })
                return
            }

            this.hasSearched = true
            this.loading = true
            this.saveHistory(this.keyword.trim())

            try {
                const [deptRes, docRes] = await Promise.all([
                    searchDepartments(this.keyword.trim()),
                    searchDoctors(this.keyword.trim())
                ])

                if (deptRes.code === 200) {
                    this.departmentResults = deptRes.data || []
                } else {
                    this.departmentResults = []
                }

                if (docRes.code === 200) {
                    this.doctorResults = docRes.data || []
                } else {
                    this.doctorResults = []
                }
            } catch (e) {
                uni.showToast({
                    title: '搜索失败，请稍后重试',
                    icon: 'none'
                })
                this.departmentResults = []
                this.doctorResults = []
            } finally {
                this.loading = false
            }
        },

        goToDepartment(dept) {
            uni.navigateTo({
                url: '/pages/register/doctor-list?departmentId=' + dept.id + '&departmentName=' + dept.name
            })
        },

        goToDoctorDetail(doctor) {
            uni.navigateTo({
                url: '/pages/register/doctor-detail?doctorId=' + doctor.id
            })
        },

        goBack() {
            uni.navigateBack()
        }
    }
}
</script>

<style scoped>
.search-container {
    min-height: 100vh;
    background: #f5f5f5;
}

.search-header {
    display: flex;
    align-items: center;
    padding: 24rpx;
    background: #fff;
    border-bottom: 1rpx solid #eee;
}

.search-box {
    flex: 1;
    display: flex;
    align-items: center;
    height: 72rpx;
    background: #f5f5f5;
    border-radius: 36rpx;
    padding: 0 24rpx;
    margin-right: 24rpx;
}

.search-icon {
    font-size: 28rpx;
    color: #999;
    margin-right: 12rpx;
}

.search-input {
    flex: 1;
    height: 72rpx;
    font-size: 28rpx;
    color: #333;
}

.clear-icon {
    font-size: 28rpx;
    color: #ccc;
    padding: 8rpx;
}

.cancel-btn {
    font-size: 28rpx;
    color: #1989fa;
}

.search-content {
    padding: 24rpx;
}

.history-section,
.hot-section {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
}

.section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20rpx;
}

.section-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
}

.clear-history {
    font-size: 24rpx;
    color: #999;
}

.tag-list {
    display: flex;
    flex-wrap: wrap;
}

.tag-item {
    display: flex;
    align-items: center;
    padding: 12rpx 24rpx;
    background: #f5f5f5;
    border-radius: 24rpx;
    font-size: 26rpx;
    color: #666;
    margin: 0 16rpx 16rpx 0;
}

.tag-item.hot {
    background: #fff7e6;
    color: #fa8c16;
}

.hot-index {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32rpx;
    height: 32rpx;
    background: linear-gradient(135deg, #ff7a45 0%, #fa541c 100%);
    color: #fff;
    font-size: 20rpx;
    border-radius: 6rpx;
    margin-right: 8rpx;
}

.search-result {
    padding: 24rpx;
}

.loading {
    text-align: center;
    padding: 80rpx 0;
}

.loading-text {
    font-size: 28rpx;
    color: #999;
}

.no-result {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 120rpx 0;
}

.no-result-icon {
    font-size: 120rpx;
    opacity: 0.3;
    margin-bottom: 24rpx;
}

.no-result-text {
    font-size: 32rpx;
    color: #333;
    margin-bottom: 12rpx;
}

.no-result-tip {
    font-size: 26rpx;
    color: #999;
}

.result-section {
    background: #fff;
    border-radius: 16rpx;
    margin-bottom: 24rpx;
    overflow: hidden;
}

.result-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24rpx;
    border-bottom: 1rpx solid #f5f5f5;
}

.result-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
}

.result-count {
    font-size: 24rpx;
    color: #999;
}

.result-list {
    padding: 0 24rpx;
}

.result-item {
    display: flex;
    align-items: center;
    padding: 24rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
}

.result-item:last-child {
    border-bottom: none;
}

.result-icon {
    width: 80rpx;
    height: 80rpx;
    background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 36rpx;
    margin-right: 20rpx;
    flex-shrink: 0;
}

.result-avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    margin-right: 20rpx;
    flex-shrink: 0;
}

.result-info {
    flex: 1;
    overflow: hidden;
}

.result-name-row {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.result-name {
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
    margin-right: 12rpx;
}

.result-title-tag {
    font-size: 22rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.result-desc {
    font-size: 24rpx;
    color: #999;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.result-arrow {
    font-size: 32rpx;
    color: #ccc;
    margin-left: 16rpx;
}
</style>
