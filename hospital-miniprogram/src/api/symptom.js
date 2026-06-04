import request from '../utils/request.js'

export const getSymptomList = (parentId) => {
    return request({
        url: '/mini/symptom/list',
        method: 'GET',
        data: { parentId: parentId || 0 }
    })
}

export const searchSymptoms = (keyword) => {
    return request({
        url: '/mini/symptom/search',
        method: 'GET',
        data: { keyword }
    })
}

export const diagnose = (symptomIds) => {
    return request({
        url: '/mini/symptom/diagnose',
        method: 'POST',
        data: { symptomIds }
    })
}

export const aiDiagnose = (symptomText) => {
    return request({
        url: '/mini/symptom/ai-diagnose',
        method: 'POST',
        data: { symptomText }
    })
}
