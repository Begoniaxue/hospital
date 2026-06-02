import request from '@/utils/request'

export function getPatientPage(params) {
  return request({
    url: '/patient/page',
    method: 'get',
    params
  })
}

export function getPatient(id) {
  return request({
    url: '/patient/' + id,
    method: 'get'
  })
}

export function addPatient(data) {
  return request({
    url: '/patient',
    method: 'post',
    data
  })
}

export function updatePatient(data) {
  return request({
    url: '/patient',
    method: 'put',
    data
  })
}

export function deletePatient(id) {
  return request({
    url: '/patient/' + id,
    method: 'delete'
  })
}
