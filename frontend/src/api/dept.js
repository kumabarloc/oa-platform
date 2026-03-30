import request from '@/utils/request'

export function getDeptList(params) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params,
  })
}

export function getDeptTree() {
  return request({
    url: '/system/dept/tree',
    method: 'get',
  })
}

export function getDeptDetail(id) {
  return request({
    url: `/system/dept/${id}`,
    method: 'get',
  })
}

export function createDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data,
  })
}

export function updateDept(id, data) {
  return request({
    url: `/system/dept/${id}`,
    method: 'put',
    data,
  })
}

export function deleteDept(id) {
  return request({
    url: `/system/dept/${id}`,
    method: 'delete',
  })
}