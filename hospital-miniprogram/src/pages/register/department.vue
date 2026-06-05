<template>
    <view class="department-container">
        <view class="header">
            <view class="header-title">选择科室</view>
            <view class="header-subtitle">请选择就诊科室</view>
        </view>

        <view class="search-box">
            <input 
                class="search-input" 
                placeholder="搜索科室名称" 
                v-model="searchKeyword"
                @input="handleSearch"
                confirm-type="search"
            />
            <text class="search-icon">🔍</text>
            <text class="search-cancel" v-if="searchKeyword" @click="clearSearch">取消</text>
        </view>

        <view class="content" v-if="!loading">
            <view class="search-result" v-if="searchKeyword && searchResults.length > 0">
                <view class="result-title">搜索结果</view>
                <view 
                    class="result-item" 
                    v-for="item in searchResults" 
                    :key="item.id"
                    @click="goToDoctorList(item)"
                >
                    <view class="result-info">
                        <text class="result-name">{{ item.deptName || item.name }}</text>
                        <text class="result-desc" v-if="item.description">{{ item.description }}</text>
                    </view>
                    <text class="arrow">›</text>
                </view>
            </view>

            <view class="no-result" v-else-if="searchKeyword && searchResults.length === 0">
                <text class="no-result-icon">🔍</text>
                <text class="no-result-text">未找到相关科室</text>
            </view>

            <view class="department-tree" v-else>
                <view 
                    class="dept-group" 
                    v-for="group in departmentTree" 
                    :key="group.id"
                >
                    <view class="dept-group-header" @click="toggleGroup(group.id)">
                        <text class="group-icon">{{ getGroupIcon(group.deptName || group.name) }}</text>
                        <text class="group-name">{{ group.deptName || group.name }}</text>
                        <text class="group-count">{{ group.children ? group.children.length : 0 }}个科室</text>
                        <text class="group-arrow" :class="{ expanded: expandedGroups.includes(group.id) }">›</text>
                    </view>
                    <view class="dept-sub-list" v-if="expandedGroups.includes(group.id) && group.children">
                        <view 
                            class="dept-sub-item" 
                            v-for="subDept in group.children" 
                            :key="subDept.id"
                            @click="goToDoctorList(subDept)"
                        >
                            <view class="sub-dept-info">
                                <text class="sub-dept-name">{{ subDept.deptName || subDept.name }}</text>
                                <text class="sub-dept-desc" v-if="subDept.description">{{ subDept.description }}</text>
                            </view>
                            <view class="sub-dept-right">
                                <text class="sub-dept-doctors" v-if="subDept.doctorCount">{{ subDept.doctorCount }}位医生</text>
                                <text class="arrow">›</text>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
import { getDepartmentTree, searchDepartments } from '../../api/department.js'

