<template>
  <div class="transfer-records-container">
    <el-card class="records-card">
      <template #header>
        <div class="card-header">
          <h2 class="card-title">Transfer Records</h2>
          <el-button type="primary" @click="openDialog" class="add-button">
            <el-icon><Plus /></el-icon>
            <span>New Transfer</span>
          </el-button>
        </div>
      </template>

      <el-table :data="records" class="records-table">
        <el-table-column prop="transferredAt" label="Date" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.transferredAt) }}
          </template>
        </el-table-column>
        <el-table-column label="From" width="150">
          <template #default="scope">
            {{ getAccountName(scope.row.fromAccountId) }}
          </template>
        </el-table-column>
        <el-table-column label="To" width="150">
          <template #default="scope">
            {{ getAccountName(scope.row.toAccountId) }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="Amount" width="120">
          <template #default="scope">
            <span class="amount-text">{{ Number(scope.row.amount).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="fee" label="Fee" width="100">
          <template #default="scope">
            {{ scope.row.fee ? Number(scope.row.fee).toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="Remark" />
        <el-table-column label="Actions" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openDialog(scope.row)">Edit</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">Delete</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="dialogVisible" title="Transfer" width="500px">
        <el-form :model="form" label-width="120px">
          <el-form-item label="Date">
            <el-date-picker v-model="form.transferredAt" type="datetime" placeholder="Select date" style="width: 100%" />
          </el-form-item>
          <el-form-item label="From Account">
            <el-select v-model="form.fromAccountId" placeholder="Select source account" style="width: 100%">
              <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="To Account">
            <el-select v-model="form.toAccountId" placeholder="Select destination account" style="width: 100%">
              <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Amount">
            <el-input-number v-model="form.amount" :precision="2" :step="0.01" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Fee">
            <el-input-number v-model="form.fee" :precision="2" :step="0.01" :min="0" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Remark">
            <el-input v-model="form.remark" type="textarea" :rows="2" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="handleSave">Save</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  listTransferRecords,
  addTransferRecord,
  updateTransferRecord,
  deleteTransferRecordById
} from '@/services/transferRecord'
import { listAccounts } from '@/services/account'

const records = ref([])
const accounts = ref([])
const dialogVisible = ref(false)
const userId = 1

const form = ref({
  id: null,
  userId,
  accountBookId: null,
  fromAccountId: null,
  toAccountId: null,
  amount: 0,
  fee: 0,
  remark: '',
  transferredAt: new Date()
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString().slice(0, 5)
}

const getAccountName = (accountId) => {
  const acc = accounts.value.find(a => a.id === accountId)
  return acc ? acc.name : '-'
}

const loadData = async () => {
  const res = await listTransferRecords(userId)
  records.value = res.data
}

const loadAccounts = async () => {
  const res = await listAccounts(userId)
  accounts.value = res.data
}

const openDialog = (row = null) => {
  dialogVisible.value = true
  if (row) {
    form.value = { ...row, transferredAt: row.transferredAt ? new Date(row.transferredAt) : new Date() }
  } else {
    form.value = {
      id: null,
      userId,
      accountBookId: null,
      fromAccountId: null,
      toAccountId: null,
      amount: 0,
      fee: 0,
      remark: '',
      transferredAt: new Date()
    }
  }
}

const handleSave = async () => {
  if (!form.value.fromAccountId || !form.value.toAccountId) {
    ElMessage.error('Please select both accounts')
    return
  }
  if (form.value.id) {
    await updateTransferRecord(form.value)
  } else {
    form.value.userId = userId
    await addTransferRecord(form.value)
  }
  ElMessage.success('Saved')
  dialogVisible.value = false
  await loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Are you sure?', 'Confirm')
  await deleteTransferRecordById(id)
  ElMessage.success('Deleted')
  await loadData()
}

onMounted(async () => {
  await loadAccounts()
  await loadData()
})
</script>

<style scoped>
.transfer-records-container {
  animation: fadeIn 0.6s ease;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.add-button {
  border-radius: 8px;
}

.records-table {
  margin-top: 16px;
}

.amount-text {
  font-weight: 600;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
