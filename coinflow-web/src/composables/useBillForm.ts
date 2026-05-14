import { ref } from 'vue'

export interface BillFormData {
  id: number | null
  date: Date
  type: 'EXPENSE' | 'INCOME' | 'TRANSFER'
  categoryId: string
  amount: number
  accountId: string
  fromAccountId: string
  toAccountId: string
  description: string
}

export interface BackendBillPayload {
  id?: number | null
  userId: number
  accountBookId: number
  happenedAt: Date
  type: string
  categoryId: number | null
  amount: number
  assetAccountId: number | null
  remark: string
}

export function useBillForm(userId: number = 1) {
  const form = ref<BillFormData>({
    id: null,
    date: new Date(),
    type: 'EXPENSE',
    categoryId: '',
    amount: 0,
    accountId: '',
    fromAccountId: '',
    toAccountId: '',
    description: ''
  })

  function mapToBackend(accountBookId: number): BackendBillPayload {
    return {
      id: form.value.id,
      userId,
      accountBookId,
      happenedAt: form.value.date,
      type: form.value.type,
      categoryId: form.value.categoryId ? Number(form.value.categoryId) : null,
      amount: form.value.amount,
      assetAccountId: form.value.accountId ? Number(form.value.accountId) : null,
      remark: form.value.description
    }
  }

  function mapFromBackend(row: any): Partial<BillFormData> {
    return {
      id: row.id || null,
      date: new Date(row.happenedAt || row.date),
      type: row.type || 'EXPENSE',
      categoryId: row.categoryId?.toString() || '',
      amount: row.amount || 0,
      accountId: row.assetAccountId?.toString() || row.accountId?.toString() || '',
      fromAccountId: row.fromAccountId?.toString() || '',
      toAccountId: row.toAccountId?.toString() || '',
      description: row.remark || row.description || ''
    }
  }

  function resetForm() {
    form.value = {
      id: null,
      date: new Date(),
      type: 'EXPENSE',
      categoryId: '',
      amount: 0,
      accountId: '',
      fromAccountId: '',
      toAccountId: '',
      description: ''
    }
  }

  return { form, mapToBackend, mapFromBackend, resetForm }
}
