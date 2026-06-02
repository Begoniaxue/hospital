import request from '../utils/request.js'

export const getHealthRecord = (patientId) => {
    return request({
        url: '/mini/health/record/' + patientId,
        method: 'GET'
    })
}

export const saveHealthRecord = (data) => {
    return request({
        url: '/mini/health/record',
        method: 'POST',
        data
    })
}

export const getPatientInfo = (patientId) => {
    return request({
        url: '/mini/health/patient/' + patientId,
        method: 'GET'
    })
}

export const updatePatientInfo = (data) => {
    return request({
        url: '/mini/health/patient',
        method: 'PUT',
        data
    })
}

export const getAttachmentList = (patientId, category) => {
    return request({
        url: '/mini/health/attachment/list',
        method: 'GET',
        data: { patientId, category }
    })
}

export const getAttachmentPage = (data) => {
    return request({
        url: '/mini/health/attachment/page',
        method: 'GET',
        data
    })
}

export const saveAttachment = (data) => {
    return request({
        url: '/mini/health/attachment',
        method: 'POST',
        data
    })
}

export const deleteAttachment = (id) => {
    return request({
        url: '/mini/health/attachment/' + id,
        method: 'DELETE'
    })
}
