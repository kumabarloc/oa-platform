import request from '@/utils/request'

export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params,
  })
}

export function getRoleDetail(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'get',
  })
}

export function createRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data,
  })
}

export function updateRole(id, data) {
  return request({
    url: `/system/role/${id}`,
    method: 'put',
    data,
  })
}

export function deleteRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete',
  })
}

export function getRoleMenus(roleId) {
  return request({
    url: `/system/role/${roleId}/menus`,
    method: 'get',
  })
}

export function assignMenus(roleId, menuIds) {
  return request({
    url: `/system/role/${roleId}/menus`,
    method: 'put',
    data: menuIds,
  })
}