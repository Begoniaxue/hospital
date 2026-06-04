import request from '../utils/request.js'

export const getMessageList = (wechatUserId, offset, limit) => {
    return request({
        url: '/mini/message/list/' + wechatUserId,
        method: 'GET',
        data: { offset, limit }
    })
}

export const getUnreadCount = (wechatUserId) => {
    return request({
        url: '/mini/message/unread/' + wechatUserId,
        method: 'GET'
    })
}

export const markAsRead = (id) => {
    return request({
        url: '/mini/message/read/' + id,
        method: 'POST'
    })
}

export const markAllAsRead = (wechatUserId) => {
    return request({
        url: '/mini/message/read-all/' + wechatUserId,
        method: 'POST'
    })
}
