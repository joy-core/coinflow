import request from '@/utils/request'

export function listAccountBooks(userId: number) {
    return request.get('/account-books', { params: { userId } })
}

export function getAccountBook(id: number) {
    return request.get(`/account-books/${id}`)
}

export function addAccountBook(data: any) {
    return request.post('/account-books', data)
}

export function updateAccountBook(data: any) {
    return request.put('/account-books', data)
}

export function deleteAccountBookById(id: number) {
    return request.delete(`/account-books/${id}`)
}

export function setDefaultAccountBook(id: number, userId: number) {
    return request.put(`/account-books/${id}/default`, null, { params: { userId } })
}