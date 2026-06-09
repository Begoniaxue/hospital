// #ifdef H5
const BASE_URL = '/api'
// #endif
// #ifdef MP-WEIXIN
const BASE_URL = 'http://localhost:8080/api'
// #endif
// #ifdef APP-PLUS
const BASE_URL = 'http://localhost:8080/api'
// #endif

const statusMap = {
    0: 'pending_pay',
    1: 'pending',
    2: 'checked_in',
    3: 'cancelled',
    4: 'refunded'
}

const payMethodMap = {
    1: 'wechat',
    2: 'alipay',
    3: 'insurance',
    4: 'cash',
    5: 'bank'
}

const transformRegistrationData = (data) => {
    if (!data) return data
    
    const transform = (item) => {
        if (item.status !== undefined && typeof item.status === 'number') {
            item.status = statusMap[item.status] || item.status
        }
        if (item.payMethod !== undefined && typeof item.payMethod === 'number') {
            item.payMethod = payMethodMap[item.payMethod] || item.payMethod
        }
        if (item.registrationFee !== undefined) {
            item.fee = item.fee || item.registrationFee
        }
        if (item.registrationNo !== undefined) {
            item.orderNo = item.orderNo || item.registrationNo
        }
        if (item.visitTimeSlot !== undefined) {
            item.period = item.period || (item.visitTimeSlot === '上午' ? 'morning' : 'afternoon')
        }
        if (item.doctorTitle !== undefined) {
            item.title = item.title || item.doctorTitle
        }
        if (item.doctorName !== undefined) {
            item.name = item.name || item.doctorName
        }
        if (item.departmentName !== undefined) {
            item.department = item.department || item.departmentName
        }
        if (item.specialty !== undefined) {
            item.skill = item.skill || item.specialty
        }
        if (item.consultationFee !== undefined) {
            item.fee = item.fee || item.consultationFee
        }
        return item
    }
    
    if (Array.isArray(data)) {
        return data.map(transform)
    }
    return transform(data)
}

const transformResponseData = (data, url) => {
    if (!data) return data
    
    if (url.includes('/registration')) {
        if (data.data) {
            data.data = transformRegistrationData(data.data)
        }
    }
    
    if (url.includes('/doctor')) {
        if (data.data) {
            if (Array.isArray(data.data)) {
                data.data = data.data.map(item => transformRegistrationData(item))
            } else {
                data.data = transformRegistrationData(data.data)
            }
        }
    }
    
    return data
}

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
                        const transformedData = transformResponseData(res.data, options.url)
                        resolve(transformedData)
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
                            title: res.data.message || res.data.msg || '请求失败',
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
