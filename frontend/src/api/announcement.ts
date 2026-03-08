import request from '@/utils/request'

export function getList(page: number, size: number) {
  return request.get('/api/announcements', { params: { page, size } })
}
