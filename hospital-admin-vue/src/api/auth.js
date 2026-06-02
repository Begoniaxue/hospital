import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function getCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function getUserInfo() {
  return request({
    url: '/auth/userInfo',
    method: 'get'
  })
}

export function getMenu() {
  return request({
    url: '/auth/menu',
    method: 'get'
  })
}

export function updatePassword(data) {
  return request({
    url: '/auth/updatePassword',
    method: 'post',
    data
  })
}
