<template>
    <view class="privacy-container">
        <view class="header">
            <view class="header-title">隐私协议</view>
            <view class="header-subtitle">您的隐私，我们的承诺</view>
        </view>

        <view class="content" v-if="!loading">
            <scroll-view class="privacy-scroll" scroll-y>
                <view class="privacy-content">
                    <view class="privacy-title">{{ privacyData.title || '用户隐私协议' }}</view>
                    <view class="privacy-update-time" v-if="privacyData.updateTime">
                        更新时间：{{ privacyData.updateTime }}
                    </view>
                    <view class="privacy-body">
                        <text class="privacy-text">{{ privacyData.content || defaultContent }}</text>
                    </view>
                </view>
            </scroll-view>
        </view>

        <view class="loading-box" v-else>
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
import { getPrivacy } from '../../api/profile.js'

export default {
    data() {
        return {
            loading: false,
            privacyData: {
                title: '',
                content: '',
                updateTime: ''
            },
            defaultContent: `智慧医院非常重视用户的隐私保护。本隐私协议旨在帮助您了解我们如何收集、使用、存储和保护您的个人信息。

一、信息收集
我们可能会收集以下类型的信息：
1. 个人基本信息：姓名、身份证号、手机号等，用于实名认证和就诊服务；
2. 健康信息：就诊记录、检查报告、用药信息等，用于提供医疗服务；
3. 设备信息：设备型号、操作系统版本等，用于优化应用体验；
4. 日志信息：访问时间、操作记录等，用于服务安全和故障排查。

二、信息使用
我们收集的信息将用于：
1. 为您提供预约挂号、就诊提醒、报告查询等医疗服务；
2. 验证您的身份，保障账号安全；
3. 改进和优化我们的服务质量；
4. 遵守法律法规的要求。

三、信息保护
我们采用行业标准的安全技术和管理措施来保护您的个人信息，包括但不限于：
1. 数据加密传输和存储；
2. 严格的权限控制和访问审计；
3. 定期的安全检查和更新。

四、信息共享
除以下情形外，我们不会向任何第三方共享您的个人信息：
1. 获得您的明确同意；
2. 法律法规要求或司法机关、行政机关依法要求；
3. 为提供必要服务而与合作伙伴共享（我们会要求合作伙伴遵守保密义务）。

五、您的权利
您有权：
1. 查询、更正您的个人信息；
2. 删除您的个人信息（法律法规另有规定的除外）；
3. 撤回您的同意授权；
4. 注销您的账号。

六、未成年人保护
我们非常重视未成年人的隐私保护。如果您是未成年人，请在监护人的陪同下阅读本协议，并在监护人同意的情况下使用我们的服务。

七、协议更新
我们可能会适时更新本隐私协议。更新后的协议将在本页面公布，重大变更会通过显著方式通知您。

八、联系我们
如果您对本隐私协议有任何疑问、意见或建议，请通过以下方式联系我们：
电话：400-888-8888
邮箱：privacy@hospital.com

感谢您对智慧医院的信任！我们将始终致力于保护您的隐私安全。`
        }
    },
    onLoad() {
        this.loadPrivacy()
    },
    methods: {
        async loadPrivacy() {
            this.loading = true
            try {
                const res = await getPrivacy()
                if (res.code === 200 && res.data) {
                    this.privacyData = {
                        title: res.data.title || '用户隐私协议',
                        content: res.data.content || '',
                        updateTime: res.data.updateTime || ''
                    }
                }
            } catch (e) {
                // 使用默认内容
            } finally {
                this.loading = false
            }
        }
    }
}
</script>

<style scoped>
.privacy-container {
    min-height: 100vh;
    background: #f5f5f5;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
}

.header {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    padding: 48rpx 32rpx;
    color: #fff;
    flex-shrink: 0;
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

.content {
    flex: 1;
    padding: 24rpx;
    margin-top: -16rpx;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.privacy-scroll {
    flex: 1;
    height: 0;
    background: #fff;
    border-radius: 16rpx;
}

.privacy-content {
    padding: 32rpx;
}

.privacy-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    margin-bottom: 16rpx;
}

.privacy-update-time {
    font-size: 24rpx;
    color: #999;
    text-align: center;
    margin-bottom: 32rpx;
}

.privacy-body {
    padding-top: 16rpx;
    border-top: 1rpx solid #f0f0f0;
}

.privacy-text {
    font-size: 26rpx;
    color: #666;
    line-height: 1.8;
    white-space: pre-wrap;
    word-break: break-all;
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
