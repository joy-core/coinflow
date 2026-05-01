import request from '@/utils/request'

export function listAccounts(userId: number) {
    return request.get('/accounts', { params: { userId } })
}

export function addAccount(data: any) {
    return request.post('/accounts', data)
}

export function updateAccount(data: any) {
    return request.put('/accounts', data)
}

export function deleteAccountById(id: number) {
    return request.delete(`/accounts/${id}`)
}
