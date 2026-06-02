import request from '../utils/request.js'

export const wechatLogin = (data) => {
    return request({
        url: '/mini/auth/login',
        method: 'POST',
        data
    })
}

export const sendSmsCode = (data) => {
    return request({
        url: '/mini/auth/sendSmsCode',
        method: 'POST',
        data
    })
}

export const verifySmsCode = (data) => {
    return request({
        url: '/mini/auth/verifySmsCode',
        method: 'POST',
        data
    })
}

export const realNameAuth = (data) => {
    return request({
        url: '/mini/auth/realName',
        method: 'POST',
        data
    })
}
