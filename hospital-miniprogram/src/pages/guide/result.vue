<template>
    <view class="result-container">
        <view class="header">
            <view class="header-title">导诊结果</view>
            <view class="header-subtitle">根据您的症状，为您推荐以下科室</view>
        </view>

        <view class="result-content" v-if="resultData">
            <view class="department-list">
                <view class="department-card" 
                    v-for="(item, index) in resultData.departments" 
                    :key="item.id"
                    @click="goToRegister(item)"
                >
                    <view class="rank-badge" v-if="index < 3">{{ index + 1 }}</view>
                    <view class="dept-info">
                        <view class="dept-name">
                            {{ item.name }}
                            <text class="dept-level" v-if="item.level">{{ item.level }}</text>
                        </view>
                        <view class="dept-desc" v-if="item.description">{{ item.description }}</view>
                        <view class="dept-tags" v-if="item.tags && item.tags.length > 0">
                            <text class="tag" v-for="tag in item.tags" :key="tag">{{ tag }}</text>
                        </view>
                    </view>
                    <view class="match-info">
                        <view class="match-score">{{ item.matchRate }}%</view>
                        <view class="match-label">匹配度</view>
                    </view>
                    <text class="arrow">›</text>
                </view>
            </view>

            <view class="advice-box" v-if="resultData.advice">
                <view class="advice-title">
                    <text class="advice-icon">💡</text>
                    就诊建议
                </view>
                <view class="advice-content">{{ resultData.advice }}</view>
            </view>

            <view class="warning-box" v-if="resultData.warning">
                <view class="warning-title">
                    <text class="warning-icon">⚠️</text>
                    温馨提示
                </view>
                <view class="warning-content">{{ resultData.warning }}</view>
            </view>

            <view class="symptom-review" v-if="resultData.symptoms && resultData.symptoms.length > 0">
                <view class="review-title">您选择的症状</view>
                <view class="review-tags">
                    <text class="review-tag" v-for="symptom in resultData.symptoms" :key="symptom">{{ symptom }}</text>
                </view>
            </view>
        </view>

        <view class="empty-box" v-else>
            <text class="empty-icon">📋</text>
            <text class="empty-text">暂无导诊结果</text>
        </view>

        <view class="btn-group">
            <button class="btn-primary" @click="goBack">
                重新选择症状
            </button>
            <button class="btn-secondary" @click="goHome">
                返回首页
            </button>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            resultData: null
        }
    },
    onLoad(options) {
        if (options.data) {
            try {
                this.resultData = JSON.parse(decodeURIComponent(options.data))
            } catch (e) {
                console.error('解析数据失败', e)
                uni.showToast({
                    title: '数据解析失败',
                    icon: 'none'
                })
            }
        }
    },
    methods: {
        goToRegister(dept) {
            uni.navigateTo({
                url: '/pages/register/doctor-list?departmentId=' + dept.id + '&departmentName=' + dept.name
            })
        },
        goBack() {
            uni.navigateBack()
        },
        goHome() {
            uni.switchTab({
                url: '/pages/index/index'
            })
        }
    }
}
</script>

<style scoped>
.result-container {
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

.result-content {
    padding: 24rpx;
}

.department-list {
    margin-bottom: 24rpx;
}

.department-card {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
    position: relative;
}

.rank-badge {
    position: absolute;
    left: 0;
    top: 0;
    width: 48rpx;
    height: 48rpx;
    background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
    color: #fff;
    font-size: 24rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 16rpx 0 16rpx 0;
}

.dept-info {
    flex: 1;
    padding-left: 32rpx;
}

.dept-name {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 8rpx;
    display: flex;
    align-items: center;
}

.dept-level {
    font-size: 22rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    margin-left: 12rpx;
    font-weight: normal;
}

.dept-desc {
    font-size: 24rpx;
    color: #666;
    margin-bottom: 12rpx;
    line-height: 1.5;
}

.dept-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8rpx;
}

.tag {
    font-size: 22rpx;
    color: #999;
    background: #f5f5f5;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.match-info {
    text-align: center;
    margin-right: 16rpx;
}

.match-score {
    font-size: 36rpx;
    font-weight: bold;
    color: #1989fa;
}

.match-label {
    font-size: 22rpx;
    color: #999;
}

.arrow {
    font-size: 36rpx;
    color: #ccc;
}

.advice-box, .warning-box {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
}

.advice-title, .warning-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 12rpx;
    display: flex;
    align-items: center;
}

.advice-icon, .warning-icon {
    margin-right: 8rpx;
    font-size: 28rpx;
}

.advice-content {
    font-size: 26rpx;
    color: #666;
    line-height: 1.6;
}

.warning-box {
    background: #fffbe6;
}

.warning-content {
    font-size: 26rpx;
    color: #874d00;
    line-height: 1.6;
}

.symptom-review {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
}

.review-title {
    font-size: 26rpx;
    color: #666;
    margin-bottom: 12rpx;
}

.review-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
}

.review-tag {
    font-size: 24rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 8rpx 20rpx;
    border-radius: 24rpx;
}

.empty-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
}

.empty-icon {
    font-size: 120rpx;
    margin-bottom: 24rpx;
}

.empty-text {
    font-size: 28rpx;
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

.btn-secondary {
    width: 100%;
    height: 88rpx;
    background: #fff;
    color: #1989fa;
    border-radius: 44rpx;
    font-size: 30rpx;
    border: 2rpx solid #1989fa;
}
</style>
