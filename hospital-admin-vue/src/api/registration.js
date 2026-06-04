import request from '@/utils/request'

export function getRegistrationPage(params) {
  return request({
    url: '/admin/registration/page',
    method: 'get',
    params
  })
}

export function getRegistration(id) {
  return request({
    url: '/admin/registration/' + id,
    method: 'get'
  })
}

export function updateRegistrationStatus(id, status) {
  return request({
    url: '/admin/registration/status/' + id,
    method: 'post',
    params: { status }
  })
}

export function checkinRegistration(id) {
  return request({
    url: '/admin/registration/checkin/' + id,
    method: 'post'
  })
}

export function finishRegistration(id) {
  return request({
    url: '/admin/registration/finish/' + id,
    method: 'post'
  })
}

export function refundRegistration(id, reason) {
  return request({
    url: '/admin/registration/refund/' + id,
    method: 'post',
    params: { reason }
  })
}

export function getRegistrationStatistics(params) {
  return request({
    url: '/admin/registration/statistics',
    method: 'get',
    params
  })
}
