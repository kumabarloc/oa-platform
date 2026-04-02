import request from '@/utils/request'

export function getIncomingList(params) {
  return request({ url: '/workflow/incoming/list', method: 'get', params })
}

export function getIncomingDetail(id) {
  return request({ url: `/workflow/incoming/${id}`, method: 'get' })
}

export function createIncoming(data) {
  return request({ url: '/workflow/incoming', method: 'post', data })
}

export function updateIncoming(id, data) {
  return request({ url: `/workflow/incoming/${id}`, method: 'put', data })
}

export function deleteIncoming(id) {
  return request({ url: `/workflow/incoming/${id}`, method: 'delete' })
}

export function distributeIncoming(id) {
  return request({ url: `/workflow/incoming/${id}/distribute`, method: 'put' })
}

export function archiveIncoming(id) {
  return request({ url: `/workflow/incoming/${id}/archive`, method: 'put' })
}
