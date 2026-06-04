<template>
    <view class="doctor-detail-container">
        <view class="header">
            <view class="doctor-info-card">
                <image class="doctor-avatar" :src="doctor.avatar || '/static/default-avatar.png'" mode="aspectFill" />
                <view class="doctor-info">
                    <view class="doctor-name-row">
                        <text class="doctor-name">{{ doctor.name }}</text>
                        <text class="doctor-title">{{ doctor.title }}</text>
                    </view>
                    <view class="doctor-dept">{{ departmentName }}</view>
                    <view class="doctor-stats">
                        <text class="stat-item">好评率 {{ doctor.goodRate || '98%' }}</text>
                        <text class="stat-item">接诊 {{ doctor.visitCount || '1000+' }}人次</text>
                    </view>
                </view>
            </view>
        </view>

        <view class="content" v-if="!loading">
            <view class="section">
                <view class="section-title">
                    <text class="title-icon">🎯</text>
                    专业特长
                </view>
                <view class="section-content">
                    <text class="content-text">{{ doctor.specialty || doctor.skill || '暂无信息' }}</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📝</text>
                    医生简介
                </view>
                <view class="section-content">
                    <text class="content-text">{{ doctor.introduction || '暂无信息' }}</text>
                </view>
            </view>

            <view class="section">
                <view class="section-title">
                    <text class="title-icon">📅</text>
                    出诊时间
                </view>
                <scroll-view scroll-x class="date-scroll">
                    <view 
                        class="date-item" 
                        v-for="(schedule, index) in schedules" 
                        :key="index"
                        :class="{ active: selectedDate === schedule.date }"
                        @click="selectDate(schedule)"
                    >
                        <text class="date-week">{{ schedule.week }}</text>
                        <text class="date-day">{{ schedule.day }}</text>
                        <view class="date-slots">
                            <view class="slot-badge morning" :class="{ disabled: !schedule.morning }">
                                {{ schedule.morning ? '上午' : '无' }}
                            </view>
                            <view class="slot-badge afternoon" :class="{ disabled: !schedule.afternoon }">
                                {{ schedule.afternoon ? '下午' : '无' }}
                            </view>
                        </view>
                    </view>
                </scroll-view>

                <view class="time-slots" v-if="selectedSchedule">
                    <view class="slot-header">选择时段</view>
                    <view class="slot-list">
                        <view 
                            class="slot-card" 
                            :class="{ 
                                disabled: !selectedSchedule.morning || selectedSchedule.morningRemaining <= 0,
                                selected: selectedSlot.period === 'morning'
                            }"
                            @click="selectSlot('morning')"
                        >
                            <view class="slot-time-info">
                                <text class="slot-period">上午</text>
                                <text class="slot-time-range">8:00-12:00</text>
                            </view>
                            <view class="slot-right">
                                <text class="slot-fee">¥{{ doctor.consultationFee || doctor.fee }}</text>
                                <text class="slot-remaining" :class="{ full: selectedSchedule.morningRemaining <= 0 }">
                                    {{ selectedSchedule.morningRemaining <= 0 ? '约满' : '剩余' + selectedSchedule.morningRemaining + '号' }}
                                </text>
                            </view>
                        </view>
                        <view 
                            class="slot-card" 
                            :class="{ 
                                disabled: !selectedSchedule.afternoon || selectedSchedule.afternoonRemaining <= 0,
                                selected: selectedSlot.period === 'afternoon'
                            }"
                            @click="selectSlot('afternoon')"
                        >
                            <view class="slot-time-info">
                                <text class="slot-period">下午</text>
                                <text class="slot-time-range">14:00-17:30</text>
                            </view>
                            <view class="slot-right">
                                <text class="slot-fee">¥{{ doctor.consultationFee || doctor.fee }}</text>
                                <text class="slot-remaining" :class="{ full: selectedSchedule.afternoonRemaining <= 0 }">
                                    {{ selectedSchedule.afternoonRemaining <= 0 ? '约满' : '剩余' + selectedSchedule.afternoonRemaining + '号' }}
                                </text>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>

        <view class="footer" v-if="selectedSlot.period">
            <view class="footer-info">
                <text class="footer-label">已选择：</text>
                <text class="footer-text">{{ selectedSchedule.dateText }} {{ selectedSlot.period === 'morning' ? '上午' : '下午' }}</text>
            </view>
            <button class="btn-register" @click="goToConfirm">
                立即挂号 ¥{{ doctor.consultationFee || doctor.fee }}
            </button>
        </view>
    </view>
