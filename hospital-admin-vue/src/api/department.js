import request from '@/utils/request'

export function getDepartmentPage(params) {
  return request({
    url: '/admin/department/page',
    method: 'get',
    params
  })
}

export function getDepartmentList(params) {
  return request({
    url: '/admin/department/list',
    method: 'get',
    params
  })
}

export function getDepartment(id) {
  return request({
    url: '/admin/department/' + id,
    method: 'get'
  })
}

export function addDepartment(data) {
  return request({
    url: '/admin/department',
    method: 'post',
    data
  })
}

export function updateDepartment(data) {
  return request({
    url: '/admin/department',
    method: 'put',
    data
  })
}

export function deleteDepartment(id) {
  return request({
    url: '/admin/department/' + id,
    method: 'delete'
  })
}

export function batchDeleteDepartment(data) {
  return request({
    url: '/admin/department/batch-delete',
    method: 'post',
    data
  })
}
