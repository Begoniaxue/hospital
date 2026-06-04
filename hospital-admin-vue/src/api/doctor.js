import request from '@/utils/request'

export function getDoctorPage(params) {
  return request({
    url: '/admin/doctor/page',
    method: 'get',
    params
  })
}

export function getDoctorList(params) {
  return request({
    url: '/admin/doctor/list',
    method: 'get',
    params
  })
}

export function getDoctor(id) {
  return request({
    url: '/admin/doctor/' + id,
    method: 'get'
  })
}

export function addDoctor(data) {
  return request({
    url: '/admin/doctor',
    method: 'post',
    data
  })
}

export function updateDoctor(data) {
  return request({
    url: '/admin/doctor',
    method: 'put',
    data
  })
}

export function deleteDoctor(id) {
  return request({
    url: '/admin/doctor/' + id,
    method: 'delete'
  })
}

export function batchDeleteDoctor(data) {
  return request({
    url: '/admin/doctor/batch-delete',
    method: 'post',
    data
  })
}