</template>

<script>
import { getDoctorDetail } from '../../api/doctor.js'
import { getDoctorSchedules } from '../../api/schedule.js'

export default {
    data() {
        return {
            loading: false,
            doctorId: '',
            departmentName: '',
            doctor: {},
            schedules: [],
            selectedDate: '',
            selectedSchedule: null,
            selectedSlot: {
                period: '',
                scheduleId: null
            }
        }
    },
    onLoad(options) {
        this.doctorId = options.doctorId
        this.departmentName = options.departmentName || ''
        this.loadDoctorDetail()
        this.loadSchedules()
    },
    methods: {
        async loadDoctorDetail() {
            this.loading = true
            try {
                const res = await getDoctorDetail(this.doctorId)
                if (res.code === 200) {
                    this.doctor = res.data || {}
                    if (!this.departmentName && this.doctor.departmentName) {
                        this.departmentName = this.doctor.departmentName
                    }
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

        async loadSchedules() {
            try {
                const today = new Date()
                const endDate = new Date(today)
                endDate.setDate(today.getDate() + 6)
                const startDateStr = today.toISOString().split('T')[0]
                const endDateStr = endDate.toISOString().split('T')[0]
                
                const res = await getDoctorSchedules(this.doctorId, startDateStr, endDateStr)
                if (res.code === 200) {
                    this.schedules = this.processSchedules(res.data || [])
                    if (this.schedules.length > 0) {
                        this.selectDate(this.schedules[0])
                    }
                }
            } catch (e) {
                console.error(e)
            }
        },

        processSchedules(data) {
            const today = new Date()
            const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
            const schedules = []
            
            for (let i = 0; i < 7; i++) {
                const date = new Date(today)
                date.setDate(today.getDate() + i)
                const dateStr = date.toISOString().split('T')[0]
                const dayData = data.find(d => d.date === dateStr) || {}
                
                schedules.push({
                    date: dateStr,
                    dateText: `${date.getMonth() + 1}月${date.getDate()}日`,
                    week: i === 0 ? '今天' : i === 1 ? '明天' : weekDays[date.getDay()],
                    day: (date.getMonth() + 1) + '/' + date.getDate(),
                    morning: dayData.morning !== false,
                    morningRemaining: dayData.morningRemaining !== undefined ? dayData.morningRemaining : Math.floor(Math.random() * 15),
                    morningScheduleId: dayData.morningScheduleId,
                    afternoon: dayData.afternoon !== false,
                    afternoonRemaining: dayData.afternoonRemaining !== undefined ? dayData.afternoonRemaining : Math.floor(Math.random() * 15),
                    afternoonScheduleId: dayData.afternoonScheduleId
                })
            }
            return schedules
        },

        selectDate(schedule) {
            this.selectedDate = schedule.date
            this.selectedSchedule = schedule
            this.selectedSlot = {
                period: '',
                scheduleId: null
            }
        },

        selectSlot(period) {
            if (!this.selectedSchedule) return
            
            const available = period === 'morning' 
                ? this.selectedSchedule.morning && this.selectedSchedule.morningRemaining > 0
                : this.selectedSchedule.afternoon && this.selectedSchedule.afternoonRemaining > 0
            
            if (!available) {
                uni.showToast({
                    title: '该时段号源已约满',
                    icon: 'none'
                })
                return
            }
            
            this.selectedSlot = {
                period: period,
                scheduleId: this.selectedSchedule[period + 'ScheduleId']
            }
        },

        goToConfirm() {
            if (!this.selectedSlot.period) {
                uni.showToast({
                    title: '请选择就诊时段',
                    icon: 'none'
                })
                return
            }
            uni.navigateTo({
                url: '/pages/register/confirm?data=' + encodeURIComponent(JSON.stringify({
                    doctorId: this.doctorId,
                    doctorName: this.doctor.name,
                    departmentId: this.doctor.departmentId,
                    departmentName: this.doctor.departmentName || this.departmentName,
                    scheduleId: this.selectedSlot.scheduleId,
                    period: this.selectedSlot.period,
                    fee: this.doctor.consultationFee || this.doctor.fee,
                    date: this.selectedDate,
                    dateText: this.selectedSchedule.dateText,
                    isToday: this.selectedSchedule.week === '今天'
                }))
            })
        }
    }
}
</script>

<style scoped>
.doctor-detail-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 140rpx;
    box-sizing: border-box;
}

.header {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    padding: 48rpx 32rpx 32rpx;
    color: #fff;
}

.doctor-info-card {
    display: flex;
    align-items: center;
}

.doctor-avatar {
    width: 140rpx;
    height: 140rpx;
    border-radius: 70rpx;
    background: #fff;
    margin-right: 24rpx;
    border: 4rpx solid rgba(255, 255, 255, 0.3);
}

.doctor-info {
    flex: 1;
}

.doctor-name-row {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.doctor-name {
    font-size: 36rpx;
    font-weight: bold;
    margin-right: 16rpx;
}

.doctor-title {
    font-size: 24rpx;
    background: rgba(255, 255, 255, 0.2);
    padding: 6rpx 16rpx;
    border-radius: 12rpx;
}

.doctor-dept {
    font-size: 26rpx;
    opacity: 0.9;
    margin-bottom: 12rpx;
}

.doctor-stats {
    display: flex;
    gap: 24rpx;
}

.stat-item {
    font-size: 22rpx;
    opacity: 0.8;
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
    margin-bottom: 16rpx;
    display: flex;
    align-items: center;
}

.title-icon {
    margin-right: 8rpx;
    font-size: 28rpx;
}

.section-content {
    line-height: 1.8;
}

.content-text {
    font-size: 26rpx;
    color: #666;
}

.date-scroll {
    white-space: nowrap;
    margin: 16rpx -24rpx;
    padding: 0 24rpx;
}

.date-item {
    display: inline-flex;
    flex-direction: column;
    align-items: center;
    padding: 16rpx 20rpx;
    margin-right: 12rpx;
    border-radius: 12rpx;
    background: #f5f5f5;
    min-width: 120rpx;
}

.date-item.active {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
}

.date-week {
    font-size: 22rpx;
    color: #333;
    margin-bottom: 4rpx;
}

.date-item.active .date-week {
    color: #fff;
}

.date-day {
    font-size: 26rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 8rpx;
}

.date-item.active .date-day {
    color: #fff;
}

.date-slots {
    display: flex;
    gap: 4rpx;
}

.slot-badge {
    font-size: 18rpx;
    padding: 2rpx 8rpx;
    border-radius: 6rpx;
}

.slot-badge.morning {
    background: #e6f7ff;
    color: #1989fa;
}

.slot-badge.afternoon {
    background: #f0fff4;
    color: #52c41a;
}

.slot-badge.disabled {
    background: #f5f5f5;
    color: #ccc;
}

.date-item.active .slot-badge.morning,
.date-item.active .slot-badge.afternoon {
    background: rgba(255, 255, 255, 0.2);
    color: #fff;
}

.date-item.active .slot-badge.disabled {
    background: rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.5);
}

.time-slots {
    margin-top: 24rpx;
}

.slot-header {
    font-size: 26rpx;
    color: #666;
    margin-bottom: 16rpx;
}

.slot-list {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
}

.slot-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx;
    border: 2rpx solid #e8e8e8;
    border-radius: 12rpx;
    transition: all 0.2s;
}

.slot-card.selected {
    border-color: #1989fa;
    background: #e6f7ff;
}

.slot-card.disabled {
    opacity: 0.5;
}

.slot-time-info {
    display: flex;
    flex-direction: column;
}

.slot-period {
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 4rpx;
}

.slot-time-range {
    font-size: 22rpx;
    color: #999;
}

.slot-right {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
}

.slot-fee {
    font-size: 28rpx;
    color: #ff6b6b;
    font-weight: bold;
    margin-bottom: 4rpx;
}

.slot-remaining {
    font-size: 22rpx;
    color: #52c41a;
}

.slot-remaining.full {
    color: #ff4d4f;
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

.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: #fff;
    padding: 20rpx 24rpx;
    box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.08);
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.footer-info {
    flex: 1;
}

.footer-label {
    font-size: 24rpx;
    color: #666;
}

.footer-text {
    font-size: 26rpx;
    color: #333;
    font-weight: 500;
}

.btn-register {
    width: 280rpx;
    height: 72rpx;
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    border-radius: 36rpx;
    font-size: 26rpx;
    border: none;
    line-height: 72rpx;
}
</style>
