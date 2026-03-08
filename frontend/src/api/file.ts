import request from '@/utils/request'

export function upload(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
