<template>
    <view class="message-container">
        <view class="tab-bar">
            <view
                class="tab-item"
                :class="{ active: currentTab === 'all' }"
                @click="switchTab('all')"
            >
                全部
            </view>
            <view
                class="tab-item"
                :class="{ active: currentTab === 'system' }"
                @click="switchTab('system')"
            >
                系统通知
                <text class="badge" v-if="unreadCount.system > 0">{{ unreadCount.system }}</text>
            </view>
            <view
                class="tab-item"
                :class="{ active: currentTab === 'visit' }"
                @click="switchTab('visit')"
            >
                就医提醒
                <text class="badge" v-if="unreadCount.visit > 0">{{ unreadCount.visit }}</text>
            </view>
            <view
                class="tab-item"
                :class="{ active: currentTab === 'health' }"
                @click="switchTab('health')"
            >
                健康资讯
            </view>
        </view>

        <view class="message-list" v-if="filteredMessages.length > 0">
            <view
                class="message-item"
                v-for="item in filteredMessages"
                :key="item.id"
                :class="{ unread: !item.read }"
                @click="handleMessageClick(item)"
            >
                <view class="item-left">
                    <view class="icon" :class="item.type">{{ item.icon }}</view>
                </view>
                <view class="item-center">
                    <view class="title-row">
                        <text class="title">{{ item.title }}</text>
                        <text class="time">{{ item.time }}</text>
                    </view>
                    <text class="content">{{ item.content }}</text>
                    <view class="tag-row" v-if="item.tags">
                        <text class="tag" v-for="(tag, idx) in item.tags" :key="idx">{{ tag }}</text>
                    </view>
                </view>
                <view class="item-right">
                    <view class="unread-dot" v-if="!item.read"></view>
                    <text class="arrow">›</text>
                </view>
            </view>
        </view>

        <view class="empty" v-else>
            <text class="empty-icon">🔔</text>
            <text class="empty-text">暂无{{ currentTabLabel }}消息</text>
            <text class="empty-tip">您将在第一时间收到就医提醒和重要通知</text>
        </view>

        <view class="clear-bar" v-if="filteredMessages.length > 0">
            <text class="clear-btn" @click="handleClearRead">清除已读消息</text>
            <text class="clear-btn" @click="handleMarkAllRead">全部标为已读</text>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            currentTab: 'all',
            unreadCount: {
                system: 2,
                visit: 3,
                health: 0
            },
            messages: [
                {
                    id: 1,
                    type: 'visit',
                    icon: '📅',
                    title: '预约成功通知',
                    content: '您已成功预约2024-06-15 上午09:00-09:30 内科门诊 张主任医师号源',
                    time: '10:30',
                    read: false,
                    tags: ['预约挂号']
                },
                {
                    id: 2,
                    type: 'visit',
                    icon: '✅',
                    title: '候诊提醒',
                    content: '您的号源已开始叫号，当前叫号：A0012，您的号码：A0015，前方等待2人',
                    time: '09:05',
                    read: false,
                    tags: ['叫号提醒']
                },
                {
                    id: 3,
                    type: 'visit',
                    icon: '💊',
                    title: '处方开具通知',
                    content: '医生已为您开具处方，请前往门诊药房或在线缴费后取药',
                    time: '昨天',
                    read: false,
                    tags: ['处方通知']
                },
                {
                    id: 4,
                    type: 'system',
                    icon: '📋',
                    title: '检验报告已出',
                    content: '您的血常规检验报告已出具，点击查看详细结果',
                    time: '昨天',
                    read: false,
                    tags: ['报告通知']
                },
                {
                    id: 5,
                    type: 'system',
                    icon: '💰',
                    title: '缴费成功通知',
                    content: '您已成功缴纳门诊费用 ¥156.00，可直接前往药房取药',
                    time: '2天前',
                    read: true,
                    tags: ['缴费通知']
                },
                {
                    id: 6,
                    type: 'health',
                    icon: '🩺',
                    title: '高血压患者夏季注意事项',
                    content: '夏季血压波动大，高血压患者需注意监测血压、调整用药、清淡饮食...',
                    time: '3天前',
                    read: true,
                    tags: ['健康科普']
                },
                {
                    id: 7,
                    type: 'system',
                    icon: '🔔',
                    title: '系统维护通知',
                    content: '系统将于2024-06-20 23:00-次日02:00进行维护，期间部分功能可能无法使用',
                    time: '5天前',
                    read: true,
                    tags: ['系统通知']
                }
            ]
        }
    },
    computed: {
        filteredMessages() {
            if (this.currentTab === 'all') {
                return this.messages
            }
            return this.messages.filter(m => m.type === this.currentTab)
        },
        currentTabLabel() {
            const labels = {
                'all': '',
                'system': '系统通知',
                'visit': '就医提醒',
                'health': '健康资讯'
            }
            return labels[this.currentTab] || ''
        }
    },
    methods: {
        switchTab(tab) {
            this.currentTab = tab
        },

        handleMessageClick(item) {
            if (!item.read) {
                item.read = true
                if (item.type === 'system') {
                    this.unreadCount.system = Math.max(0, this.unreadCount.system - 1)
                } else if (item.type === 'visit') {
                    this.unreadCount.visit = Math.max(0, this.unreadCount.visit - 1)
                }
            }

            if (item.type === 'visit') {
                uni.showToast({
                    title: '就医详情开发中',
                    icon: 'none'
                })
            } else if (item.type === 'system' && item.tags?.includes('报告通知')) {
                uni.showToast({
                    title: '报告查询开发中',
                    icon: 'none'
                })
            } else {
                uni.showModal({
                    title: item.title,
                    content: item.content,
                    showCancel: false,
                    confirmText: '知道了'
                })
            }
        },

        handleClearRead() {
            uni.showModal({
                title: '确认清除',
                content: '确定要清除所有已读消息吗？',
                success: (res) => {
                    if (res.confirm) {
                        this.messages = this.messages.filter(m => !m.read)
                        uni.showToast({
                            title: '已清除',
                            icon: 'success'
                        })
                    }
                }
            })
        },

        handleMarkAllRead() {
            this.messages.forEach(m => {
                m.read = true
            })
            this.unreadCount.system = 0
            this.unreadCount.visit = 0
            this.unreadCount.health = 0
            uni.showToast({
                title: '已全部标为已读',
                icon: 'success'
            })
        }
    }
}
</script>

