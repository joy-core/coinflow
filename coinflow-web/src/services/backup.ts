import request from '@/utils/request'

export function exportBackup(userId: number) {
    return request.get('/backup/export', { params: { userId } })
}

export function importBackup(data: object, userId: number) {
    return request.post('/backup/import', data, { params: { userId } })
}
