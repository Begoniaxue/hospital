import request from '@/utils/request'

export function getSymptomPage(params) {
  return request({
    url: '/admin/symptom/page',
    method: 'get',
    params
  })
}

export function getSymptomList(params) {
  return request({
    url: '/admin/symptom/list',
    method: 'get',
    params
  })
}

export function getSymptom(id) {
  return request({
    url: '/admin/symptom/' + id,
    method: 'get'
  })
}

export function addSymptom(data) {
  return request({
    url: '/admin/symptom',
    method: 'post',
    data
  })
}

export function updateSymptom(data) {
  return request({
    url: '/admin/symptom',
    method: 'put',
    data
  })
}

export function deleteSymptom(id) {
  return request({
    url: '/admin/symptom/' + id,
    method: 'delete'
  })
}

export function batchDeleteSymptom(data) {
  return request({
    url: '/admin/symptom/batch-delete',
    method: 'post',
    data
  })
}
