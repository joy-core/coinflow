import request from '@/utils/request'

export function listNotifications(userId: number) {
  return request.get('/notifications', { params: { userId } })
}

export function markAsRead(id: number) {
  return request.put(`/notifications/${id}/read`)
}

export function markAllAsRead(userId: number) {
  return request.put('/notifications/read-all', null, { params: { userId } })
}

export function getUnreadCount(userId: number) {
  return request.get('/notifications/unread-count', { params: { userId } })
}

export function deleteNotification(id: number) {
  return request.delete(`/notifications/${id}`)
}
