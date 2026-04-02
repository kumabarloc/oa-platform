import request from '@/utils/request'

export function getNotificationList(params) {
  return request({ url: '/system/notification/list', method: 'get', params })
}

export function getNotificationDetail(id) {
  return request({ url: `/system/notification/${id}`, method: 'get' })
}

export function createNotification(data) {
  return request({ url: '/system/notification', method: 'post', data })
}

export function updateNotification(id, data) {
  return request({ url: `/system/notification/${id}`, method: 'put', data })
}

export function deleteNotification(id) {
  return request({ url: `/system/notification/${id}`, method: 'delete' })
}

export function markRead(id) {
  return request({ url: `/system/notification/${id}/read`, method: 'put' })
}

export function markAllRead() {
  return request({ url: '/system/notification/read-all', method: 'put' })
}

export function getMyNotifications(params) {
  return request({ url: '/system/notification/my', method: 'get', params })
}

export function getUnreadCount() {
  return request({ url: '/system/notification/unread-count', method: 'get' })
}
