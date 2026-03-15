import request from '@/utils/request'

export function getMyList(page: number, size: number) {
  return request.get('/api/notifications', { params: { page, size } })
}

export function markRead(id: number) {
  return request.put(`/api/notifications/${id}/read`)
}

export function markAllRead() {
  return request.put('/api/notifications/read-all')
}

export function getUnreadCount() {
  return request.get('/api/notifications/unread-count')
}
