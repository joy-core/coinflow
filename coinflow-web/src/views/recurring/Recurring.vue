<template>
  <el-card>
    <template #header>
      <div class="flex justify-between items-center">
        <span>Recurring Transactions</span>
        <el-button type="primary" @click="openDialog">Add Recurring Transaction</el-button>
      </div>
    </template>

    <el-table :data="recurringItems" stripe>
      <el-table-column prop="name" label="Name" />
      <el-table-column prop="type" label="Type">
        <template #default="scope">
          {{ scope.row.type === 'INCOME' ? 'Income' : scope.row.type === 'EXPENSE' ? 'Expense' : 'Transfer' }}
        </template>
      </el-table-column>
      <el-table-column prop="categoryName" label="Category" />
      <el-table-column prop="amount" label="Amount" />
      <el-table-column prop="frequency" label="Frequency">
        <template #default="scope">
          {{ getFrequencyText(scope.row.frequency) }}
        </template>
      </el-table-column>
      <el-table-column prop="nextRun" label="Next Run" />
      <el-table-column label="Status">
        <template #default="scope">
          <el-switch 
            v-model="scope.row.status" 
            @change="toggleStatus(scope.row.id, scope.row.status)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteRecurring(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="周期记账编辑">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type" @change="handleTypeChange">
            <el-radio label="EXPENSE">支出</el-radio>
            <el-radio label="INCOME">收入</el-radio>
            <el-radio label="TRANSFER">转账</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分类" v-if="form.type !== 'TRANSFER'">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="category in filteredCategories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="form.amount" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="账户" v-if="form.type !== 'TRANSFER'">
          <el-select v-model="form.accountId" placeholder="选择账户">
            <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
          </el-select>
        </el-form-item>
        <template v-if="form.type === 'TRANSFER'">
          <el-form-item label="转出账户">
            <el-select v-model="form.fromAccountId" placeholder="选择转出账户">
              <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="转入账户">
            <el-select v-model="form.toAccountId" placeholder="选择转入账户">
              <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
            </el-select>
          </el-form-item>
        </template>
        <el-form-item label="频率">
          <el-select v-model="form.frequency" placeholder="选择频率">
            <el-option label="每天" value="DAILY" />
            <el-option label="每周" value="WEEKLY" />
            <el-option label="每月" value="MONTHLY" />
            <el-option label="每年" value="YEARLY" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="date" placeholder="选择开始日期" />
        </el-form-item>
        <el-form-item label="结束日期" required>
          <el-date-picker v-model="form.endDate" type="date" placeholder="选择结束日期" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecurring">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listRecurring,
  addRecurring,
  updateRecurring,
  deleteRecurringById,
  toggleRecurringStatus
} from '@/services/recurring'
import { listCategories } from '@/services/category'
import { listAccounts } from '@/services/account'

const recurringItems = ref([])
const categories = ref([])
const accounts = ref([])
const dialogVisible = ref(false)

const form = ref({
  id: null,
  name: '',
  type: 'EXPENSE',
  categoryId: '',
  amount: 0,
  accountId: '',
  fromAccountId: '',
  toAccountId: '',
  frequency: 'MONTHLY',
  startDate: new Date(),
  endDate: new Date(),
  description: '',
  status: true,
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
  const res = await listRecurring(1) // 传入当前 userId
  recurringItems.value = res.data
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
    if (row.startDate) {
      form.value.startDate = new Date(row.startDate)
    }
    if (row.endDate) {
      form.value.endDate = new Date(row.endDate)
    }
  } else {
    form.value = {
      id: null,
      name: '',
      type: 'EXPENSE',
      categoryId: '',
      amount: 0,
      accountId: '',
      fromAccountId: '',
      toAccountId: '',
      frequency: 'MONTHLY',
      startDate: new Date(),
      endDate: new Date(),
      description: '',
      status: true,
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

const saveRecurring = async () => {
  if (form.value.id) {
    await updateRecurring(form.value)
  } else {
    form.value.userId = 1
    await addRecurring(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  await loadData()
}

const deleteRecurring = async (id) => {
  await deleteRecurringById(id)
  ElMessage.success('已删除')
  await loadData()
}

const toggleStatus = async (id, status) => {
  await toggleRecurringStatus(id, status)
  ElMessage.success('状态已更新')
}

const getFrequencyText = (frequency) => {
  const map = {
    DAILY: '每天',
    WEEKLY: '每周',
    MONTHLY: '每月',
    YEARLY: '每年'
  }
  return map[frequency] || frequency
}

onMounted(async () => {
  await loadCategories()
  await loadAccounts()
  await loadData()
})
</script>
