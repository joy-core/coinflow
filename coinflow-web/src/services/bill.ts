import request from '@/utils/request'

export function listBills(params: any) {
    return request.get('/bills', { params })
}

export function addBill(data: any) {
    return request.post('/bills', data)
}

export function updateBill(data: any) {
    return request.put('/bills', data)
}

export function deleteBillById(id: number) {
    return request.delete(`/bills/${id}`)
}

export function getBillStatistics(params: any) {
    return request.get('/bills/statistics', { params })
}
