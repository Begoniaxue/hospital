<template>
    <view class="doctor-list-container">
        <view class="header">
            <view class="header-title">{{ departmentName }}</view>
            <view class="header-subtitle">选择就诊医生</view>
        </view>

        <view class="date-filter">
            <scroll-view scroll-x class="date-scroll">
                <view 
                    class="date-item" 
                    v-for="(date, index) in dateList" 
                    :key="index"
                    :class="{ active: selectedDate === date.date }"
                    @click="selectDate(date.date)"
                >
                    <text class="date-week">{{ date.week }}</text>
                    <text class="date-day">{{ date.day }}</text>
                    <text class="date-status" v-if="date.hasSchedule">有号</text>
                    <text class="date-status no-schedule" v-else>无号</text>
                </view>
            </scroll-view>
        </view>

        <view class="content" v-if="!loading">
            <view class="doctor-list">
                <view 
                    class="doctor-card" 
                    v-for="doctor in doctors" 
                    :key="doctor.id"
                    @click="goToDoctorDetail(doctor)"
                >
                    <image class="doctor-avatar" :src="doctor.avatar || '/static/default-avatar.png'" mode="aspectFill" />
                    <view class="doctor-info">
                        <view class="doctor-name-row">
                            <text class="doctor-name">{{ doctor.name }}</text>
                            <text class="doctor-title">{{ doctor.title }}</text>
                        </view>
                        <view class="doctor-skill" v-if="doctor.skill">{{ doctor.skill }}</view>
                        <view class="doctor-schedule" v-if="doctor.schedule">
                            <view class="schedule-item" :class="{ disabled: !doctor.schedule.morning }">
                                <text class="schedule-label">上午</text>
                                <text class="schedule-count">{{ doctor.schedule.morning ? '剩余' + doctor.schedule.morningRemaining + '号' : '约满' }}</text>
                            </view>
                            <view class="schedule-item" :class="{ disabled: !doctor.schedule.afternoon }">
                                <text class="schedule-label">下午</text>
                                <text class="schedule-count">{{ doctor.schedule.afternoon ? '剩余' + doctor.schedule.afternoonRemaining + '号' : '约满' }}</text>
                            </view>
                        </view>
                    </view>
                    <view class="doctor-right">
                        <text class="doctor-fee">¥{{ doctor.fee }}</text>
                        <text class="arrow">›</text>
                    </view>
                </view>
                <view class="empty-box" v-if="doctors.length === 0">
                    <text class="empty-icon">👨‍⚕️</text>
                    <text class="empty-text">当日暂无出诊医生</text>
                    <text class="empty-tip">请选择其他日期</text>
                </view>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
import { getDoctorsByDepartment } from '../../api/doctor.js'

export default {
    data() {
        return {
            loading: false,
            departmentId: '',
            departmentName: '',
            selectedDate: '',
            dateList: [],
            doctors: []
        }
    },
    onLoad(options) {
        this.departmentId = options.departmentId
        this.departmentName = options.departmentName || '科室医生'
        this.generateDateList()
        this.loadDoctors()
    },
    methods: {
        generateDateList() {
            const today = new Date()
            const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
            for (let i = 0; i < 7; i++) {
                const date = new Date(today)
                date.setDate(today.getDate() + i)
                const dateStr = date.toISOString().split('T')[0]
                this.dateList.push({
                    date: dateStr,
                    week: i === 0 ? '今天' : i === 1 ? '明天' : weekDays[date.getDay()],
                    day: (date.getMonth() + 1) + '/' + date.getDate(),
                    hasSchedule: Math.random() > 0.3
                })
            }
            this.selectedDate = this.dateList[0].date
        },

        async loadDoctors() {
            this.loading = true
            try {
                const res = await getDoctorsByDepartment(this.departmentId)
                if (res.code === 200) {
                    this.doctors = this.processDoctorData(res.data || [])
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

        processDoctorData(doctors) {
            return doctors.map(doctor => ({
                ...doctor,
                schedule: {
                    morning: Math.random() > 0.3,
                    morningRemaining: Math.floor(Math.random() * 15),
                    afternoon: Math.random() > 0.4,
                    afternoonRemaining: Math.floor(Math.random() * 15)
                }
            }))
        },

        selectDate(date) {
            this.selectedDate = date
            this.loadDoctors()
        },

        goToDoctorDetail(doctor) {
            uni.navigateTo({
                url: '/pages/register/doctor-detail?doctorId=' + doctor.id + '&departmentName=' + this.departmentName
            })
        }
    }
}
</script>

<style scoped>
.doctor-list-container {
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

.date-filter {
    background: #fff;
    padding: 20rpx 0;
    margin-bottom: 24rpx;
}

.date-scroll {
    white-space: nowrap;
    padding: 0 24rpx;
}

.date-item {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    padding: 16rpx 28rpx;
    margin-right: 16rpx;
    border-radius: 12rpx;
    background: #f5f5f5;
    min-width: 100rpx;
}

.date-item.active {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
}

.date-week {
    font-size: 24rpx;
    color: #333;
    margin-bottom: 4rpx;
}

.date-item.active .date-week {
    color: #fff;
}

.date-day {
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 4rpx;
}

.date-item.active .date-day {
    color: #fff;
}

.date-status {
    font-size: 20rpx;
    color: #52c41a;
}

.date-status.no-schedule {
    color: #999;
}

.date-item.active .date-status {
    color: rgba(255, 255, 255, 0.9);
}

.content {
    padding: 0 24rpx;
}

.doctor-list {
    margin-bottom: 24rpx;
}

.doctor-card {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
}

.doctor-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    background: #f5f5f5;
    margin-right: 20rpx;
    flex-shrink: 0;
}

.doctor-info {
    flex: 1;
    min-width: 0;
}

.doctor-name-row {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.doctor-name {
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-right: 12rpx;
}

.doctor-title {
    font-size: 22rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.doctor-skill {
    font-size: 22rpx;
    color: #999;
    margin-bottom: 12rpx;
    line-height: 1.5;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.doctor-schedule {
    display: flex;
    gap: 16rpx;
}

.schedule-item {
    flex: 1;
    padding: 8rpx 12rpx;
    background: #f0f9ff;
    border-radius: 8rpx;
    text-align: center;
}

.schedule-item.disabled {
    background: #f5f5f5;
}

.schedule-label {
    display: block;
    font-size: 20rpx;
    color: #666;
    margin-bottom: 2rpx;
}

.schedule-count {
    display: block;
    font-size: 20rpx;
    color: #52c41a;
}

.schedule-item.disabled .schedule-count {
    color: #ff4d4f;
}

.doctor-right {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    margin-left: 16rpx;
    flex-shrink: 0;
}

.doctor-fee {
    font-size: 28rpx;
    color: #ff6b6b;
    font-weight: bold;
    margin-bottom: 8rpx;
}

.arrow {
    font-size: 28rpx;
    color: #ccc;
}

.empty-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
}

.empty-icon {
    font-size: 100rpx;
    margin-bottom: 20rpx;
}

.empty-text {
    font-size: 28rpx;
    color: #666;
    margin-bottom: 8rpx;
}

.empty-tip {
    font-size: 24rpx;
    color: #999;
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
