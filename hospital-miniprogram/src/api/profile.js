import request from '../utils/request.js'
import { sendSmsCode as authSendSmsCode } from './auth.js'

export const getOrders = (patientId, type) => {
    return request({
        url: '/mini/profile/orders',
        method: 'GET',
        data: { patientId, type }
    })
}

export const submitReview = (data) => {
    return request({
        url: '/mini/profile/review',
        method: 'POST',
        data
    })
}

export const getReviews = (patientId) => {
    return request({
        url: '/mini/profile/reviews',
        method: 'GET',
        data: { patientId }
    })
}

export const submitComplaint = (data) => {
    return request({
        url: '/mini/profile/complaint',
        method: 'POST',
        data
    })
}

export const getComplaints = (patientId) => {
    return request({
        url: '/mini/profile/complaints',
        method: 'GET',
        data: { patientId }
    })
}

export const updatePhone = (data) => {
    return request({
        url: '/mini/profile/updatePhone',
        method: 'POST',
        data
    })
}

export const sendSmsCode = (phone) => {
    return authSendSmsCode({ phone })
}

export const getMessageSettings = (patientId) => {
    return request({
        url: '/mini/profile/messageSettings',
        method: 'GET',
        data: { patientId }
    })
}

export const saveMessageSettings = (patientId, settings) => {
    return request({
        url: '/mini/profile/messageSettings',
        method: 'POST',
        data: { patientId, ...settings }
    })
}

export const getAbout = () => {
    return request({
        url: '/mini/profile/about',
        method: 'GET'
    })
}

export const getPrivacy = () => {
    return request({
        url: '/mini/profile/privacy',
        method: 'GET'
    })
}
