<template>
  <div class="bills-container">
    <el-card class="bills-card">
      <template #header>
        <div class="card-header">
          <h2 class="card-title">Bill Management</h2>
          <el-button type="primary" @click="openDialog" class="add-bill-button">
            <el-icon><Plus /></el-icon>
            <span>Add Entry</span>
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" class="bills-tabs">
        <el-tab-pane label="Table View" name="table">
          <div class="filter-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="to"
              start-placeholder="Start date"
              end-placeholder="End date"
              @change="loadData"
              class="filter-item"
            />
          </el-col>
          <el-col :span="6">
            <el-select v-model="billType" placeholder="Bill Type" @change="loadData" class="filter-item">
              <el-option label="All" value="" />
              <el-option label="Expense" value="EXPENSE" />
              <el-option label="Income" value="INCOME" />
              <el-option label="Transfer" value="TRANSFER" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="categoryId" placeholder="Category" @change="loadData" class="filter-item">
              <el-option label="All" value="" />
              <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-input v-model="keyword" placeholder="Search" @keyup.enter="loadData" class="filter-item">
              <template #append>
                <el-button @click="loadData" class="search-button">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </el-col>
        </el-row>
      </div>

      <el-table :data="bills" class="bills-table">
        <el-table-column prop="date" label="Date" width="120" />
        <el-table-column label="Type" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'INCOME' ? 'success' : scope.row.type === 'EXPENSE' ? 'danger' : 'warning'" class="type-tag">
              {{ scope.row.type === 'INCOME' ? 'Income' : scope.row.type === 'EXPENSE' ? 'Expense' : 'Transfer' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="Category" />
        <el-table-column prop="amount" label="Amount" width="120">
          <template #default="scope">
            <span :class="scope.row.type === 'INCOME' ? 'text-success' : 'text-danger'" class="amount-text">
              {{ scope.row.type === 'INCOME' ? '+' : '-' }}{{ scope.row.amount.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="accountName" label="Account" width="120" />
        <el-table-column prop="description" label="Description" />
        <el-table-column label="Actions" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openDialog(scope.row)" class="action-button edit-button">
              Edit
            </el-button>
            <el-button type="danger" size="small" @click="deleteBill(scope.row.id)" class="action-button delete-button">
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>
        </el-tab-pane>
        <el-tab-pane label="Calendar View" name="calendar">
          <BillsCalendar
            :bills="bills"
            :categories="categories"
            :accounts="accounts"
            @edit-bill="openDialog"
            @add-bill-on-date="openDialogForDate"
            @month-change="handleMonthChange"
          />
        </el-tab-pane>
      </el-tabs>

      <el-dialog v-model="dialogVisible" title="Add Entry" width="500px" class="bill-dialog">
        <el-form :model="form" label-width="100px" class="bill-form">
          <el-form-item label="Date">
            <el-date-picker v-model="form.date" type="date" placeholder="Select date" class="form-item" />
          </el-form-item>
          <el-form-item label="Type">
            <el-radio-group v-model="form.type" @change="handleTypeChange" class="radio-group">
              <el-radio label="EXPENSE" class="radio-item">Expense</el-radio>
              <el-radio label="INCOME" class="radio-item">Income</el-radio>
              <el-radio label="TRANSFER" class="radio-item">Transfer</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="Category" v-if="form.type !== 'TRANSFER'">
            <el-select v-model="form.categoryId" placeholder="Select category" class="form-item">
              <el-option v-for="category in filteredCategories" :key="category.id" :label="category.name" :value="category.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Amount">
            <el-input-number v-model="form.amount" :precision="2" :step="0.01" class="form-item" style="width: 100%" />
          </el-form-item>
          <el-form-item label="Account" v-if="form.type !== 'TRANSFER'">
            <el-select v-model="form.accountId" placeholder="Select account" class="form-item">
              <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
            </el-select>
          </el-form-item>
          <template v-if="form.type === 'TRANSFER'">
            <el-form-item label="From Account">
              <el-select v-model="form.fromAccountId" placeholder="Select source account" class="form-item">
                <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="To Account">
              <el-select v-model="form.toAccountId" placeholder="Select destination account" class="form-item">
                <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
              </el-select>
            </el-form-item>
          </template>
          <el-form-item label="Description">
            <el-input type="textarea" v-model="form.description" class="form-item" :rows="3" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false" class="dialog-button cancel-button">Cancel</el-button>
          <el-button type="primary" @click="saveBill" class="dialog-button save-button">Save</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listBills,
  addBill,
  updateBill,
  deleteBillById
} from '@/services/bill'
import { listCategories } from '@/services/category'
import { listAccounts } from '@/services/account'
import { Plus, Search } from '@element-plus/icons-vue'
import BillsCalendar from './BillsCalendar.vue'

const bills = ref([])
const categories = ref([])
const accounts = ref([])
const dialogVisible = ref(false)
const dateRange = ref([])
const billType = ref('')
const categoryId = ref('')
const keyword = ref('')
const activeTab = ref('table')

const form = ref({
  id: null,
  date: new Date(),
  type: 'EXPENSE',
  categoryId: '',
  amount: 0,
  accountId: '',
  fromAccountId: '',
  toAccountId: '',
  description: '',
  userId: 1
})

const filteredCategories = computed(() => {
  if (form.value.type === 'INCOME') {
    return categories.value.filter(c => c.type === 'INCOME')
  } else if (form.value.type === 'EXPENSE') {
    return categories.value.filter(c => c.type === 'EXPENSE')
  }
  return categories.value
})

const loadData = async () => {
  const params = {
    userId: 1,
    startDate: dateRange.value[0] ? dateRange.value[0].toISOString() : '',
    endDate: dateRange.value[1] ? dateRange.value[1].toISOString() : '',
    type: billType.value,
    categoryId: categoryId.value,
    keyword: keyword.value
  }
  const res = await listBills(params)
  bills.value = res.data
}

const loadCategories = async () => {
  const res = await listCategories(1)
  categories.value = res.data
}

const loadAccounts = async () => {
  const res = await listAccounts(1)
  accounts.value = res.data
}

const openDialog = (row = null) => {
  dialogVisible.value = true
  if (row) {
    form.value = { ...row }
    form.value.date = new Date(row.happenedAt || row.date)
  } else {
    form.value = {
      id: null,
      date: new Date(),
      type: 'EXPENSE',
      categoryId: '',
      amount: 0,
      accountId: '',
      fromAccountId: '',
      toAccountId: '',
      description: '',
      userId: 1
    }
  }
}

const handleTypeChange = () => {
  form.value.categoryId = ''
  form.value.accountId = ''
  form.value.fromAccountId = ''
  form.value.toAccountId = ''
}

const openDialogForDate = (date) => {
  form.value = {
    id: null,
    date: date ? new Date(date) : new Date(),
    type: 'EXPENSE',
    categoryId: '',
    amount: 0,
    accountId: '',
    fromAccountId: '',
    toAccountId: '',
    description: '',
    userId: 1
  }
  dialogVisible.value = true
}

const handleMonthChange = ({ year, month }) => {
  const start = new Date(year, month, 1)
  const end = new Date(year, month + 1, 0)
  dateRange.value = [start, end]
  loadData()
}

const saveBill = async () => {
  if (form.value.id) {
    await updateBill(form.value)
  } else {
    form.value.userId = 1
    await addBill(form.value)
  }
  ElMessage.success('Operation successful')
  dialogVisible.value = false
  await loadData()
}

const deleteBill = async (id) => {
  await deleteBillById(id)
  ElMessage.success('Deleted')
  await loadData()
}

onMounted(async () => {
  await loadCategories()
  await loadAccounts()
  await loadData()
})
</script>

<style scoped>
.bills-container {
  padding: 0;
  min-height: 100%;
  animation: fadeIn 0.6s ease;
}

.bills-card {
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid var(--border-light);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-light);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.add-bill-button {
  display: flex;
  align-items: center;
  gap: 6px;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-light) 100%);
  border: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.add-bill-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
  background: linear-gradient(135deg, var(--primary-hover) 0%, var(--primary-color) 100%);
}

.bills-tabs {
  padding: 0;
}

.filter-section {
  padding: 24px;
  border-bottom: 1px solid var(--border-light);
  background-color: var(--bg-secondary);
}

.filter-item {
  width: 100%;
  border-radius: 8px;
  border: 1px solid var(--border-light);
  transition: all 0.3s ease;
}

.filter-item:hover {
  border-color: var(--primary-color);
}

.search-button {
  border-radius: 0 8px 8px 0;
  border-left: none;
  background-color: var(--bg-tertiary);
  transition: all 0.3s ease;
}

.search-button:hover {
  background-color: var(--primary-color);
  color: white;
}

.bills-table {
  border-radius: 8px;
  overflow: hidden;
}

.bills-table th {
  background-color: var(--bg-secondary);
  border-bottom: 1px solid var(--border-light);
  font-weight: 600;
  color: var(--text-secondary);
  padding: 16px;
}

.bills-table td {
  border-bottom: 1px solid var(--border-light);
  padding: 16px;
  color: var(--text-primary);
}

.bills-table tr:hover {
  background-color: var(--bg-secondary);
}

.type-tag {
  border-radius: 12px;
  font-size: 12px;
  padding: 2px 8px;
  font-weight: 500;
}

.amount-text {
  font-weight: 600;
  font-size: 14px;
}

.text-success {
  color: var(--success-color);
}

.text-danger {
  color: var(--danger-color);
}

.action-button {
  border-radius: 6px;
  font-size: 12px;
  padding: 4px 12px;
  transition: all 0.3s ease;
  margin-right: 8px;
}

.edit-button {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.edit-button:hover {
  background-color: var(--primary-hover);
  border-color: var(--primary-hover);
  transform: translateY(-1px);
}

.delete-button {
  background-color: var(--danger-color);
  border-color: var(--danger-color);
}

.delete-button:hover {
  background-color: #dc2626;
  border-color: #dc2626;
  transform: translateY(-1px);
}

.bill-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.bill-form {
  padding: 20px 0;
}

.form-item {
  width: 100%;
  border-radius: 8px;
  border: 1px solid var(--border-light);
  transition: all 0.3s ease;
}

.form-item:hover {
  border-color: var(--primary-color);
}

.radio-group {
  display: flex;
  gap: 20px;
}

.radio-item {
  font-size: 14px;
  color: var(--text-primary);
}

.dialog-button {
  border-radius: 8px;
  padding: 8px 20px;
  transition: all 0.3s ease;
}

.cancel-button {
  color: var(--text-secondary);
  border-color: var(--border-light);
}

.cancel-button:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.save-button {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.save-button:hover {
  background-color: var(--primary-hover);
  border-color: var(--primary-hover);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .filter-section {
    padding: 16px;
  }
  
  .card-header {
    padding: 16px 20px;
  }
  
  .card-title {
    font-size: 16px;
  }
  
  .add-bill-button span {
    display: none;
  }
  
  .radio-group {
    flex-direction: column;
    gap: 10px;
  }
  
  .bills-table th,
  .bills-table td {
    padding: 12px;
  }
  
  .action-button {
    font-size: 11px;
    padding: 3px 8px;
    margin-right: 4px;
  }
  
  .bill-dialog {
    width: 90% !important;
  }
}
</style>
