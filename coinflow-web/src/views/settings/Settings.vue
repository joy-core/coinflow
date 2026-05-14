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
          <el-form-item label="Phone">
            <el-input v-model="userInfo.phone" />
          </el-form-item>
          <el-form-item label="Change Password">
            <el-button type="primary" @click="openChangePasswordDialog">Change Password</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateUserInfo">Update Info</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="Data Management">
        <el-form label-width="160px">
          <el-form-item label="Export Data">
            <el-button type="primary" @click="handleExport" :loading="exporting">
              <el-icon><Download /></el-icon>
              <span>Export All Data (JSON)</span>
            </el-button>
            <span class="form-tip">Download all bills, categories, accounts, and more as a JSON file.</span>
          </el-form-item>

          <el-form-item label="Import Data">
            <el-upload
              action=""
              :auto-upload="false"
              :show-file-list="true"
              :limit="1"
              accept=".json"
              :on-change="handleFileSelect"
            >
              <el-button type="warning">
                <el-icon><Upload /></el-icon>
                <span>Select JSON File</span>
              </el-button>
            </el-upload>
            <span class="form-tip">Import will merge data. Existing records with the same ID will be skipped.</span>
          </el-form-item>

          <el-form-item>
            <el-button type="danger" @click="handleImport" :loading="importing" :disabled="!selectedFile">
              Start Import
            </el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { themes, getCurrentTheme, setTheme } from '@/services/theme'
import { listCategories } from '@/services/category'
import { listAccounts } from '@/services/account'
import { getUserById, updateUser, changePassword as apiChangePassword } from '@/services/user'
import { exportBackup, importBackup } from '@/services/backup'
import { Download, Upload } from '@element-plus/icons-vue'

const activeTab = ref('0')
const categories = ref([])
const accounts = ref([])
const changePasswordDialogVisible = ref(false)
const userId = ref(1)

const settings = ref({
  theme: getCurrentTheme(),
  language: 'zh-CN',
  defaultCategory: '',
  defaultAccount: ''
})

const userInfo = ref({
  username: '',
  email: '',
  phone: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const exporting = ref(false)
const importing = ref(false)
const selectedFile = ref(null)

const getUserId = () => {
  try {
    const tokenStr = localStorage.getItem('token')
    if (!tokenStr) return 1
    const tokenObj = JSON.parse(tokenStr)
    return tokenObj?.userId || tokenObj?.id || 1
  } catch {
    return 1
  }
}

const loadData = async () => {
  userId.value = getUserId()

  const categoriesRes = await listCategories(userId.value)
  categories.value = categoriesRes.data

  const accountsRes = await listAccounts(userId.value)
  accounts.value = accountsRes.data

  try {
    const userRes = await getUserById(userId.value)
    if (userRes.data) {
      userInfo.value.username = userRes.data.username || ''
      userInfo.value.email = userRes.data.email || ''
      userInfo.value.phone = userRes.data.phone || ''
    }
  } catch {
    // User not found, keep empty
  }
}

const changeTheme = (theme) => {
  setTheme(theme)
  ElMessage.success('Theme switched successfully')
}

const saveSettings = async () => {
  // Settings like theme/language/default are stored locally for now
  localStorage.setItem('language', settings.value.language)
  localStorage.setItem('defaultCategory', settings.value.defaultCategory)
  localStorage.setItem('defaultAccount', settings.value.defaultAccount)
  ElMessage.success('Settings saved')
}

const updateUserInfo = async () => {
  await updateUser({
    id: userId.value,
    username: userInfo.value.username,
    email: userInfo.value.email,
    phone: userInfo.value.phone
  })
  ElMessage.success('User info updated')
}

const openChangePasswordDialog = () => {
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  changePasswordDialogVisible.value = true
}

const changePassword = async () => {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    ElMessage.error('Please fill in all password fields')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.error('Passwords do not match')
    return
  }
  if (passwordForm.value.newPassword.length < 6) {
    ElMessage.error('Password must be at least 6 characters')
    return
  }
  await apiChangePassword({
    userId: userId.value.toString(),
    oldPassword: passwordForm.value.oldPassword,
    newPassword: passwordForm.value.newPassword
  })
  ElMessage.success('Password changed')
  changePasswordDialogVisible.value = false
}

const handleExport = async () => {
  exporting.value = true
  try {
    const res = await exportBackup(userId.value)
    const json = JSON.stringify(res.data, null, 2)
    const blob = new Blob([json], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `coinflow-backup-${new Date().toISOString().slice(0, 10)}.json`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('Data exported successfully')
  } catch {
    ElMessage.error('Failed to export data')
  } finally {
    exporting.value = false
  }
}

const handleFileSelect = (file) => {
  selectedFile.value = file.raw
}

const handleImport = async () => {
  if (!selectedFile.value) {
    ElMessage.error('Please select a JSON file')
    return
  }
  try {
    await ElMessageBox.confirm(
      'This will import all data from the selected file. Existing data may be duplicated. Continue?',
      'Confirm Import',
      { confirmButtonText: 'Import', cancelButtonText: 'Cancel', type: 'warning' }
    )
  } catch {
    return
  }

  importing.value = true
  try {
    const text = await selectedFile.value.text()
    const data = JSON.parse(text)
    await importBackup(data, userId.value)
    ElMessage.success('Data imported successfully')
    selectedFile.value = null
    await loadData()
  } catch (e) {
    ElMessage.error('Failed to import data: ' + (e.message || 'Unknown error'))
  } finally {
    importing.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.form-tip {
  color: #9ca3af;
  font-size: 12px;
  margin-left: 12px;
}
</style>
