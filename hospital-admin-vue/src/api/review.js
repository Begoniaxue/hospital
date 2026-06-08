import request from '@/utils/request'

export function getReviewPage(params) {
  return request({
    url: '/admin/review/page',
    method: 'get',
    params
  })
}

export function getReviewDetail(id) {
  return request({
    url: '/admin/review/' + id,
    method: 'get'
  })
}

export function replyReview(data) {
  return request({
    url: '/admin/review/reply',
    method: 'post',
    data
  })
}

export function updateReviewStatus(data) {
  return request({
    url: '/admin/review/status',
    method: 'post',
    data
  })
}
