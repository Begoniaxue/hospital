import request from '../utils/request.js'

export const getDepartmentTree = () => {
    return request({
        url: '/mini/department/tree',
        method: 'GET'
    })
}

export const getDepartmentsByParentId = (parentId) => {
    return request({
        url: '/mini/department/list/parent/' + parentId,
        method: 'GET'
    })
}

export const getDepartmentsByLevel = (level) => {
    return request({
        url: '/mini/department/level/' + level,
        method: 'GET'
    })
}

export const getDepartmentDetail = (id) => {
    return request({
        url: '/mini/department/' + id,
        method: 'GET'
    })
}

export const searchDepartments = (keyword) => {
    return request({
        url: '/mini/department/search',
        method: 'GET',
        data: { keyword }
    })
}
