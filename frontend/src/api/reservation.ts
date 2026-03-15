import request from '@/utils/request'

export function create(data: {
  farmhouseId: number
  packageId: number
  scheduleId: number
  reserveDate: string
  personCount: number
  contactName: string
  contactPhone: string
  remark?: string
}) {
  return request.post('/api/reservations', data)
}

export function getMyList(page: number, size: number, status?: number) {
  return request.get('/api/reservations', { params: { page, size, status } })
}

export function getDetail(id: number) {
  return request.get(`/api/reservations/${id}`)
}

export function cancel(id: number, reason: string) {
  return request.put(`/api/reservations/${id}/cancel`, { cancelReason: reason })
}
