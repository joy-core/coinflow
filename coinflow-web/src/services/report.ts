import request from '@/utils/request'

export function getOverview(params: any) {
    return request.get('/reports/overview', { params })
}

export function getCategoryStats(params: any) {
    return request.get('/reports/category', { params })
}

export function getMonthlyStats(params: any) {
    return request.get('/reports/monthly', { params })
}

export function getCustomReport(params: any) {
    return request.get('/reports/custom', { params })
}
