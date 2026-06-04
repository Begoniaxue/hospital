import request from '@/utils/request'

export function getSchedulePage(params) {
  return request({
    url: '/admin/schedule/page',
    method: 'get',
    params
  })
}

export function getSchedule(id) {
  return request({
    url: '/admin/schedule/' + id,
    method: 'get'
  })
}

export function addSchedule(data) {
  return request({
    url: '/admin/schedule',
    method: 'post',
    data
  })
}

export function updateSchedule(data) {
  return request({
    url: '/admin/schedule',
    method: 'put',
    data
  })
}

export function deleteSchedule(id) {
  return request({
    url: '/admin/schedule/' + id,
    method: 'delete'
  })
}

export function suspendSchedule(id, reason) {
  return request({
    url: '/admin/schedule/suspend/' + id,
    method: 'post',
    params: { reason }
  })
}

export function batchGenerateSchedule(params, data) {
  return request({
    url: '/admin/schedule/batch-generate',
    method: 'post',
    params,
    data
  })
}