<style scoped>
.message-container {
    min-height: 100vh;
    background: #f5f5f5;
}

.tab-bar {
    display: flex;
    background: #fff;
    position: sticky;
    top: 0;
    z-index: 10;
    border-bottom: 1rpx solid #f0f0f0;
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 28rpx 0;
    font-size: 28rpx;
    color: #666;
    position: relative;
}

.tab-item.active {
    color: #1989fa;
    font-weight: bold;
}

.badge {
    position: absolute;
    top: 16rpx;
    right: 20rpx;
    min-width: 32rpx;
    height: 32rpx;
    line-height: 32rpx;
    background: #ee0a24;
    color: #fff;
    font-size: 20rpx;
    border-radius: 16rpx;
    padding: 0 6rpx;
    font-weight: normal;
}

.message-list {
    padding: 24rpx;
}

.message-item {
    display: flex;
    align-items: center;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
}

.message-item.unread {
    background: #fff;
    border-left: 6rpx solid #1989fa;
}

.item-left {
    margin-right: 20rpx;
    flex-shrink: 0;
}

.icon {
    width: 80rpx;
    height: 80rpx;
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 36rpx;
    background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
}

.icon.visit {
    background: linear-gradient(135deg, #f0f9ff 0%, #cde9ff 100%);
}

.icon.system {
    background: linear-gradient(135deg, #fff7e6 0%, #ffe7ba 100%);
}

.icon.health {
    background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
}

.item-center {
    flex: 1;
    min-width: 0;
}

.title-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8rpx;
}

.title {
    font-size: 30rpx;
    font-weight: 500;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 350rpx;
}

.time {
    font-size: 22rpx;
    color: #999;
    flex-shrink: 0;
}

.content {
    font-size: 26rpx;
    color: #666;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.tag-row {
    display: flex;
    gap: 8rpx;
    margin-top: 12rpx;
}

.tag {
    font-size: 20rpx;
    color: #1989fa;
    background: rgba(25, 137, 250, 0.1);
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
}

.item-right {
    display: flex;
    align-items: center;
    flex-shrink: 0;
    margin-left: 16rpx;
}

.unread-dot {
    width: 16rpx;
    height: 16rpx;
    border-radius: 50%;
    background: #ee0a24;
    margin-right: 12rpx;
}

.arrow {
    font-size: 36rpx;
    color: #ccc;
}

.empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 48rpx;
}

.empty-icon {
    font-size: 120rpx;
    margin-bottom: 24rpx;
    opacity: 0.3;
}

.empty-text {
    font-size: 32rpx;
    color: #333;
    margin-bottom: 12rpx;
}

.empty-tip {
    font-size: 26rpx;
    color: #999;
    text-align: center;
    line-height: 1.5;
}

.clear-bar {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    background: #fff;
    border-top: 1rpx solid #f0f0f0;
    padding: 20rpx 48rpx;
    padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
    justify-content: space-between;
}

.clear-btn {
    font-size: 26rpx;
    color: #666;
}
</style>
