<template>
    <view class="qrcode-container">
        <view class="qrcode-card">
            <view class="patient-info">
                <view class="avatar" :class="{ 'avatar-female': patientGender === 0 }">
                    {{ patientName.substring(0, 1) }}
                </view>
                <view class="info">
                    <view class="name">{{ patientName }}</view>
                    <view class="patient-no">就诊人</view>
                </view>
            </view>

            <view class="qrcode-area">
                <view class="qrcode-border">
                    <canvas canvas-id="qrcodeCanvas" id="qrcodeCanvas" class="qrcode-canvas"></canvas>
                    <view class="qrcode-placeholder" v-if="!qrCodeUrl">
                        <view class="loading-icon">🔄</view>
                        <text class="loading-text">正在生成就诊码...</text>
                    </view>
                    <image v-else :src="qrCodeUrl" mode="aspectFit" class="qrcode-image" />
                </view>
            </view>

            <view class="code-info" v-if="visitCode">
                <view class="code-content">
                    <text class="label">就诊码:</text>
                    <text class="value">{{ visitCode.codeContent }}</text>
                </view>
                <view class="expire-time">
                    <text class="label">有效期至:</text>
                    <text class="value">{{ formatDate(visitCode.expireTime, 'HH:mm:ss') }}</text>
                    <text class="countdown">({{ countdownText }})</text>
                </view>
            </view>

            <view class="tip">
                <text class="tip-icon">💡</text>
                <text>请出示此二维码用于分诊签到、药房取药、护士核验等院内就诊场景</text>
            </view>
        </view>

        <view class="code-types">
            <view
                class="type-item"
                :class="{ active: currentType === 'PATIENT' }"
                @click="switchCodeType('PATIENT')"
            >
                <text class="icon">🏥</text>
                <text class="text">通用就诊码</text>
            </view>
            <view
                class="type-item"
                :class="{ active: currentType === 'REGISTER' }"
                @click="switchCodeType('REGISTER')"
            >
                <text class="icon">📋</text>
                <text class="text">挂号签到码</text>
            </view>
            <view
                class="type-item"
                :class="{ active: currentType === 'PAYMENT' }"
                @click="switchCodeType('PAYMENT')"
            >
                <text class="icon">💳</text>
                <text class="text">缴费码</text>
            </view>
            <view
                class="type-item"
                :class="{ active: currentType === 'PHARMACY' }"
                @click="switchCodeType('PHARMACY')"
            >
                <text class="icon">💊</text>
                <text class="text">取药码</text>
            </view>
        </view>

        <view class="btn-refresh" @click="refreshCode">
            <text class="refresh-icon">🔄</text>
            刷新二维码
        </view>

        <view class="refresh-tip">
            二维码每30分钟自动刷新，为保护您的隐私，请勿截图分享
        </view>
    </view>
</template>

<script>
import { generateVisitCode, getLatestVisitCode } from '../../api/visit.js'
import { formatDate } from '../../utils/index.js'

export default {
    data() {
        return {
            patientName: '',
            patientGender: 1,
            patientId: null,
            visitCode: null,
            qrCodeUrl: '',
            currentType: 'PATIENT',
            countdownText: '',
            countdownTimer: null
        }
    },
    onLoad(options) {
        this.patientId = options.patientId
        this.patientName = options.name || '就诊人'

        this.loadVisitCode()
        this.startCountdown()
    },
    onUnload() {
        if (this.countdownTimer) {
            clearInterval(this.countdownTimer)
        }
    },
    methods: {
        formatDate,

        async loadVisitCode() {
            if (!this.patientId) return

            try {
                const res = await getLatestVisitCode(this.patientId)
                if (res.code === 200) {
                    this.visitCode = res.data
                    this.generateQRCode(res.data.codeContent)
                    this.updateCountdown()
                }
            } catch (e) {
                console.error('获取就诊码失败', e)
                this.refreshCode()
            }
        },

        async refreshCode() {
            if (!this.patientId) return

            try {
                const useSceneMap = {
                    'PATIENT': '通用就诊',
                    'REGISTER': '挂号签到',
                    'PAYMENT': '缴费',
                    'PHARMACY': '取药'
                }

                const res = await generateVisitCode(this.patientId, this.currentType, useSceneMap[this.currentType])
                if (res.code === 200) {
                    this.visitCode = res.data
                    this.generateQRCode(res.data.codeContent)
                    this.updateCountdown()
                    uni.showToast({
                        title: '已刷新',
                        icon: 'success'
                    })
                }
            } catch (e) {
                console.error('刷新就诊码失败', e)
            }
        },

        async switchCodeType(type) {
            if (this.currentType === type) return
            this.currentType = type
            this.refreshCode()
        },

        generateQRCode(content) {
            const size = 400
            const ctx = uni.createCanvasContext('qrcodeCanvas', this)

            const cellSize = size / 25
            const modules = []
            for (let i = 0; i < 25; i++) {
                modules[i] = []
                for (let j = 0; j < 25; j++) {
                    const hash = this.simpleHash(content + i + j)
                    modules[i][j] = hash % 2 === 0
                }
            }

            for (let i = 0; i < 7; i++) {
                for (let j = 0; j < 7; j++) {
                    const isBorder = i === 0 || i === 6 || j === 0 || j === 6
                    const isInner = i >= 2 && i <= 4 && j >= 2 && j <= 4
                    modules[i][j] = isBorder || isInner
                    modules[24 - i][j] = isBorder || isInner
                    modules[i][24 - j] = isBorder || isInner
                }
            }

            ctx.setFillStyle('#ffffff')
            ctx.fillRect(0, 0, size, size)

            ctx.setFillStyle('#000000')
            for (let i = 0; i < 25; i++) {
                for (let j = 0; j < 25; j++) {
                    if (modules[i][j]) {
                        ctx.fillRect(j * cellSize, i * cellSize, cellSize, cellSize)
                    }
                }
            }

            ctx.draw(false, () => {
                setTimeout(() => {
                    uni.canvasToTempFilePath({
                        canvasId: 'qrcodeCanvas',
                        success: (res) => {
                            this.qrCodeUrl = res.tempFilePath
                        },
                        fail: () => {
                            this.qrCodeUrl = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0id2hpdGUiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZm9udC1zaXplPSIxNCIgZmlsbD0iYmxhY2siIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj7ot6/otoDmnIrov4npgJrnq5kvPC90ZXh0Pjwvc3ZnPg=='
                        }
                    }, this)
                }, 100)
            })
        },

        simpleHash(str) {
            let hash = 0
            for (let i = 0; i < str.length; i++) {
                const char = str.charCodeAt(i)
                hash = ((hash << 5) - hash) + char
                hash = hash & hash
            }
            return Math.abs(hash)
        },

        startCountdown() {
            this.countdownTimer = setInterval(() => {
                this.updateCountdown()
            }, 1000)
        },

        updateCountdown() {
            if (!this.visitCode || !this.visitCode.expireTime) return

            const now = new Date().getTime()
            const expire = new Date(this.visitCode.expireTime).getTime()
            const diff = expire - now

            if (diff <= 0) {
                this.countdownText = '已过期'
                this.refreshCode()
                return
            }

            const minutes = Math.floor(diff / 60000)
            const seconds = Math.floor((diff % 60000) / 1000)
            this.countdownText = `${minutes}分${seconds}秒后过期`
        }
    }
}
</script>

