import request from '../utils/request.js'

export const getDoctorsByDepartment = (departmentId) => {
    return request({
        url: '/mini/doctor/department/' + departmentId,
        method: 'GET'
    })
}

export const getDoctorDetail = (id) => {
    return request({
        url: '/mini/doctor/' + id,
        method: 'GET'
    })
}

export const searchDoctors = (keyword) => {
    return request({
        url: '/mini/doctor/search',
        method: 'GET',
        data: { keyword }
    })
}

export const getRecommendDoctors = (departmentId, limit) => {
    return request({
        url: '/mini/doctor/recommend',
        method: 'GET',
        data: { departmentId, limit }
    })
}
