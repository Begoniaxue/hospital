import request from '../utils/request.js'

const payMethodToNumber = (method) => {
    const map = {
        'wechat': 1,
        'alipay': 2,
        'insurance': 3,
        'cash': 4,
        'bank': 5
    }
    return map[method] || 1
}

const statusToNumber = (status) => {
    const map = {
        'pending_pay': 0,
        'pending': 1,
        'checked_in': 2,
        'cancelled': 3,
        'refunded': 4
    }
    return map[status] !== undefined ? map[status] : null
}

export const createRegistration = (data) => {
    return request({
        url: '/mini/registration/create',
        method: 'POST',
        data
    })
}

export const payRegistration = (data) => {
    const requestData = {
        ...data,
        payMethod: typeof data.payMethod === 'string' ? payMethodToNumber(data.payMethod) : data.payMethod
    }
    return request({
        url: '/mini/registration/pay',
        method: 'POST',
        data: requestData
    })
}

export const mockPayRegistration = async (data) => {
    return new Promise((resolve, reject) => {
        setTimeout(async () => {
            try {
                const payMethodNum = typeof data.payMethod === 'string' ? payMethodToNumber(data.payMethod) : data.payMethod
                const backendRes = await request({
                    url: '/mini/registration/pay',
                    method: 'POST',
                    data: {
                        registrationId: data.registrationId,
                        payMethod: payMethodNum
                    }
                })
                
                if (backendRes.code === 200) {
                    resolve({
                        code: 200,
                        message: '支付成功',
                        data: {
                            id: data.registrationId,
                            status: 'pending',
                            payMethod: data.payMethod,
                            payTime: new Date().toISOString()
                        }
                    })
                } else {
                    reject(new Error(backendRes.message || '支付失败'))
                }
            } catch (e) {
                reject(e)
            }
        }, 1000)
    })
}

export const cancelRegistration = (data) => {
    return request({
        url: '/mini/registration/cancel',
        method: 'POST',
        data
    })
}

export const getPatientRegistrations = (patientId, status, offset, limit) => {
    const statusNum = status ? statusToNumber(status) : null
    return request({
        url: '/mini/registration/patient/' + patientId,
        method: 'GET',
        data: { status: statusNum, offset, limit }
    })
}

export const getRegistrationDetail = (id) => {
    return request({
        url: '/mini/registration/' + id,
        method: 'GET'
    })
}

export const checkinRegistration = (id) => {
    return request({
        url: '/mini/registration/checkin/' + id,
        method: 'GET'
    })
}

export const getQueueInfo = (id) => {
    return request({
        url: '/mini/registration/queue/' + id,
        method: 'GET'
    })
}
