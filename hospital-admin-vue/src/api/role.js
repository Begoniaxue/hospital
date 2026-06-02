import request from '@/utils/request'

export function getRoleList() {
  return request({
    url: '/system/role/list',
    method: 'get'
  })
}

export function getRolePage(params) {
  return request({
    url: '/system/role/page',
    method: 'get',
    params
  })
}

export function getRole(id) {
  return request({
    url: '/system/role/' + id,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: '/system/role/' + id,
    method: 'delete'
  })
}

export function assignPermissions(roleId, permissionIds) {
  return request({
    url: '/system/role/permission',
    method: 'put',
    params: { roleId },
    data: permissionIds
  })
}
