import request from '@/utils/request'

export function getDocumentList(params) {
  return request({
    url: '/workflow/document/list',
    method: 'get',
    params,
  })
}

export function getDocumentDetail(id) {
  return request({ url: `/workflow/document/${id}`, method: 'get' })
}

export function createDocument(data) {
  return request({ url: '/workflow/document', method: 'post', data })
}

export function updateDocument(id, data) {
  return request({ url: `/workflow/document/${id}`, method: 'put', data })
}

export function deleteDocument(id) {
  return request({ url: `/workflow/document/${id}`, method: 'delete' })
}

export function submitDocument(id, userId) {
  return request({ url: `/workflow/document/${id}/submit`, method: 'post', data: { userId } })
}

export function approveDocument(id, approverId) {
  return request({ url: `/workflow/document/${id}/approve`, method: 'put', data: { approverId } })
}

export function rejectDocument(id, approverId, reason) {
  return request({ url: `/workflow/document/${id}/reject`, method: 'put', data: { approverId, reason } })
}

export function getMyTasks(userId) {
  return request({ url: '/workflow/document/my-tasks', method: 'get', params: { userId } })
}

export function getApprovalHistory(id) {
  return request({ url: `/workflow/document/${id}/history`, method: 'get' })
}
