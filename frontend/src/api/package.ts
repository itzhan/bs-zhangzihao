import request from '@/utils/request'

export function getDetail(id: number) {
  return request.get(`/api/packages/${id}`)
}
