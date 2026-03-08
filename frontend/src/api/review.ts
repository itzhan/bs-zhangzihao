import request from '@/utils/request'

export function create(data: {
  farmhouseId: number
  orderId: number
  rating: number
  content: string
  images?: string
}) {
  return request.post('/api/reviews', data)
}

export function getByFarmhouse(farmhouseId: number, page: number, size: number) {
  return request.get(`/api/reviews/farmhouse/${farmhouseId}`, { params: { page, size } })
}
