// #ifdef H5
const BASE_URL = 'http://localhost:8080/api'
// #endif
// #ifndef H5
const BASE_URL = '/api'
// #endif

const request = (options) => {
    return new Promise((resolve, reject) => {
        const token = uni.getStorageSync('token')
        const header = {
            'Content-Type': 'application/json'
        }
        if (token) {
            header['Authorization'] = 'Bearer ' + token
        }

        uni.request({
            url: BASE_URL + options.url,
            method: options.method || 'GET',
            data: options.data || {},
            header: header,
            timeout: 10000,
            success: (res) => {
                if (res.statusCode === 200) {
                    if (res.data.code === 200) {
                        resolve(res.data)
                    } else if (res.data.code === 401) {
                        uni.removeStorageSync('token')
                        uni.removeStorageSync('wechatUser')
                        uni.removeStorageSync('currentPatient')
                        uni.reLaunch({
                            url: '/pages/login/index'
                        })
                        reject(res.data)
                    } else {
                        uni.showToast({
                            title: res.data.message || '请求失败',
                            icon: 'none'
                        })
                        reject(res.data)
                    }
                } else {
                    uni.showToast({
                        title: '网络请求失败',
                        icon: 'none'
                    })
                    reject(res)
                }
            },
            fail: (err) => {
                uni.showToast({
                    title: '网络连接失败',
                    icon: 'none'
                })
                reject(err)
            }
        })
    })
}

export default request
