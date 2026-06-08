import request from '@/utils/request'

export function getComplaintPage(params) {
  return request({
    url: '/admin/complaint/page',
    method: 'get',
    params
  })
}

export function getComplaintDetail(id) {
  return request({
    url: '/admin/complaint/' + id,
    method: 'get'
  })
}

export function handleComplaint(data) {
  return request({
    url: '/admin/complaint/handle',
    method: 'post',
    data
  })
}

export function updateComplaintStatus(data) {
  return request({
    url: '/admin/complaint/status',
    method: 'post',
    data
  })
}
