<template>
    <view class="family-container">
        <view class="header-tip">
            <text class="tip-icon">ℹ️</text>
            <text>最多可绑定5位家庭成员，用于代挂号、代缴费、代查报告</text>
        </view>

        <view class="family-list" v-if="familyList.length > 0">
            <view
                class="family-item"
                v-for="item in familyList"
                :key="item.id"
                @click="handleItemClick(item)"
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
                            <text class="status-tag status-disabled" v-if="item.status === 0">已禁用</text>
                        </view>
                        <view class="id-card">身份证: {{ maskIdCard(item.idCard) }}</view>
                        <view class="phone" v-if="item.phone">手机号: {{ maskPhone(item.phone) }}</view>
                    </view>
                </view>
                <view class="item-right">
                    <view class="current-tag" v-if="isCurrentPatient(item)">
                        当前就诊人
                    </view>
                    <view class="arrow">›</view>
                </view>
            </view>
        </view>

        <view class="empty" v-else>
            <text class="empty-icon">👨‍👩‍👧‍👦</text>
            <text class="empty-text">暂无就诊人</text>
            <text class="empty-tip">添加就诊人后可代挂号、代缴费</text>
        </view>

        <button class="btn-add" @click="handleAdd">
            <text class="add-icon">+</text>
            添加就诊人
        </button>
    </view>
</template>

<script>
import { getFamilyList, deleteFamilyMember, disableFamilyMember, switchCurrentPatient } from '../../api/family.js'
import { maskIdCard, maskPhone } from '../../utils/index.js'

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
        maskPhone,

        isCurrentPatient(item) {
            return item.patientId && item.patientId === this.currentPatientId
        },

        async loadFamilyList() {
            if (!this.wechatUserId) return

            this.loading = true
            try {
                const res = await getFamilyList(this.wechatUserId)
                if (res.code === 200) {
                    this.familyList = res.data
                    this.$store.commit('SET_FAMILY_LIST', res.data)
                }
            } catch (e) {
                console.error('加载就诊人列表失败', e)
            } finally {
                this.loading = false
            }
        },

        handleItemClick(item) {
            uni.showActionSheet({
                itemList: [
                    this.isCurrentPatient(item) ? '✓ 当前就诊人' : '切换为当前就诊人',
                    '查看就诊码',
                    '编辑',
                    item.hasVisitRecord === 1 ? '禁用' : '删除'
                ],
                success: async (res) => {
                    switch (res.tapIndex) {
                        case 0:
                            if (!this.isCurrentPatient(item) && item.patientId) {
                                this.handleSwitch(item)
                            }
                            break
                        case 1:
                            this.showQrCode(item)
                            break
                        case 2:
                            this.handleEdit(item)
                            break
                        case 3:
                            if (item.hasVisitRecord === 1) {
                                this.handleDisable(item)
                            } else {
                                this.handleDelete(item)
                            }
                            break
                    }
                }
            })
        },

        async handleSwitch(item) {
            if (!item.patientId) {
                uni.showToast({
                    title: '该就诊人暂无就诊档案',
                    icon: 'none'
                })
                return
            }

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

                    uni.showToast({
                        title: '已切换为当前就诊人',
                        icon: 'success'
                    })
                }
            } catch (e) {
                console.error('切换就诊人失败', e)
            }
        },

        showQrCode(item) {
            uni.navigateTo({
                url: '/pages/family/qrcode?familyId=' + item.id + '&patientId=' + item.patientId + '&name=' + item.name
            })
        },

        handleEdit(item) {
            uni.navigateTo({
                url: '/pages/family/edit?id=' + item.id
            })
        },

        async handleDisable(item) {
            uni.showModal({
                title: '提示',
                content: '该就诊人已有就诊记录，不可删除，是否禁用？',
                success: async (res) => {
                    if (res.confirm) {
                        try {
                            const result = await disableFamilyMember(item.id)
                            if (result.code === 200) {
                                uni.showToast({
                                    title: '已禁用',
                                    icon: 'success'
                                })
                                this.loadFamilyList()
                            }
                        } catch (e) {
                            console.error('禁用失败', e)
                        }
                    }
                }
            })
        },

        async handleDelete(item) {
            uni.showModal({
                title: '确认删除',
                content: '确定要删除该就诊人吗？',
                success: async (res) => {
                    if (res.confirm) {
                        try {
                            const result = await deleteFamilyMember(item.id)
                            if (result.code === 200) {
                                uni.showToast({
                                    title: '删除成功',
                                    icon: 'success'
                                })
                                this.loadFamilyList()
                            }
                        } catch (e) {
                            uni.showToast({
                                title: e.message || '删除失败',
                                icon: 'none'
                            })
                        }
                    }
                }
            })
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
.family-container {
    min-height: 100vh;
    background: #f5f5f5;
    padding: 24rpx;
    padding-bottom: 160rpx;
    box-sizing: border-box;
}

.header-tip {
    display: flex;
    align-items: flex-start;
    background: #e6f7ff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 24rpx;
    font-size: 24rpx;
    color: #1890ff;
    line-height: 1.5;
}

.tip-icon {
    margin-right: 12rpx;
    flex-shrink: 0;
}

.family-list {
    background: #fff;
    border-radius: 16rpx;
    overflow: hidden;
}

.family-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 32rpx;
    border-bottom: 1rpx solid #f0f0f0;
}

.family-item:last-child {
    border-bottom: none;
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
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.status-disabled {
    color: #999;
    background: #f0f0f0;
}

.id-card,
.phone {
    font-size: 24rpx;
    color: #999;
    margin-top: 8rpx;
}

.item-right {
    display: flex;
    align-items: center;
    flex-shrink: 0;
}

.current-tag {
    font-size: 24rpx;
    color: #07c160;
    background: rgba(7, 193, 96, 0.1);
    padding: 8rpx 16rpx;
    border-radius: 8rpx;
    margin-right: 16rpx;
}

.arrow {
    font-size: 40rpx;
    color: #ccc;
}

.empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
    background: #fff;
    border-radius: 16rpx;
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
}

.btn-add {
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

.btn-add:active {
    opacity: 0.85;
}

.add-icon {
    font-size: 40rpx;
    margin-right: 8rpx;
    font-weight: bold;
}
</style>