export default {
    data() {
        return {
            loading: false,
            searchKeyword: '',
            searchResults: [],
            departmentTree: [],
            expandedGroups: []
        }
    },
    onLoad() {
        this.loadDepartmentTree()
    },
    methods: {
        async loadDepartmentTree() {
            this.loading = true
            try {
                const res = await getDepartmentTree()
                if (res.code === 200) {
                    this.departmentTree = res.data || []
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
                const res = await searchDepartments(this.searchKeyword.trim())
                if (res.code === 200) {
                    this.searchResults = res.data || []
                }
            } catch (e) {
                console.error(e)
            }
        },

        clearSearch() {
            this.searchKeyword = ''
            this.searchResults = []
        },

        getGroupIcon(name) {
            const icons = {
                '内科': '🫀',
                '外科': '🔪',
                '儿科': '👶',
                '妇产科': '🤰',
                '眼科': '👁️',
                '耳鼻喉科': '👂',
                '口腔科': '🦷',
                '皮肤科': '🧴',
                '神经科': '🧠',
                '骨科': '🦴',
                '中医科': '🌿',
                '肿瘤科': '🎗️',
                '急诊科': '🚑',
                '康复科': '💪'
            }
            return icons[name] || '🏥'
        },

        toggleGroup(id) {
            const index = this.expandedGroups.indexOf(id)
            if (index > -1) {
                this.expandedGroups.splice(index, 1)
            } else {
                this.expandedGroups.push(id)
            }
        },

        goToDoctorList(dept) {
            const deptName = dept.deptName || dept.name || ''
            uni.navigateTo({
                url: '/pages/register/doctor-list?departmentId=' + dept.id + '&departmentName=' + encodeURIComponent(deptName)
            })
        }
    }
}
</script>

<style scoped>
.department-container {
    min-height: 100vh;
    background: #f5f5f5;
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

.search-box {
    position: relative;
    padding: 24rpx;
    background: #fff;
    display: flex;
    align-items: center;
}

.search-input {
    flex: 1;
    height: 72rpx;
    background: #f5f5f5;
    border-radius: 36rpx;
    padding: 0 60rpx 0 24rpx;
    font-size: 26rpx;
    box-sizing: border-box;
}

.search-icon {
    position: absolute;
    left: 52rpx;
    top: 50%;
    transform: translateY(-50%);
    font-size: 28rpx;
    color: #999;
}

.search-cancel {
    margin-left: 20rpx;
    font-size: 26rpx;
    color: #1989fa;
}

.content {
    padding: 24rpx;
}

.search-result {
    background: #fff;
    border-radius: 16rpx;
    padding: 8rpx 0;
}

.result-title {
    font-size: 26rpx;
    color: #999;
    padding: 16rpx 24rpx 8rpx;
}

.result-item {
    display: flex;
    align-items: center;
    padding: 20rpx 24rpx;
    border-bottom: 1rpx solid #f5f5f5;
}

.result-item:last-child {
    border-bottom: none;
}

.result-info {
    flex: 1;
}

.result-name {
    display: block;
    font-size: 28rpx;
    color: #333;
    margin-bottom: 4rpx;
}

.result-desc {
    display: block;
    font-size: 22rpx;
    color: #999;
}

.no-result {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
}

.no-result-icon {
    font-size: 100rpx;
    margin-bottom: 20rpx;
}

.no-result-text {
    font-size: 26rpx;
    color: #999;
}

.department-tree {
    background: #fff;
    border-radius: 16rpx;
    overflow: hidden;
}

.dept-group {
    border-bottom: 1rpx solid #f5f5f5;
}

.dept-group:last-child {
    border-bottom: none;
}

.dept-group-header {
    display: flex;
    align-items: center;
    padding: 24rpx;
}

.group-icon {
    font-size: 36rpx;
    margin-right: 16rpx;
}

.group-name {
    flex: 1;
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
}

.group-count {
    font-size: 22rpx;
    color: #999;
    margin-right: 12rpx;
}

.group-arrow {
    font-size: 32rpx;
    color: #ccc;
    transition: transform 0.2s;
}

.group-arrow.expanded {
    transform: rotate(90deg);
}

.dept-sub-list {
    background: #f9f9f9;
    padding: 0 24rpx 16rpx;
}

.dept-sub-item {
    display: flex;
    align-items: center;
    padding: 20rpx 16rpx;
    background: #fff;
    border-radius: 12rpx;
    margin-top: 12rpx;
}

.sub-dept-info {
    flex: 1;
}

.sub-dept-name {
    display: block;
    font-size: 26rpx;
    color: #333;
    margin-bottom: 4rpx;
}

.sub-dept-desc {
    display: block;
    font-size: 22rpx;
    color: #999;
}

.sub-dept-right {
    display: flex;
    align-items: center;
}

.sub-dept-doctors {
    font-size: 22rpx;
    color: #1989fa;
    margin-right: 8rpx;
}

.arrow {
    font-size: 28rpx;
    color: #ccc;
}

.loading-box {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
}

.loading-text {
    font-size: 26rpx;
    color: #999;
}
</style>