<style scoped>
.qrcode-container {
    min-height: 100vh;
    background: linear-gradient(180deg, #1989fa 0%, #f5f5f5 40%);
    padding: 32rpx;
    padding-bottom: 80rpx;
    box-sizing: border-box;
}

.qrcode-card {
    background: #fff;
    border-radius: 24rpx;
    padding: 48rpx;
    margin-bottom: 32rpx;
    box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.patient-info {
    display: flex;
    align-items: center;
    margin-bottom: 48rpx;
    padding-bottom: 32rpx;
    border-bottom: 1rpx dashed #e0e0e0;
}

.avatar {
    width: 96rpx;
    height: 96rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, #1989fa, #007aff);
    color: #fff;
    font-size: 40rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 24rpx;
}

.avatar-female {
    background: linear-gradient(135deg, #ff6b9d, #c44569);
}

.info {
    flex: 1;
}

.name {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 8rpx;
}

.patient-no {
    font-size: 26rpx;
    color: #999;
}

.qrcode-area {
    display: flex;
    justify-content: center;
    margin-bottom: 48rpx;
}

.qrcode-border {
    width: 400rpx;
    height: 400rpx;
    border: 8rpx solid #1989fa;
    border-radius: 16rpx;
    padding: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
}

.qrcode-canvas {
    width: 368rpx;
    height: 368rpx;
}

.qrcode-image {
    width: 368rpx;
    height: 368rpx;
    position: absolute;
}

.qrcode-placeholder {
    position: absolute;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 368rpx;
    height: 368rpx;
}

.loading-icon {
    font-size: 60rpx;
    animation: spin 1s linear infinite;
    margin-bottom: 16rpx;
}

@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

.loading-text {
    font-size: 24rpx;
    color: #999;
}

.code-info {
    margin-bottom: 32rpx;
}

.code-content,
.expire-time {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 26rpx;
    margin-bottom: 12rpx;
}

.label {
    color: #999;
    margin-right: 8rpx;
}

.value {
    color: #333;
    font-family: monospace;
}

.countdown {
    color: #ff976a;
    margin-left: 8rpx;
}

.tip {
    display: flex;
    align-items: flex-start;
    background: #f6ffed;
    border-radius: 12rpx;
    padding: 20rpx;
    font-size: 24rpx;
    color: #52c41a;
    line-height: 1.5;
}

.tip-icon {
    margin-right: 12rpx;
    flex-shrink: 0;
}

.code-types {
    display: flex;
    justify-content: space-between;
    background: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 32rpx;
}

.type-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16rpx;
    border-radius: 12rpx;
    transition: all 0.3s;
}

.type-item.active {
    background: rgba(25, 137, 250, 0.1);
}

.type-item .icon {
    font-size: 48rpx;
    margin-bottom: 8rpx;
}

.type-item .text {
    font-size: 22rpx;
    color: #666;
}

.type-item.active .text {
    color: #1989fa;
    font-weight: bold;
}

.btn-refresh {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 88rpx;
    background: #fff;
    border: 2rpx solid #1989fa;
    border-radius: 44rpx;
    font-size: 28rpx;
    color: #1989fa;
    margin-bottom: 24rpx;
}

.btn-refresh:active {
    background: rgba(25, 137, 250, 0.05);
}

.refresh-icon {
    margin-right: 8rpx;
}

.refresh-tip {
    text-align: center;
    font-size: 22rpx;
    color: #999;
}
</style>
