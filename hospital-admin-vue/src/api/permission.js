import request from '@/utils/request'

export function getPermissionTree() {
  return request({
    url: '/system/permission/tree',
    method: 'get'
  })
}

export function getPermission(id) {
  return request({
    url: '/system/permission/' + id,
    method: 'get'
  })
}

export function addPermission(data) {
  return request({
    url: '/system/permission',
    method: 'post',
    data
  })
}

export function updatePermission(data) {
  return request({
    url: '/system/permission',
    method: 'put',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: '/system/permission/' + id,
    method: 'delete'
  })
}
