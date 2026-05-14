<template>
  <el-button class="quick-add-fab" circle type="primary" size="large" @click="$emit('update:modelValue', true)">
    <el-icon :size="24"><Plus /></el-icon>
  </el-button>

  <el-dialog v-model="visible" title="Quick Add Bill" width="500px" @opened="onOpened">
    <el-form :model="form" label-width="100px">
      <el-form-item label="Date">
        <el-date-picker v-model="form.date" type="date" placeholder="Select date" style="width: 100%" />
      </el-form-item>
      <el-form-item label="Type">
        <el-radio-group v-model="form.type" @change="handleTypeChange">
          <el-radio label="EXPENSE">Expense</el-radio>
          <el-radio label="INCOME">Income</el-radio>
          <el-radio label="TRANSFER">Transfer</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="Category" v-if="form.type !== 'TRANSFER'">
        <el-select v-model="form.categoryId" placeholder="Select category" style="width: 100%">
          <el-option v-for="c in filteredCategories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="Amount">
        <el-input-number v-model="form.amount" :precision="2" :step="0.01" style="width: 100%" />
      </el-form-item>
      <el-form-item label="Account" v-if="form.type !== 'TRANSFER'">
        <el-select v-model="form.accountId" placeholder="Select account" style="width: 100%">
          <el-option v-for="a in accounts" :key="a.id" :label="a.name" :value="a.id" />
        </el-select>
      </el-form-item>
      <template v-if="form.type === 'TRANSFER'">
        <el-form-item label="From Account">
          <el-select v-model="form.fromAccountId" placeholder="Select source account" style="width: 100%">
            <el-option v-for="a in accounts" :key="a.id" :label="a.name" :value="a.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="To Account">
          <el-select v-model="form.toAccountId" placeholder="Select destination account" style="width: 100%">
            <el-option v-for="a in accounts" :key="a.id" :label="a.name" :value="a.id" />
          </el-select>
        </el-form-item>
      </template>
      <el-form-item label="Description">
        <el-input v-model="form.description" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" @click="handleSave">Save</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { addBill } from '@/services/bill'
import { listCategories } from '@/services/category'
import { listAccounts } from '@/services/account'
import { listAccountBooks } from '@/services/accountBook'
import { useBillForm } from '@/composables/useBillForm'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [boolean]; 'bill-added': [] }>()

const visible = computed({ get: () => props.modelValue, set: (v) => emit('update:modelValue', v) })
const categories = ref<any[]>([])
const accounts = ref<any[]>([])
const defaultAccountBookId = ref(1)
const userId = 1

const { form, mapToBackend, resetForm } = useBillForm(userId)

const filteredCategories = computed(() => {
  if (form.value.type === 'INCOME') return categories.value.filter(c => c.type === 'INCOME')
  if (form.value.type === 'EXPENSE') return categories.value.filter(c => c.type === 'EXPENSE')
  return categories.value
})

const onOpened = async () => {
  if (categories.value.length === 0) {
    const [catRes, accRes, bookRes] = await Promise.all([
      listCategories(userId),
      listAccounts(userId),
      listAccountBooks(userId)
    ])
    categories.value = catRes.data || []
    accounts.value = accRes.data || []
    const defaultBook = bookRes.data?.find((b: any) => b.isDefault) || bookRes.data?.[0]
    defaultAccountBookId.value = defaultBook?.id || 1
  }
}

const handleTypeChange = () => {
  form.value.categoryId = ''
  form.value.accountId = ''
  form.value.fromAccountId = ''
  form.value.toAccountId = ''
}

const handleSave = async () => {
  if (!form.value.amount || form.value.amount <= 0) {
    ElMessage.error('Please enter a valid amount')
    return
  }
  const payload = mapToBackend(defaultAccountBookId.value)
  await addBill(payload)
  ElMessage.success('Bill added')
  visible.value = false
  resetForm()
  emit('bill-added')
}
</script>

<style scoped>
.quick-add-fab {
  position: fixed;
  bottom: 32px;
  right: 32px;
  z-index: 2000;
  width: 56px;
  height: 56px;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.4);
  transition: transform 0.2s;
}

.quick-add-fab:hover {
  transform: scale(1.1);
}
</style>