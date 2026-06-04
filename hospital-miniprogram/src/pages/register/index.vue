<template>
    <view class="register-container">
        <view class="header">
            <view class="header-title">预约挂号</view>
            <view class="header-subtitle">便捷就医，从预约开始</view>
        </view>

        <view class="tab-bar">
            <view 
                class="tab-item" 
                :class="{ active: activeTab === 'appointment' }"
                @click="switchTab('appointment')"
            >
                预约挂号
            </view>
            <view 
                class="tab-item" 
                :class="{ active: activeTab === 'today' }"
                @click="switchTab('today')"
            >
                当日挂号
            </view>
        </view>

        <view class="search-box">
            <input 
                class="search-input" 
                placeholder="搜索科室、医生姓名/拼音" 
                v-model="searchKeyword"
                confirm-type="search"
                @confirm="handleSearch"
            />
            <text class="search-icon" @click="handleSearch">🔍</text>
        </view>

        <view class="quick-entry">
            <view class="entry-item" @click="goToDepartment">
                <view class="entry-icon">🏥</view>
                <text class="entry-text">按科室挂号</text>
            </view>
            <view class="entry-item" @click="goToGuide">
                <view class="entry-icon">🤖</view>
                <text class="entry-text">智能导诊</text>
            </view>
            <view class="entry-item" @click="goToMyAppointment">
                <view class="entry-icon">📋</view>
                <text class="entry-text">我的挂号</text>
            </view>
        </view>

        <view class="section">
            <view class="section-title">
                <text class="title-icon">📅</text>
                热门科室
            </view>
            <view class="dept-grid" v-if="!loading">
                <view 
                    class="dept-item" 
                    v-for="dept in hotDepartments" 
                    :key="dept.id"
                    @click="goToDoctorList(dept)"
                >
                    <view class="dept-icon">{{ dept.icon || '🏥' }}</view>
                    <text class="dept-name">{{ dept.name }}</text>
                    <text class="dept-count">{{ dept.doctorCount || 0 }}位医生</text>
                </view>
            </view>
            <view class="loading-text" v-else>加载中...</view>
        </view>

        <view class="section" v-if="activeTab === 'appointment'">
            <view class="section-title">
                <text class="title-icon">🏥</text>
                全部科室
            </view>
            <view class="dept-tree" v-if="!loading">
                <view 
                    class="dept-group" 
                    v-for="group in departmentTree" 
                    :key="group.id"
                >
                    <view class="dept-group-header" @click="toggleGroup(group.id)">
                        <text class="group-name">{{ group.name }}</text>
                        <text class="group-arrow" :class="{ expanded: expandedGroups.includes(group.id) }">›</text>
                    </view>
                    <view class="dept-sub-list" v-if="expandedGroups.includes(group.id)">
                        <view 
                            class="dept-sub-item" 
                            v-for="subDept in group.children" 
                            :key="subDept.id"
                            @click="goToDoctorList(subDept)"
                        >
                            <text class="sub-dept-name">{{ subDept.name }}</text>
                            <text class="sub-dept-desc" v-if="subDept.description">{{ subDept.description }}</text>
                            <text class="arrow">›</text>
                        </view>
                    </view>
                </view>
            </view>
            <view class="loading-text" v-else>加载中...</view>
        </view>

        <view class="section" v-if="activeTab === 'today'">
            <view class="section-title">
                <text class="title-icon">⏰</text>
                今日可挂号科室
            </view>
            <view class="today-list" v-if="!loading">
                <view 
                    class="today-item" 
                    v-for="item in todayDepartments" 
                    :key="item.id"
                    @click="goToTodayRegister(item)"
                >
                    <view class="today-info">
                        <text class="today-name">{{ item.name }}</text>
                        <text class="today-doctors">{{ item.doctorCount }}位医生出诊</text>
                    </view>
                    <view class="today-right">
                        <text class="today-remaining">剩余{{ item.remainingCount }}号</text>
                        <text class="arrow">›</text>
                    </view>
                </view>
                <view class="empty-text" v-if="todayDepartments.length === 0">
                    今日暂无可挂号科室
                </view>
            </view>
            <view class="loading-text" v-else>加载中...</view>
        </view>
    </view>
</template>

<script>
import { getDepartmentTree, searchDepartments } from '../../api/department.js'

