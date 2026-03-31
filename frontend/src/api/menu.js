import request from '@/utils/request'

export function getMenuTree() {
  return request({
    url: '/system/menu/tree',
    method: 'get',
  })
}