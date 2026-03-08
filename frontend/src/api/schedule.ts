import request from '@/utils/request'

export function getByPackage(packageId: number) {
  return request.get(`/api/schedules/package/${packageId}`)
}
