<script>
export default {
    onLaunch: function() {
        console.log('App Launch')
        const token = uni.getStorageSync('token')
        const wechatUser = uni.getStorageSync('wechatUser')
        if (token && wechatUser) {
            this.$store.commit('SET_TOKEN', token)
            this.$store.commit('SET_WECHAT_USER', wechatUser)
            if (wechatUser.currentPatientId) {
                this.$store.dispatch('getCurrentPatient', wechatUser.currentPatientId)
            }
        }
    },
    onShow: function() {
        console.log('App Show')
    },
    onHide: function() {
        console.log('App Hide')
    }
}
</script>

<style>
page {
    background-color: #f5f5f5;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.container {
    padding: 24rpx;
    min-height: 100vh;
    box-sizing: border-box;
}

.card {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.btn-primary {
    background: linear-gradient(135deg, #1989fa 0%, #007aff 100%);
    color: #fff;
    border-radius: 48rpx;
    border: none;
    font-size: 32rpx;
    padding: 20rpx 0;
}

.btn-primary:active {
    opacity: 0.85;
}

.btn-outline {
    background: #fff;
    color: #1989fa;
    border: 2rpx solid #1989fa;
    border-radius: 48rpx;
    font-size: 32rpx;
    padding: 20rpx 0;
}

.text-primary {
    color: #1989fa;
}

.text-gray {
    color: #999;
}

.text-danger {
    color: #ee0a24;
}

.text-success {
    color: #07c160;
}

.flex {
    display: flex;
}

.flex-center {
    display: flex;
    align-items: center;
    justify-content: center;
}

.flex-between {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.flex-1 {
    flex: 1;
}

.mt-24 {
    margin-top: 24rpx;
}

.mb-24 {
    margin-bottom: 24rpx;
}

.p-24 {
    padding: 24rpx;
}
</style>
