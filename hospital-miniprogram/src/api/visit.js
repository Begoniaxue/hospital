import request from '../utils/request.js'

export const generateVisitCode = (patientId, codeType, useScene) => {
    return request({
        url: '/mini/visit/code/' + patientId,
        method: 'GET',
        data: { codeType, useScene }
    })
}

export const getLatestVisitCode = (patientId) => {
    return request({
        url: '/mini/visit/code/latest/' + patientId,
        method: 'GET'
    })
}

export const verifyVisitCode = (codeContent) => {
    return request({
        url: '/mini/visit/code/verify',
        method: 'POST',
        data: { codeContent }
    })
}
