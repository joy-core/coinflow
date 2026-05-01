import request from '@/utils/request'

export function listCategories(userId: number) {
    return request.get('/categories', { params: { userId } })
}

export function addCategory(data: any) {
    return request.post('/categories', data)
}

export function updateCategory(data: any) {
    return request.put('/categories', data)
}

export function deleteCategoryById(id: number) {
    return request.delete(`/categories/${id}`)
}

// Export Excel template
export function exportCategoryTemplate() {
    return request.get('/categories/export-template', { responseType: 'blob' })
}

// Import categories
export function importCategories(file: File, userId: number) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId.toString())
    return request.post('/categories/import', formData)
}
