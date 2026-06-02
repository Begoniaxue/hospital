import request from '../utils/request.js'

export const getFamilyList = (wechatUserId) => {
    return request({
        url: '/mini/family/list',
        method: 'GET',
        data: { wechatUserId }
    })
}

export const addFamilyMember = (data) => {
    return request({
        url: '/mini/family/add',
        method: 'POST',
        data
    })
}

export const updateFamilyMember = (data) => {
    return request({
        url: '/mini/family/update',
        method: 'PUT',
        data
    })
}

export const deleteFamilyMember = (id) => {
    return request({
        url: '/mini/family/' + id,
        method: 'DELETE'
    })
}

export const disableFamilyMember = (id) => {
    return request({
        url: '/mini/family/disable/' + id,
        method: 'PUT'
    })
}

export const switchCurrentPatient = (wechatUserId, patientId) => {
    return request({
        url: '/mini/family/switchPatient',
        method: 'POST',
        data: { wechatUserId, patientId }
    })
}
