import request from '@/utils/request'

export function listTransferRecords(userId: number) {
  return request.get('/transfer-records', { params: { userId } })
}

export function addTransferRecord(data: any) {
  return request.post('/transfer-records', data)
}

export function updateTransferRecord(data: any) {
  return request.put('/transfer-records', data)
}

export function deleteTransferRecordById(id: number) {
  return request.delete(`/transfer-records/${id}`)
}
