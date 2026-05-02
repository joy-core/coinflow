<template>
  <el-card>
    <template #header>
      <div class="text-lg font-bold">System Settings</div>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="General">
        <el-form :model="settings" label-width="100px">
          <el-form-item label="Theme">
            <el-select v-model="settings.theme" @change="changeTheme">
              <el-option v-for="theme in themes" :key="theme.value" :label="theme.label" :value="theme.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="Language">
            <el-select v-model="settings.language">
              <el-option label="Simplified Chinese" value="zh-CN" />
              <el-option label="English" value="en-US" />
            </el-select>
          </el-form-item>
          <el-form-item label="Default Category">
            <el-select v-model="settings.defaultCategory">
              <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="Default Account">
            <el-select v-model="settings.defaultAccount">
              <el-option v-for="account in accounts" :key="account.id" :label="account.name" :value="account.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveSettings">Save Settings</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="User Info">
        <el-form :model="userInfo" label-width="100px">
          <el-form-item label="Username">
            <el-input v-model="userInfo.username" />
          </el-form-item>
          <el-form-item label="Email">
            <el-input v-model="userInfo.email" />
          </el-form-item>
          <el-form-item label="Change Password">
            <el-button type="primary" @click="openChangePasswordDialog">Change Password</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateUserInfo">Update Info</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="changePasswordDialogVisible" title="Change Password">
      <el-form :model="passwordForm" label-width="100px">
        <el-form-item label="Old Password">
          <el-input type="password" v-model="passwordForm.oldPassword" />
        </el-form-item>
        <el-form-item label="New Password">
          <el-input type="password" v-model="passwordForm.newPassword" />
        </el-form-item>
        <el-form-item label="Confirm New Password">
          <el-input type="password" v-model="passwordForm.confirmPassword" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePasswordDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="changePassword">Confirm</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { themes, getCurrentTheme, setTheme } from '@/services/theme'
import { listCategories } from '@/services/category'
import { listAccounts } from '@/services/account'

const activeTab = ref('0')
const categories = ref([])
const accounts = ref([])
const changePasswordDialogVisible = ref(false)

const settings = ref({
  theme: getCurrentTheme(),
  language: 'zh-CN',
  defaultCategory: '',
  defaultAccount: ''
})

const userInfo = ref({
  username: '',
  email: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadData = async () => {
  const categoriesRes = await listCategories(1)
  categories.value = categoriesRes.data
  
  const accountsRes = await listAccounts(1)
  accounts.value = accountsRes.data
  
  // Load user info
  // This should be fetched from API, using mock data for now
  userInfo.value = {
    username: 'test',
    email: 'test@example.com'
  }
}

const changeTheme = (theme) => {
  setTheme(theme)
  ElMessage.success('Theme switched successfully')
}

const saveSettings = () => {
  // This should call API to save settings
  ElMessage.success('Settings saved')
}

const updateUserInfo = () => {
  // This should call API to update user info
  ElMessage.success('User info updated')
}

const openChangePasswordDialog = () => {
  changePasswordDialogVisible.value = true
}

const changePassword = () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('Passwords do not match')
    return
  }
  // This should call API to change password
  ElMessage.success('Password changed')
  changePasswordDialogVisible.value = false
}

onMounted(() => {
  loadData()
})
</script>
