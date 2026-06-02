import request from '@/utils/request'

export function getPrescriptionPage(params) {
  return request({
    url: '/pharmacy/prescription/page',
    method: 'get',
    params
  })
}

export function getPrescription(id) {
  return request({
    url: '/pharmacy/prescription/' + id,
    method: 'get'
  })
}

export function addPrescription(data) {
  return request({
    url: '/pharmacy/prescription',
    method: 'post',
    data
  })
}

export function auditPrescription(data) {
  return request({
    url: '/pharmacy/prescription/audit',
    method: 'put',
    data
  })
}

export function dispensePrescription(id) {
  return request({
    url: '/pharmacy/prescription/dispense/' + id,
    method: 'put'
  })
}

export function issuePrescription(id) {
  return request({
    url: '/pharmacy/prescription/issue/' + id,
    method: 'put'
  })
}
