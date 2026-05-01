<template>
  <el-card>
    <template #header>
      <div class="flex justify-between items-center">
        <span>账户列表</span>
        <el-button type="primary" @click="openDialog">新增</el-button>
      </div>
    </template>

    <el-table :data="accounts" stripe>
      <el-table-column prop="name" label="账户名称" />
      <el-table-column prop="type" label="账户类型" />
      <el-table-column prop="balance" label="余额" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteAccount(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="账户编辑">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账户名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="账户类型">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option label="现金" value="CASH" />
            <el-option label="银行卡" value="BANK" />
            <el-option label="支付宝" value="ALIPAY" />
            <el-option label="微信" value="WECHAT" />
            <el-option label="信用卡" value="CREDIT_CARD" />
          </el-select>
        </el-form-item>
        <el-form-item label="余额">
          <el-input-number v-model="form.balance" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAccount">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listAccounts,
  addAccount,
  updateAccount,
  deleteAccountById
} from '@/services/account'

const accounts = ref([])
const dialogVisible = ref(false)
const form = ref({ id: null, name: '', type: '', balance: 0, description: '', userId: 1 })

const loadData = async () => {
  const res = await listAccounts(1) // 传入当前 userId
  accounts.value = res.data
}

const openDialog = (row = null) => {
  dialogVisible.value = true
  form.value = row ? { ...row } : { id: null, name: '', type: '', balance: 0, description: '', userId: 1 }
}

const saveAccount = async () => {
  if (form.value.id) {
    await updateAccount(form.value)
  } else {
    form.value.userId = 1
    await addAccount(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  await loadData()
}

const deleteAccount = async (id) => {
  await deleteAccountById(id)
  ElMessage.success('已删除')
  await loadData()
}

onMounted(() => {
  loadData()
})
</script>
