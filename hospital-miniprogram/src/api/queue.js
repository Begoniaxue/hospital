import request from '../utils/request.js'

export const getQueueByRegistration = (registrationId) => {
    return request({
        url: '/mini/queue/registration/' + registrationId,
        method: 'GET'
    })
}

export const getQueueByDoctor = (doctorId, date) => {
    return request({
        url: '/mini/queue/doctor/' + doctorId,
        method: 'GET',
        data: { date }
    })
}

export const getCurrentNumber = (doctorId, date) => {
    return request({
        url: '/mini/queue/current/' + doctorId,
        method: 'GET',
        data: { date }
    })
}