export default {
    data() {
        return {
            activeTab: 'appointment',
            loading: false,
            searchKeyword: '',
            departmentTree: [],
            hotDepartments: [],
            todayDepartments: [],
            expandedGroups: []
        }
    },
    onLoad() {
        this.loadDepartmentTree()
    },
    methods: {
        switchTab(tab) {
            this.activeTab = tab
        },

        async loadDepartmentTree() {
            this.loading = true
            try {
                const res = await getDepartmentTree()
                if (res.code === 200) {
                    this.departmentTree = res.data || []
                    this.hotDepartments = this.getHotDepartments()
                    this.todayDepartments = this.getTodayDepartments()
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

        getHotDepartments() {
            const hot = []
            this.departmentTree.forEach(group => {
                if (group.children && group.children.length > 0) {
                    hot.push(...group.children.slice(0, 2).map(d => ({
                        ...d,
                        icon: this.getDeptIcon(d.name),
                        doctorCount: Math.floor(Math.random() * 10) + 3
                    })))
                }
            })
            return hot.slice(0, 8)
        },

        getTodayDepartments() {
            const today = []
            this.departmentTree.forEach(group => {
                if (group.children && group.children.length > 0) {
                    group.children.forEach(d => {
                        if (Math.random() > 0.3) {
                            today.push({
                                ...d,
                                doctorCount: Math.floor(Math.random() * 5) + 1,
                                remainingCount: Math.floor(Math.random() * 20)
                            })
                        }
                    })
                }
            })
            return today
        },

        getDeptIcon(name) {
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
                '骨科': '🦴'
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

        async handleSearch() {
            if (!this.searchKeyword.trim()) {
                return
            }
            uni.navigateTo({
                url: '/pages/search/index?keyword=' + encodeURIComponent(this.searchKeyword.trim())
            })
        },

        goToDepartment() {
            uni.navigateTo({
                url: '/pages/register/department'
            })
        },

        goToGuide() {
            uni.navigateTo({
                url: '/pages/guide/index'
            })
        },

        goToMyAppointment() {
            uni.navigateTo({
                url: '/pages/appointment/list'
            })
        },

        goToDoctorList(dept) {
            uni.navigateTo({
                url: '/pages/register/doctor-list?departmentId=' + dept.id + '&departmentName=' + dept.name
            })
        },

        goToTodayRegister(dept) {
            uni.navigateTo({
                url: '/pages/register/today?departmentId=' + dept.id + '&departmentName=' + dept.name
            })
        }
    }
}
</script>

<style scoped>
.register-container {
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

.tab-bar {
    display: flex;
    background: #fff;
    margin: -24rpx 24rpx 24rpx;
    border-radius: 16rpx;
    padding: 8rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.tab-item {
    flex: 1;
    height: 72rpx;
    line-height: 72rpx;
    text-align: center;
    font-size: 28rpx;
    color: #666;
    border-radius: 12rpx;
    transition: all 0.2s;
}

.tab-item.active {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    font-weight: bold;
}

.search-box {
    position: relative;
    margin: 0 24rpx 24rpx;
}

.search-input {
    width: 100%;
    height: 72rpx;
    background: #fff;
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

.quick-entry {
    display: flex;
    background: #fff;
    margin: 0 24rpx 24rpx;
    border-radius: 16rpx;
    padding: 24rpx 0;
}

.entry-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.entry-icon {
    width: 80rpx;
    height: 80rpx;
    background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 36rpx;
    margin-bottom: 8rpx;
}

.entry-text {
    font-size: 24rpx;
    color: #333;
}

.section {
    background: #fff;
    margin: 0 24rpx 24rpx;
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
    font-size: 28rpx;
}

.dept-grid {
    display: flex;
    flex-wrap: wrap;
}

.dept-item {
    width: 25%;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16rpx 0;
}

.dept-icon {
    width: 72rpx;
    height: 72rpx;
    background: #f5f5f5;
    border-radius: 18rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32rpx;
    margin-bottom: 8rpx;
}

.dept-name {
    font-size: 24rpx;
    color: #333;
    margin-bottom: 4rpx;
}

.dept-count {
    font-size: 20rpx;
    color: #999;
}

.dept-tree {
    margin-top: 16rpx;
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
    justify-content: space-between;
    padding: 24rpx 0;
}

.group-name {
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
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
    padding-bottom: 16rpx;
}

.dept-sub-item {
    display: flex;
    align-items: center;
    padding: 20rpx 16rpx;
    background: #f9f9f9;
    border-radius: 12rpx;
    margin-bottom: 12rpx;
}

.sub-dept-name {
    flex: 1;
    font-size: 26rpx;
    color: #333;
}

.sub-dept-desc {
    font-size: 22rpx;
    color: #999;
    margin-right: 12rpx;
}

.arrow {
    font-size: 28rpx;
    color: #ccc;
}

.today-list {
    margin-top: 16rpx;
}

.today-item {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f5f5f5;
}

.today-item:last-child {
    border-bottom: none;
}

.today-info {
    flex: 1;
}

.today-name {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 4rpx;
    display: block;
}

.today-doctors {
    font-size: 22rpx;
    color: #999;
}

.today-right {
    display: flex;
    align-items: center;
}

.today-remaining {
    font-size: 24rpx;
    color: #52c41a;
    margin-right: 8rpx;
}

.loading-text {
    text-align: center;
    color: #999;
    font-size: 26rpx;
    padding: 40rpx 0;
}

.empty-text {
    text-align: center;
    color: #999;
    font-size: 26rpx;
    padding: 40rpx 0;
}
</style>
