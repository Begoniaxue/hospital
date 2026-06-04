<template>
    <view class="today-container">
        <view class="header">
            <view class="header-title">当日挂号</view>
            <view class="header-subtitle">{{ currentDate }} 今日可挂号</view>
        </view>

        <view class="department-filter">
            <scroll-view scroll-x class="filter-scroll">
                <view 
                    class="filter-item" 
                    :class="{ active: selectedDeptId === 'all' }"
                    @click="selectDept('all')"
                >
                    全部
                </view>
                <view 
                    class="filter-item" 
                    v-for="dept in departments" 
                    :key="dept.id"
                    :class="{ active: selectedDeptId === dept.id }"
                    @click="selectDept(dept.id)"
                >
                    {{ dept.name }}
                </view>
            </scroll-view>
        </view>

        <view class="content" v-if="!loading">
            <view class="doctor-list">
                <view 
                    class="doctor-card" 
                    v-for="doctor in filteredDoctors" 
                    :key="doctor.id"
                >
                    <view class="doctor-info">
                        <image class="doctor-avatar" :src="doctor.avatar || '/static/default-avatar.png'" mode="aspectFill" />
                        <view class="doctor-detail">
                            <view class="doctor-name-row">
                                <text class="doctor-name">{{ doctor.name }}</text>
                                <text class="doctor-title">{{ doctor.title }}</text>
                            </view>
                            <view class="doctor-dept">{{ doctor.departmentName }}</view>
                            <view class="doctor-skill" v-if="doctor.skill">{{ doctor.skill }}</view>
                        </view>
                    </view>
                    <view class="schedule-info">
                        <view class="time-slots">
                            <view 
                                class="slot-item" 
                                :class="{ 
                                    morning: true, 
                                    disabled: doctor.morningRemaining <= 0,
                                    selected: selectedSlot.doctorId === doctor.id && selectedSlot.period === 'morning'
                                }"
                                @click="selectSlot(doctor, 'morning')"
                            >
                                <text class="slot-label">上午</text>
                                <text class="slot-time">8:00-12:00</text>
                                <text class="slot-remaining" :class="{ full: doctor.morningRemaining <= 0 }">
                                    {{ doctor.morningRemaining <= 0 ? '约满' : '剩余' + doctor.morningRemaining + '号' }}
                                </text>
                                <text class="slot-price">¥{{ doctor.consultationFee || doctor.fee || '0' }}</text>
                            </view>
                            <view 
                                class="slot-item" 
                                :class="{ 
                                    afternoon: true, 
                                    disabled: doctor.afternoonRemaining <= 0,
                                    selected: selectedSlot.doctorId === doctor.id && selectedSlot.period === 'afternoon'
                                }"
                                @click="selectSlot(doctor, 'afternoon')"
                            >
                                <text class="slot-label">下午</text>
                                <text class="slot-time">14:00-17:30</text>
                                <text class="slot-remaining" :class="{ full: doctor.afternoonRemaining <= 0 }">
                                    {{ doctor.afternoonRemaining <= 0 ? '约满' : '剩余' + doctor.afternoonRemaining + '号' }}
                                </text>
                                <text class="slot-price">¥{{ doctor.consultationFee || doctor.fee || '0' }}</text>
                            </view>
                        </view>
                    </view>
                </view>
                <view class="empty-box" v-if="filteredDoctors.length === 0">
                    <text class="empty-icon">📭</text>
                    <text class="empty-text">当前科室暂无出诊医生</text>
                </view>
            </view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>

        <view class="footer" v-if="selectedSlot.doctorId">
            <view class="selected-info">
                <text class="selected-label">已选择：</text>
                <text class="selected-text">{{ selectedSlot.doctorName }} - {{ selectedSlot.period === 'morning' ? '上午' : '下午' }}</text>
            </view>
            <button class="btn-register" @click="goToConfirm">
                立即挂号 ¥{{ selectedSlot.fee || '0' }}
            </button>
        </view>
    </view>
</template>

<script>
import { getTodaySchedules } from '../../api/schedule.js'

