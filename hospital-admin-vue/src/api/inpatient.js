import request from '@/utils/request'

export function getInpatientPage(params) {
  return request({
    url: '/inpatient/admission/page',
    method: 'get',
    params
  })
}

export function getInpatient(id) {
  return request({
    url: '/inpatient/admission/' + id,
    method: 'get'
  })
}

export function addInpatient(data) {
  return request({
    url: '/inpatient/admission',
    method: 'post',
    data
  })
}

export function updateInpatient(data) {
  return request({
    url: '/inpatient/admission',
    method: 'put',
    data
  })
}

export function deleteInpatient(id) {
  return request({
    url: '/inpatient/admission/' + id,
    method: 'delete'
  })
}

export function dischargeInpatient(id) {
  return request({
    url: '/inpatient/admission/' + id + '/discharge',
    method: 'put'
  })
}

export function getBedPage(params) {
  return request({
    url: '/inpatient/bed/page',
    method: 'get',
    params
  })
}

export function getBedList(params) {
  return request({
    url: '/inpatient/bed/list',
    method: 'get',
    params
  })
}

export function addBed(data) {
  return request({
    url: '/inpatient/bed',
    method: 'post',
    data
  })
}

export function updateBed(data) {
  return request({
    url: '/inpatient/bed',
    method: 'put',
    data
  })
}

export function deleteBed(id) {
  return request({
    url: '/inpatient/bed/' + id,
    method: 'delete'
  })
}

export function assignBed(id, inpatientId) {
  return request({
    url: '/inpatient/bed/' + id + '/assign',
    method: 'put',
    params: { inpatientId }
  })
}

export function releaseBed(id) {
  return request({
    url: '/inpatient/bed/' + id + '/release',
    method: 'put'
  })
}

export function getDepositPage(params) {
  return request({
    url: '/inpatient/deposit/page',
    method: 'get',
    params
  })
}

export function getDeposit(id) {
  return request({
    url: '/inpatient/deposit/' + id,
    method: 'get'
  })
}

export function payDeposit(data) {
  return request({
    url: '/inpatient/deposit/pay',
    method: 'post',
    data
  })
}

export function refundDeposit(id) {
  return request({
    url: '/inpatient/deposit/' + id + '/refund',
    method: 'put'
  })
}

export function getDepositSummary(inpatientId) {
  return request({
    url: '/inpatient/deposit/summary/' + inpatientId,
    method: 'get'
  })
}

export function getOrderPage(params) {
  return request({
    url: '/inpatient/order/page',
    method: 'get',
    params
  })
}

export function getOrder(id) {
  return request({
    url: '/inpatient/order/' + id,
    method: 'get'
  })
}

export function addOrder(data) {
  return request({
    url: '/inpatient/order',
    method: 'post',
    data
  })
}

export function executeOrder(id, userId, userName) {
  return request({
    url: '/inpatient/order/' + id + '/execute',
    method: 'put',
    params: { userId, userName }
  })
}

export function cancelOrder(id) {
  return request({
    url: '/inpatient/order/' + id + '/cancel',
    method: 'put'
  })
}

export function getRecordPage(params) {
  return request({
    url: '/inpatient/record/page',
    method: 'get',
    params
  })
}

export function getRecord(id) {
  return request({
    url: '/inpatient/record/' + id,
    method: 'get'
  })
}

export function addRecord(data) {
  return request({
    url: '/inpatient/record',
    method: 'post',
    data
  })
}

export function updateRecord(data) {
  return request({
    url: '/inpatient/record',
    method: 'put',
    data
  })
}

export function getFeePage(params) {
  return request({
    url: '/inpatient/fee/page',
    method: 'get',
    params
  })
}

export function getFee(id) {
  return request({
    url: '/inpatient/fee/' + id,
    method: 'get'
  })
}

export function addFee(data) {
  return request({
    url: '/inpatient/fee',
    method: 'post',
    data
  })
}

export function getFeeSummary(inpatientId) {
  return request({
    url: '/inpatient/fee/summary/' + inpatientId,
    method: 'get'
  })
}

export function exportFee(params) {
  return request({
    url: '/inpatient/fee/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function getDischargePage(params) {
  return request({
    url: '/inpatient/discharge/page',
    method: 'get',
    params
  })
}

export function getDischarge(id) {
  return request({
    url: '/inpatient/discharge/' + id,
    method: 'get'
  })
}

export function settleDischarge(inpatientId, data) {
  return request({
    url: '/inpatient/discharge/settle',
    method: 'post',
    params: { inpatientId },
    data
  })
}

export function printDischarge(id) {
  return request({
    url: '/inpatient/discharge/' + id + '/print',
    method: 'get',
    responseType: 'blob'
  })
}

export function getArchivePage(params) {
  return request({
    url: '/inpatient/archive/page',
    method: 'get',
    params
  })
}

export function getArchive(id) {
  return request({
    url: '/inpatient/archive/' + id,
    method: 'get'
  })
}

export function archiveInpatient(inpatientId) {
  return request({
    url: '/inpatient/archive/archive',
    method: 'post',
    params: { inpatientId }
  })
}

export function borrowArchive(id, data) {
  return request({
    url: '/inpatient/archive/' + id + '/borrow',
    method: 'put',
    params: {
      borrowUserId: data.borrowUserId,
      borrowUserName: data.borrowUserName,
      days: data.days
    }
  })
}

export function returnArchive(id) {
  return request({
    url: '/inpatient/archive/' + id + '/return',
    method: 'put'
  })
}
