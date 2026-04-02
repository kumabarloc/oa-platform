import request from '@/utils/request'

export function getLoginLogList(params) {
  return request({ url: '/system/login-log/list', method: 'get', params })
}

export function getOperationLogList(params) {
  return request({ url: '/system/operation-log/list', method: 'get', params })
}
