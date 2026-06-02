import request from '@/utils/request'

export function getDrugPage(params) {
  return request({
    url: '/pharmacy/drug/page',
    method: 'get',
    params
  })
}

export function getDrug(id) {
  return request({
    url: '/pharmacy/drug/' + id,
    method: 'get'
  })
}

export function addDrug(data) {
  return request({
    url: '/pharmacy/drug',
    method: 'post',
    data
  })
}

export function updateDrug(data) {
  return request({
    url: '/pharmacy/drug',
    method: 'put',
    data
  })
}

export function deleteDrug(id) {
  return request({
    url: '/pharmacy/drug/' + id,
    method: 'delete'
  })
}

export function stockIn(data) {
  return request({
    url: '/pharmacy/drug/stockIn',
    method: 'post',
    data
  })
}

export function stockOut(data) {
  return request({
    url: '/pharmacy/drug/stockOut',
    method: 'post',
    data
  })
}

export function getStockLogPage(params) {
  return request({
    url: '/pharmacy/drug/stockLog',
    method: 'get',
    params
  })
}

export function getWarningDrugs() {
  return request({
    url: '/pharmacy/drug/warning',
    method: 'get'
  })
}

export function getExpiredDrugs() {
  return request({
    url: '/pharmacy/drug/expired',
    method: 'get'
  })
}
