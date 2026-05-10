import request from '@/utils/request'

export function listFeedback(userId: number) {
  return request.get('/feedback', { params: { userId } })
}

export function createFeedback(data: any) {
  return request.post('/feedback', data)
}
