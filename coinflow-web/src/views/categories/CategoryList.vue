<template>
  <el-card>
    <template #header>
      <div class="flex justify-between items-center">
        <span>分类列表</span>
        <div class="flex gap-2">
          <el-button type="success" @click="exportTemplate">导出模板</el-button>
          <el-button type="warning" @click="openImportDialog">导入分类</el-button>
          <el-button type="primary" @click="openDialog()">新增</el-button>
        </div>
      </div>
    </template>

    <el-table :data="categories" stripe>
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="icon" label="图标" />
      <el-table-column prop="sortOrder" label="排序" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteCategory(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="分类编辑">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="收入" value="INCOME" />
            <el-option label="支出" value="EXPENSE" />
          </el-select>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>

    <!-- 导入分类对话框 -->
    <el-dialog v-model="importDialogVisible" title="导入分类">
      <el-upload
        class="upload-demo"
        action=""
        :auto-upload="false"
        :on-change="handleFileChange"
        :show-file-list="false"
        accept=".xlsx,.xls"
      >
        <el-button type="primary">选择Excel文件</el-button>
      </el-upload>
      <div v-if="selectedFile" class="mt-2">
        已选择文件: {{ selectedFile.name }}
      </div>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="importCategories" :disabled="!selectedFile">导入</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listCategories,
  addCategory,
  updateCategory,
  deleteCategoryById,
  exportCategoryTemplate,
  importCategories as importCategoriesApi
} from '@/services/category'

const categories = ref([])
const dialogVisible = ref(false)
const importDialogVisible = ref(false)
const selectedFile = ref(null)
const form = ref({ id: null, name: '', type: '', icon: '', sortOrder: 0 })

const loadData = async () => {
  const res = await listCategories(1) // 传入当前 userId
  categories.value = res.data
}

const openDialog = (row = null) => {
  dialogVisible.value = true
  form.value = row ? { ...row } : { id: null, name: '', type: '', icon: '', sortOrder: 0 }
}

const saveCategory = async () => {
  if (form.value.id) {
    await updateCategory(form.value)
  } else {
    form.value.userId = 1
    await addCategory(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  await loadData()
}

const deleteCategory = async (id) => {
  await deleteCategoryById(id)
  ElMessage.success('已删除')
  await loadData()
}

// 打开导入对话框
const openImportDialog = () => {
  selectedFile.value = null
  importDialogVisible.value = true
}

// 处理文件选择
const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

// 导出模板
const exportTemplate = async () => {
  try {
    const res = await exportCategoryTemplate()
    const blob = new Blob([res.data], { type: 'application/vnd.ms-excel' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'category-template.xlsx'
    document.body.appendChild(a) // 添加到DOM
    a.click()
    URL.revokeObjectURL(url)
    // 确保元素存在再移除
    if (document.body.contains(a)) {
      document.body.removeChild(a) // 从DOM中移除
    }
    ElMessage.success('模板导出成功')
  } catch (error) {
    console.error('导出模板失败:', error)
    ElMessage.error('导出模板失败')
  }
}

// 导入分类
const importCategories = async () => {
  if (!selectedFile.value) return
  await importCategoriesApi(selectedFile.value, 1) // 传入当前 userId
  ElMessage.success('分类导入成功')
  importDialogVisible.value = false
  await loadData()
}

onMounted(() => {
  loadData()
})
</script>
