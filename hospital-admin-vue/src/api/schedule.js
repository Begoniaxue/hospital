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

export function batchGenerateSchedule(data) {
  const { doctorId, startDate, endDate, timeSlots, maxCount, registrationFee, weekdays, departmentId, departmentName, doctorName } = data
  return request({
    url: '/admin/schedule/batch-generate',
    method: 'post',
    params: { 
      doctorId, 
      startDate, 
      endDate,
      maxCount,
      registrationFee,
      departmentId,
      departmentName,
      doctorName,
      weekdays
    },
    data: timeSlots
  })
}
