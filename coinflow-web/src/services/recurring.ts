import request from '@/utils/request'

export function listRecurring(userId: number) {
    return request.get('/recurring', { params: { userId } })
}

export function addRecurring(data: any) {
    return request.post('/recurring', data)
}

export function updateRecurring(data: any) {
    return request.put('/recurring', data)
}

export function deleteRecurringById(id: number) {
    return request.delete(`/recurring/${id}`)
}

export function toggleRecurringStatus(id: number, status: boolean) {
    return request.put(`/recurring/${id}/status`, { status })
}
