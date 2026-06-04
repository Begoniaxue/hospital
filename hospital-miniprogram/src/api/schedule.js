import request from '../utils/request.js'

export const getDoctorSchedules = (doctorId, startDate, endDate) => {
    return request({
        url: '/mini/schedule/doctor/' + doctorId,
        method: 'GET',
        data: { startDate, endDate }
    })
}

export const getDepartmentSchedules = (departmentId, date) => {
    return request({
        url: '/mini/schedule/department/' + departmentId,
        method: 'GET',
        data: { date }
    })
}

export const getTodaySchedules = () => {
    return request({
        url: '/mini/schedule/today',
        method: 'GET'
    })
}

export const getScheduleDetail = (id) => {
    return request({
        url: '/mini/schedule/' + id,
        method: 'GET'
    })
}
