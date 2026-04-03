import request from '@/utils/request'

export function getVehicleList(params) {
  return request({
    url: '/api/workflow/vehicle/list',
    method: 'get',
    params,
  })
}

export function getVehicleDetail(id) {
  return request({ url: `/api/workflow/vehicle/${id}`, method: 'get' })
}

export function createVehicle(data) {
  return request({ url: '/api/workflow/vehicle', method: 'post', data })
}

export function updateVehicle(id, data) {
  return request({ url: `/api/workflow/vehicle/${id}`, method: 'put', data })
}

export function deleteVehicle(id) {
  return request({ url: `/api/workflow/vehicle/${id}`, method: 'delete' })
}

export function submitVehicle(id) {
  return request({ url: `/api/workflow/vehicle/${id}/submit`, method: 'post' })
}

export function approveVehicle(id, data) {
  return request({ url: `/api/workflow/vehicle/${id}/approve`, method: 'put', data })
}

export function rejectVehicle(id, reason) {
  return request({ url: `/api/workflow/vehicle/${id}/reject`, method: 'put', data: { reason } })
}

export function getMyVehicleTasks() {
  return request({ url: '/api/workflow/vehicle/my-tasks', method: 'get' })
}
