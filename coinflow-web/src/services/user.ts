import request from '@/utils/request'

export function getUserById(id: number) {
  return request.get(`/users/${id}`)
}

export function updateUser(data: any) {
  return request.put('/users', data)
}

export function changePassword(data: any) {
  return request.post('/users/change-password', data)
}

export function resetPassword(data: any) {
  return request.post('/users/reset-password', data)
}
