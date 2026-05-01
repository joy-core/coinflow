<template>
  <el-card>
    <template #header>
      <div class="flex justify-between items-center">
        <span>账本管理</span>
        <el-button type="primary" @click="openDialog()">新建账本</el-button>
      </div>
    </template>

    <el-row :gutter="16" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="book in books" :key="book.id">
        <el-card shadow="hover" class="book-card" :body-style="{ padding: '20px' }">
          <div class="book-header" :style="{ borderLeftColor: book.color || '#4f46e5' }">
            <span class="book-icon">{{ book.icon || '📒' }}</span>
            <span class="book-name">{{ book.name }}</span>
          </div>
          <div class="book-tags">
            <el-tag v-if="book.isDefault" size="small" type="success">默认</el-tag>
            <el-tag v-if="book.isArchived" size="small" type="info">已归档</el-tag>
          </div>
          <div class="book-actions">
            <el-button size="small" @click="openDialog(book)">编辑</el-button>
            <el-button v-if="!book.isDefault" size="small" type="warning" @click="handleSetDefault(book)">设为默认</el-button>
            <el-button v-if="!book.isDefault" size="small" type="danger" @click="handleDelete(book)">删除</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && books.length === 0" description="暂无账本，请点击右上角新建" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑账本' : '新建账本'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账本名称">
          <el-input v-model="form.name" placeholder="请输入账本名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="输入 emoji 图标，如 📒" />
        </el-form-item>
        <el-form-item label="颜色">
          <el-color-picker v-model="form.color" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
        <el-form-item label="归档">
          <el-switch v-model="form.isArchived" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBook">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listAccountBooks,
  addAccountBook,
  updateAccountBook,
  deleteAccountBookById,
  setDefaultAccountBook
} from '@/services/accountBook'

const userId = 1

const books = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ id: null, name: '', icon: '📒', color: '#4f46e5', isDefault: false, isArchived: false, userId })

const loadData = async () => {
  loading.value = true
  try {
    const res = await listAccountBooks(userId)
    books.value = res.data
  } finally {
    loading.value = false
  }
}

const openDialog = (row = null) => {
  dialogVisible.value = true
  if (row) {
    form.value = { ...row }
  } else {
    form.value = { id: null, name: '', icon: '📒', color: '#4f46e5', isDefault: false, isArchived: false, userId }
  }
}

const saveBook = async () => {
  if (!form.value.name) {
    ElMessage.warning('请输入账本名称')
    return
  }
  form.value.userId = userId
  if (form.value.id) {
    await updateAccountBook(form.value)
  } else {
    await addAccountBook(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  await loadData()
}

const handleSetDefault = async (book) => {
  await setDefaultAccountBook(book.id, userId)
  ElMessage.success('已设为默认账本')
  await loadData()
}

const handleDelete = async (book) => {
  await ElMessageBox.confirm(`确认删除账本「${book.name}」？`, '提示', { type: 'warning' })
  await deleteAccountBookById(book.id)
  ElMessage.success('已删除')
  await loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.book-card {
  margin-bottom: 16px;
}
.book-header {
  display: flex;
  align-items: center;
  gap: 10px;
  border-left: 4px solid;
  padding-left: 12px;
  margin-bottom: 12px;
}
.book-icon {
  font-size: 24px;
}
.book-name {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}
.book-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.book-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>