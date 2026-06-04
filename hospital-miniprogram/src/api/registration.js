import request from '../utils/request.js'

export const createRegistration = (data) => {
    return request({
        url: '/mini/registration/create',
        method: 'POST',
        data
    })
}

export const payRegistration = (data) => {
    return request({
        url: '/mini/registration/pay',
        method: 'POST',
        data
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
    return request({
        url: '/mini/registration/patient/' + patientId,
        method: 'GET',
        data: { status, offset, limit }
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
