import request from '@/utils/request'

export function getRegistrationReport(params) {
  return request({
    url: '/report/registration',
    method: 'get',
    params
  })
}

export function getRegistrationSummary(params) {
  return request({
    url: '/report/registration/summary',
    method: 'get',
    params
  })
}

export function exportRegistrationReport(params) {
  return request({
    url: '/report/registration',
    method: 'get',
    params: { ...params, format: 'excel' },
    responseType: 'blob'
  })
}

export function getOutpatientReport(params) {
  return request({
    url: '/report/outpatient',
    method: 'get',
    params
  })
}

export function getOutpatientSummary(params) {
  return request({
    url: '/report/outpatient/summary',
    method: 'get',
    params
  })
}

export function exportOutpatientReport(params) {
  return request({
    url: '/report/outpatient',
    method: 'get',
    params: { ...params, format: 'excel' },
    responseType: 'blob'
  })
}

export function getDoctorWorkloadReport(params) {
  return request({
    url: '/report/doctor',
    method: 'get',
    params
  })
}

export function exportDoctorWorkloadReport(params) {
  return request({
    url: '/report/doctor',
    method: 'get',
    params: { ...params, format: 'excel' },
    responseType: 'blob'
  })
}

export function getRevenueReport(params) {
  return request({
    url: '/report/revenue',
    method: 'get',
    params
  })
}

export function getRevenueSummary(params) {
  return request({
    url: '/report/revenue/summary',
    method: 'get',
    params
  })
}

export function exportRevenueReport(params) {
  return request({
    url: '/report/revenue',
    method: 'get',
    params: { ...params, format: 'excel' },
    responseType: 'blob'
  })
}

export function getDrugStockReport(params) {
  return request({
    url: '/report/drug',
    method: 'get',
    params
  })
}

export function exportDrugStockReport(params) {
  return request({
    url: '/report/drug',
    method: 'get',
    params: { ...params, format: 'excel' },
    responseType: 'blob'
  })
}

export function getPatientTrendReport(params) {
  return request({
    url: '/report/trend',
    method: 'get',
    params
  })
}

export function exportPatientTrendReport(params) {
  return request({
    url: '/report/trend',
    method: 'get',
    params: { ...params, format: 'excel' },
    responseType: 'blob'
  })
}
