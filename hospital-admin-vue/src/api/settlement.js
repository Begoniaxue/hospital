import request from '@/utils/request'

export function getSettlementPage(params) {
  return request({
    url: '/settlement/page',
    method: 'get',
    params
  })
}

export function getSettlement(id) {
  return request({
    url: '/settlement/' + id,
    method: 'get'
  })
}

export function createSettlement(data) {
  return request({
    url: '/settlement',
    method: 'post',
    data
  })
}

export function getDailySettlement(date) {
  return request({
    url: '/settlement/daily',
    method: 'get',
    params: { date }
  })
}

export function getMonthlySettlement(month) {
  return request({
    url: '/settlement/monthly',
    method: 'get',
    params: { month }
  })
}

export function getRefundPage(params) {
  return request({
    url: '/settlement/refund/page',
    method: 'get',
    params
  })
}

export function getRefund(id) {
  return request({
    url: '/settlement/refund/' + id,
    method: 'get'
  })
}

export function applyRefund(data) {
  return request({
    url: '/settlement/refund',
    method: 'post',
    data
  })
}

export function auditRefund(data) {
  return request({
    url: '/settlement/refund/audit',
    method: 'put',
    data
  })
}
