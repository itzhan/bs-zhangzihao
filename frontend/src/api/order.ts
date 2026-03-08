import request from '@/utils/request'

export function create(data: {
  reservationId?: number
  farmhouseId: number
  packageId: number
  personCount: number
  unitPrice: number
  totalAmount: number
}) {
  return request.post('/api/orders', data)
}

export function getMyList(page: number, size: number, status?: number) {
  return request.get('/api/orders/my', { params: { page, size, status } })
}

export function getDetail(id: number) {
  return request.get(`/api/orders/${id}`)
}

export function pay(id: number, paymentMethod: string) {
  return request.put(`/api/orders/${id}/pay`, { paymentMethod })
}

export function cancel(id: number, reason: string) {
  return request.put(`/api/orders/${id}/cancel`, { cancelReason: reason })
}

export function requestRefund(id: number, reason: string) {
  return request.put(`/api/orders/${id}/refund`, { refundReason: reason })
}