export default {
    data() {
        return {
            loading: false,
            currentDate: '',
            departments: [],
            doctors: [],
            selectedDeptId: 'all',
            selectedSlot: {
                doctorId: null,
                doctorName: '',
                period: '',
                fee: 0,
                scheduleId: null
            }
        }
    },
    onLoad(options) {
        this.currentDate = this.formatDate(new Date())
        if (options.departmentId) {
            this.selectedDeptId = options.departmentId
        }
        this.loadTodaySchedules()
    },
    computed: {
        filteredDoctors() {
            if (this.selectedDeptId === 'all') {
                return this.doctors
            }
            return this.doctors.filter(d => d.departmentId === this.selectedDeptId)
        }
    },
    methods: {
        formatDate(date) {
            const year = date.getFullYear()
            const month = String(date.getMonth() + 1).padStart(2, '0')
            const day = String(date.getDate()).padStart(2, '0')
            const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
            return `${year}年${month}月${day}日 ${weekDays[date.getDay()]}`
        },

        async loadTodaySchedules() {
            this.loading = true
            try {
                const res = await getTodaySchedules()
                if (res.code === 200) {
                    const data = res.data || {}
                    this.departments = data.departments || []
                    this.doctors = data.doctors || []
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

        selectDept(id) {
            this.selectedDeptId = id
        },

        selectSlot(doctor, period) {
            const remaining = period === 'morning' ? doctor.morningRemaining : doctor.afternoonRemaining
            if (remaining <= 0) {
                uni.showToast({
                    title: '该时段号源已约满',
                    icon: 'none'
                })
                return
            }
            this.selectedSlot = {
                doctorId: doctor.id,
                doctorName: doctor.name,
                departmentId: doctor.departmentId,
                departmentName: doctor.departmentName,
                period: period,
                fee: doctor.consultationFee || doctor.fee,
                scheduleId: doctor[period + 'ScheduleId']
            }
        },

        goToConfirm() {
            if (!this.selectedSlot.doctorId) {
                uni.showToast({
                    title: '请选择就诊时段',
                    icon: 'none'
                })
                return
            }
            uni.navigateTo({
                url: '/pages/register/confirm?data=' + encodeURIComponent(JSON.stringify({
                    doctorId: this.selectedSlot.doctorId,
                    doctorName: this.selectedSlot.doctorName,
                    departmentId: this.selectedSlot.departmentId,
                    departmentName: this.selectedSlot.departmentName,
                    scheduleId: this.selectedSlot.scheduleId,
                    period: this.selectedSlot.period,
                    fee: this.selectedSlot.fee,
                    date: new Date().toISOString().split('T')[0],
                    dateText: this.formatDate(new Date()),
                    isToday: true
                }))
            })
        }
    }
}
</script>

<style scoped>
.today-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding-bottom: 140rpx;
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

.department-filter {
    background: #fff;
    padding: 20rpx 0;
    position: sticky;
    top: 0;
    z-index: 10;
}

.filter-scroll {
    white-space: nowrap;
    padding: 0 24rpx;
}

.filter-item {
    display: inline-block;
    padding: 12rpx 28rpx;
    background: #f5f5f5;
    border-radius: 32rpx;
    font-size: 26rpx;
    color: #666;
    margin-right: 16rpx;
}

.filter-item.active {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
}

.content {
    padding: 24rpx;
}

.doctor-list {
    margin-top: 16rpx;
}

.doctor-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;
}

.doctor-info {
    display: flex;
    margin-bottom: 20rpx;
}

.doctor-avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 60rpx;
    background: #f5f5f5;
    margin-right: 20rpx;
}

.doctor-detail {
    flex: 1;
}

.doctor-name-row {
    display: flex;
    align-items: center;
    margin-bottom: 8rpx;
}

.doctor-name {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-right: 12rpx;
}

.doctor-title {
    font-size: 24rpx;
    color: #1989fa;
    background: #e6f7ff;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.doctor-dept {
    font-size: 24rpx;
    color: #666;
    margin-bottom: 8rpx;
}

.doctor-skill {
    font-size: 22rpx;
    color: #999;
    line-height: 1.5;
}

.time-slots {
    display: flex;
    gap: 16rpx;
}

.slot-item {
    flex: 1;
    padding: 16rpx;
    border: 2rpx solid #e8e8e8;
    border-radius: 12rpx;
    text-align: center;
    transition: all 0.2s;
}

.slot-item.selected {
    border-color: #1989fa;
    background: #e6f7ff;
}

.slot-item.disabled {
    opacity: 0.5;
}

.slot-label {
    display: block;
    font-size: 26rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 4rpx;
}

.slot-time {
    display: block;
    font-size: 20rpx;
    color: #999;
    margin-bottom: 8rpx;
}

.slot-remaining {
    display: block;
    font-size: 22rpx;
    color: #52c41a;
    margin-bottom: 4rpx;
}

.slot-remaining.full {
    color: #ff4d4f;
}

.slot-price {
    display: block;
    font-size: 26rpx;
    color: #ff6b6b;
    font-weight: bold;
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

.empty-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80rpx 0;
}

.empty-icon {
    font-size: 100rpx;
    margin-bottom: 20rpx;
}

.empty-text {
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

.selected-info {
    flex: 1;
}

.selected-label {
    font-size: 24rpx;
    color: #666;
}

.selected-text {
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
