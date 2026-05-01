import request from '@/utils/request'

export function listBudgets(params: any) {
    return request.get('/budgets', { params })
}

export function addBudget(data: any) {
    return request.post('/budgets', data)
}

export function updateBudget(data: any) {
    return request.put('/budgets', data)
}

export function deleteBudgetById(id: number) {
    return request.delete(`/budgets/${id}`)
}

export function getBudgetStatus(params: any) {
    return request.get('/budgets/status', { params })
}
