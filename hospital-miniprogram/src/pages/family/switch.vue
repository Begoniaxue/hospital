<template>
    <view class="switch-container">
        <view class="header-tip">
            <text>切换就诊人后，全页面数据将自动更新为所选就诊人信息</text>
        </view>

        <view class="patient-list">
            <view
                class="patient-item"
                v-for="item in familyList"
                :key="item.id"
                :class="{ 'item-disabled': item.status === 0 }"
                @click="handleSwitch(item)"
            >
                <view class="item-left">
                    <view class="avatar" :class="{ 'avatar-female': item.gender === 0 }">
                        {{ item.name.substring(0, 1) }}
                    </view>
                    <view class="info">
                        <view class="name-row">
                            <text class="name">{{ item.name }}</text>
                            <text class="tag" v-if="item.relation === '本人'">本人</text>
                            <text class="tag tag-other" v-else>{{ item.relation }}</text>
                            <text class="status-tag" v-if="item.status === 0">已禁用</text>
                        </view>
                        <view class="id-card">身份证: {{ maskIdCard(item.idCard) }}</view>
                    </view>
                </view>
                <view class="item-right">
                    <radio :checked="isCurrentPatient(item)" :disabled="item.status === 0 || !item.patientId" color="#1989fa" />
                </view>
            </view>
        </view>

        <button class="btn-add" @click="handleAdd">
            <text class="add-icon">+</text>
            添加新就诊人
        </button>
    </view>
</template>

<script>
import { getFamilyList, switchCurrentPatient } from '../../api/family.js'
import { maskIdCard } from '../../utils/index.js'

export default {
    data() {
        return {
            familyList: [],
            loading: false
        }
    },
    computed: {
        wechatUserId() {
            return this.$store.state.wechatUser?.id || uni.getStorageSync('wechatUser')?.id
        },
        currentPatientId() {
            return this.$store.state.wechatUser?.currentPatientId || uni.getStorageSync('wechatUser')?.currentPatientId
        }
    },
    onShow() {
        this.loadFamilyList()
    },
    methods: {
        maskIdCard,

        isCurrentPatient(item) {
            return item.patientId && item.patientId === this.currentPatientId
        },

        async loadFamilyList() {
            if (!this.wechatUserId) return

            try {
                const res = await getFamilyList(this.wechatUserId)
                if (res.code === 200) {
                    this.familyList = res.data
                }
            } catch (e) {
                console.error('加载就诊人列表失败', e)
            }
        },

        async handleSwitch(item) {
            if (item.status === 0) {
                uni.showToast({
                    title: '该就诊人已被禁用',
                    icon: 'none'
                })
                return
            }

            if (!item.patientId) {
                uni.showToast({
                    title: '该就诊人暂无就诊档案',
                    icon: 'none'
                })
                return
            }

            if (this.isCurrentPatient(item)) {
                return
            }

            this.loading = true
            uni.showLoading({ title: '切换中...' })

            try {
                const res = await switchCurrentPatient(this.wechatUserId, item.patientId)
                if (res.code === 200) {
                    this.$store.commit('UPDATE_CURRENT_PATIENT_ID', item.patientId)
                    this.$store.commit('SET_CURRENT_PATIENT', res.data)
                    uni.setStorageSync('currentPatient', res.data)

                    const wechatUser = this.$store.state.wechatUser
                    wechatUser.currentPatientId = item.patientId
                    this.$store.commit('SET_WECHAT_USER', wechatUser)
                    uni.setStorageSync('wechatUser', wechatUser)

                    uni.hideLoading()
                    uni.showToast({
                        title: '切换成功',
                        icon: 'success'
                    })

                    setTimeout(() => {
                        uni.navigateBack()
                    }, 1000)
                }
            } catch (e) {
                uni.hideLoading()
                console.error('切换就诊人失败', e)
            } finally {
                this.loading = false
            }
        },

        handleAdd() {
            if (this.familyList.length >= 5) {
                uni.showToast({
                    title: '最多只能绑定5位家庭成员',
                    icon: 'none'
                })
                return
            }
            uni.navigateTo({
                url: '/pages/family/add'
            })
        }
    }
}
</script>

<style scoped>
.switch-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 24rpx;
    padding-bottom: 160rpx;
    box-sizing: border-box;
}

.header-tip {
    background: #e6f7ff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 24rpx;
    font-size: 24rpx;
    color: #1890ff;
    line-height: 1.5;
}

.patient-list {
    background: #fff;
    border-radius: 16rpx;
    overflow: hidden;
}

.patient-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 32rpx;
    border-bottom: 1rpx solid #f0f0f0;
}

.patient-item:last-child {
    border-bottom: none;
}

.patient-item.item-disabled {
    opacity: 0.5;
}

.item-left {
    display: flex;
    align-items: center;
    flex: 1;
    min-width: 0;
}

.avatar {
    width: 88rpx;
    height: 88rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, #1989fa, #007aff);
    color: #fff;
    font-size: 36rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 24rpx;
    flex-shrink: 0;
}

.avatar-female {
    background: linear-gradient(135deg, #ff6b9d, #c44569);
}

.info {
    flex: 1;
    min-width: 0;
}

.name-row {
    display: flex;
    align-items: center;
    margin-bottom: 12rpx;
    flex-wrap: wrap;
    gap: 8rpx;
}

.name {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-right: 12rpx;
}

.tag {
    font-size: 20rpx;
    color: #1989fa;
    background: rgba(25, 137, 250, 0.1);
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    margin-right: 8rpx;
}

.tag-other {
    color: #ff976a;
    background: rgba(255, 151, 106, 0.1);
}

.status-tag {
    font-size: 20rpx;
    color: #999;
    background: #f0f0f0;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.id-card {
    font-size: 24rpx;
    color: #999;
}

.item-right {
    flex-shrink: 0;
    margin-left: 16rpx;
}

.btn-add {
    position: fixed;
    left: 32rpx;
    right: 32rpx;
    bottom: 60rpx;
    height: 96rpx;
    background: #fff;
    color: #1989fa;
    border: 2rpx solid #1989fa;
    border-radius: 48rpx;
    font-size: 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn-add:active {
    background: rgba(25, 137, 250, 0.05);
}

.add-icon {
    font-size: 40rpx;
    margin-right: 8rpx;
    font-weight: bold;
}
</style>
