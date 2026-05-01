<template>
  <el-card>
    <template #header>
      <div class="flex justify-between items-center">
        <span>模板管理</span>
        <el-button type="primary" @click="openDialog">新增模板</el-button>
      </div>
    </template>

    <el-table :data="templates" stripe>
      <el-table-column prop="name" label="模板名称" />
      <el-table-column prop="type" label="类型">
        <template #default="scope">
          {{ scope.row.type === 'INCOME' ? '收入' : scope.row.type === 'EXPENSE' ? '支出' : '转账' }}
        </template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="accountName" label="账户" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleUseTemplate(scope.row.id)">使用</el-button>
          <el-button type="success" size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteTemplate(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="模板编辑">
      <el-form :model="form" label-width="80px">
        <el-form-item label="模板名称">
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
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTemplate">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listTemplates,
  addTemplate,
  updateTemplate,
  deleteTemplateById,
  useTemplate
} from '@/services/template'
import { listCategories } from '@/services/category'
import { listAccounts } from '@/services/account'

const templates = ref([])
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
  const res = await listTemplates(1) // 传入当前 userId
  templates.value = res.data
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

const saveTemplate = async () => {
  if (form.value.id) {
    await updateTemplate(form.value)
  } else {
    form.value.userId = 1
    await addTemplate(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  await loadData()
}

const deleteTemplate = async (id) => {
  await deleteTemplateById(id)
  ElMessage.success('已删除')
  await loadData()
}

const handleUseTemplate = async (id) => {
  await useTemplate(id)
  ElMessage.success('模板使用成功')
  // 可以跳转到账单页面或刷新账单列表
}

onMounted(async () => {
  await loadCategories()
  await loadAccounts()
  await loadData()
})
</script>
