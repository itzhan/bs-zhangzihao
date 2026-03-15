import request from '@/utils/request'

export function add(farmhouseId: number) {
  return request.post(`/api/favorites/${farmhouseId}`)
}

export function remove(farmhouseId: number) {
  return request.delete(`/api/favorites/${farmhouseId}`)
}

export function getMyList(page: number, size: number) {
  return request.get('/api/favorites', { params: { page, size } })
}

export function check(farmhouseId: number) {
  return request.get(`/api/favorites/check/${farmhouseId}`)
}
