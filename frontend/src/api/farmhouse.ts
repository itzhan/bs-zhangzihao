import request from '@/utils/request'

export function getList(keyword?: string) {
  return request.get('/api/farmhouses', { params: { keyword } })
}

export function getDetail(id: number) {
  return request.get(`/api/farmhouses/${id}`)
}

export function getPackages(id: number) {
  return request.get(`/api/farmhouses/${id}/packages`)
}

export function getReviews(id: number, page: number, size: number) {
  return request.get(`/api/farmhouses/${id}/reviews`, { params: { page, size } })
}
