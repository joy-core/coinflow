import request from '@/utils/request'

export function listTemplates(userId: number) {
    return request.get('/templates', { params: { userId } })
}

export function addTemplate(data: any) {
    return request.post('/templates', data)
}

export function updateTemplate(data: any) {
    return request.put('/templates', data)
}

export function deleteTemplateById(id: number) {
    return request.delete(`/templates/${id}`)
}

export function useTemplate(id: number) {
    return request.post(`/templates/${id}/use`)
}
