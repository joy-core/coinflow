<template>
  <el-card>
    <template #header>
      <div class="flex justify-between items-center">
        <span>预算管理</span>
        <el-button type="primary" @click="openDialog">新增预算</el-button>
      </div>
    </template>

    <div class="mb-4">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="selectedMonth" placeholder="选择月份" @change="loadData">
            <el-option v-for="month in months" :key="month.value" :label="month.label" :value="month.value" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="categoryId" placeholder="选择分类" @change="loadData">
            <el-option label="全部" value="" />
            <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
        </el-col>
      </el-row>
    </div>

    <el-table :data="budgets" stripe>
      <el-table-column prop="categoryName" label="分类" />
      <el-table-column prop="amount" label="预算金额" />
      <el-table-column prop="spent" label="已花费" />
      <el-table-column label="预算使用情况">
        <template #default="scope">
          <el-progress 
            :percentage="Math.min((scope.row.spent / scope.row.amount) * 100, 100)" 
            :color="getProgressColor(scope.row.spent / scope.row.amount)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteBudget(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="预算编辑">
      <el-form :model="form" label-width="80px">
        <el-form-item label="月份">
          <el-date-picker v-model="form.month" type="month" placeholder="选择月份" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预算金额">
          <el-input-number v-model="form.amount" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="是否为二级预算">
          <el-switch v-model="form.isSubBudget" />
        </el-form-item>
        <el-form-item label="父级预算" v-if="form.isSubBudget">
          <el-select v-model="form.parentBudgetId" placeholder="选择父级预算">
            <el-option v-for="budget in parentBudgets" :key="budget.id" :label="budget.categoryName" :value="budget.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBudget">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listBudgets,
  addBudget,
  updateBudget,
  deleteBudgetById
} from '@/services/budget'
import { listCategories } from '@/services/category'

const budgets = ref([])
const categories = ref([])
const parentBudgets = ref([])
const dialogVisible = ref(false)
const selectedMonth = ref(new Date().toISOString().substring(0, 7))
const categoryId = ref('')

const form = ref({
  id: null,
  month: new Date(),
  categoryId: '',
  amount: 0,
  isSubBudget: false,
  parentBudgetId: null,
  userId: 1
})

const months = computed(() => {
  const result = []
  const now = new Date()
  for (let i = 11; i >= 0; i--) {
    const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
    result.push({
      label: date.toLocaleString('zh-CN', { year: 'numeric', month: 'long' }),
      value: date.toISOString().substring(0, 7)
    })
  }
  return result
})

const loadData = async () => {
  const params = {
    userId: 1,
    month: selectedMonth.value,
    categoryId: categoryId.value
  }
  const res = await listBudgets(params)
  budgets.value = res.data
  
  // 加载父级预算选项
  parentBudgets.value = res.data.filter(b => !b.isSubBudget)
}

const loadCategories = async () => {
  const res = await listCategories(1)
  categories.value = res.data.filter(c => c.type === 'EXPENSE') // 预算只针对支出分类
}

const openDialog = (row = null) => {
  dialogVisible.value = true
  if (row) {
    form.value = { ...row }
    if (row.month) {
      form.value.month = new Date(row.month)
    }
  } else {
    form.value = {
      id: null,
      month: new Date(),
      categoryId: '',
      amount: 0,
      isSubBudget: false,
      parentBudgetId: null,
      userId: 1
    }
  }
}

const saveBudget = async () => {
  if (form.value.id) {
    await updateBudget(form.value)
  } else {
    form.value.userId = 1
    await addBudget(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  await loadData()
}

const deleteBudget = async (id) => {
  await deleteBudgetById(id)
  ElMessage.success('已删除')
  await loadData()
}

const getProgressColor = (ratio) => {
  if (ratio < 0.7) return '#67c23a'
  if (ratio < 0.9) return '#e6a23c'
  return '#f56c6c'
}

onMounted(async () => {
  await loadCategories()
  await loadData()
})
</script>

<style scoped>
.mb-4 {
  margin-bottom: 16px;
}
</style>
