<template>
  <el-container class="main-container">
    <el-header class="main-header">
      <div class="header-content">
        <div class="logo">
          <el-icon class="logo-icon"><Wallet /></el-icon>
          <span class="logo-text">CoinFlow</span>
        </div>
        <div class="header-actions">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-button type="text" @click="goToNotifications">
              <el-icon size="20"><Bell /></el-icon>
            </el-button>
          </el-badge>
          <el-button type="primary" size="small" class="add-bill-button" @click="addBill">
            <el-icon><Plus /></el-icon>
            <span>Add Bill</span>
          </el-button>
          <el-dropdown trigger="click" class="user-dropdown" @visible-change="handleDropdownVisible">
            <el-button type="text" class="user-button">
              <el-avatar size="small" :src="userAvatar" />
              <span class="user-name">{{ userName }}</span>
              <el-icon class="arrow-icon" :class="{ 'rotate': dropdownVisible }"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown-menu">
                <el-dropdown-item @click="goToSettings" class="dropdown-item">
                  <el-icon class="dropdown-item-icon"><Setting /></el-icon>
                  <span>Settings</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout" class="dropdown-item">
                  <el-icon class="dropdown-item-icon"><SwitchButton /></el-icon>
                  <span>Logout</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-container class="main-body">
      <el-aside :class="['main-sidebar', { 'collapsed': isCollapsed }]">
        <el-menu
            :default-active="activeMenu"
            @select="handleSelect"
            router
            unique-opened
            class="sidebar-menu"
            :collapse="isCollapsed"
            @collapse-change="handleCollapse"
        >
          <el-menu-item index="/main/dashboard">
            <el-icon><House /></el-icon>
            <template #title>Dashboard</template>
          </el-menu-item>
          <el-menu-item index="/main/bills">
            <el-icon><Document /></el-icon>
            <template #title>Bills</template>
          </el-menu-item>
          <el-menu-item index="/main/account-books">
            <el-icon><Folder /></el-icon>
            <template #title>Account Books</template>
          </el-menu-item>
          <el-menu-item index="/main/accounts">
            <el-icon><CreditCard /></el-icon>
            <template #title>Accounts</template>
          </el-menu-item>
          <el-menu-item index="/main/categories">
            <el-icon><Grid /></el-icon>
            <template #title>Categories</template>
          </el-menu-item>
          <el-menu-item index="/main/budgets">
            <el-icon><Timer /></el-icon>
            <template #title>Budgets</template>
          </el-menu-item>
          <el-menu-item index="/main/templates">
            <el-icon><CopyDocument /></el-icon>
            <template #title>Templates</template>
          </el-menu-item>
          <el-menu-item index="/main/recurring">
            <el-icon><Refresh /></el-icon>
            <template #title>Recurring Bills</template>
          </el-menu-item>
          <el-menu-item index="/main/reports">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>Reports</template>
          </el-menu-item>
          <el-menu-item index="/main/settings">
            <el-icon><Setting /></el-icon>
            <template #title>Settings</template>
          </el-menu-item>
          <el-menu-item index="/main/transfer-records">
            <el-icon><SwitchButton /></el-icon>
            <template #title>Transfers</template>
          </el-menu-item>
          <el-menu-item index="/main/notifications">
            <el-icon><Bell /></el-icon>
            <template #title>Notifications</template>
          </el-menu-item>
          <el-menu-item index="/main/feedback">
            <el-icon><ChatDotRound /></el-icon>
            <template #title>Feedback</template>
          </el-menu-item>
        </el-menu>
        <div class="sidebar-footer">
          <el-button type="text" class="collapse-button" @click="isCollapsed = !isCollapsed">
            <el-icon v-if="!isCollapsed"><ArrowLeft /></el-icon>
            <el-icon v-else><ArrowRight /></el-icon>
          </el-button>
        </div>
      </el-aside>

      <el-main class="main-content">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
  <QuickAddBillDialog v-model="quickAddVisible" @bill-added="handleBillAdded" />
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserById } from '@/services/user'
import { getUnreadCount } from '@/services/notification'
import { 
  Wallet,
  House,
  Document,
  Grid,
  Timer,
  CopyDocument,
  Refresh,
  DataAnalysis,
  Setting,
  ArrowDown,
  ArrowLeft,
  ArrowRight,
  Plus,
  SwitchButton,
  CreditCard,
  Folder,
  Bell,
  ChatDotRound
} from '@element-plus/icons-vue'

import QuickAddBillDialog from '@/components/QuickAddBillDialog.vue'

const router = useRouter()
const route = useRoute()
const activeMenu = ref(route.path)
const isCollapsed = ref(false)
const userName = ref('User')
const userAvatar = ref('')
const dropdownVisible = ref(false)
const unreadCount = ref(0)
const quickAddVisible = ref(false)

// Watch route changes to update menu active state
watch(
    () => route.path,
    (newPath) => {
      activeMenu.value = newPath
    },
    { immediate: true }
)

function handleSelect(index) {
  router.push(index)
}

function handleCollapse(collapsed) {
  isCollapsed.value = collapsed
  localStorage.setItem('sidebarCollapsed', collapsed.toString())
}

function goToSettings() {
  router.push('/main/settings')
}

function goToNotifications() {
  router.push('/main/notifications')
}

function logout() {
  localStorage.removeItem('token')
  router.push('/login')
}

function addBill() {
  quickAddVisible.value = true
}

function handleBillAdded() {
  ElMessage.success('Bill added successfully')
}

function handleDropdownVisible(visible) {
  dropdownVisible.value = visible
}

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

onMounted(async () => {
  // Restore sidebar state from local storage
  const savedCollapsed = localStorage.getItem('sidebarCollapsed')
  if (savedCollapsed !== null) {
    isCollapsed.value = savedCollapsed === 'true'
  }

  // Load user info from API
  try {
    const id = getUserId()
    const res = await getUserById(id)
    if (res.data) {
      userName.value = res.data.username || 'User'
      userAvatar.value = res.data.avatar || ''
    }
  } catch {
    // User not found, keep default
    userName.value = 'User'
  }

  // Load notification unread count
  try {
    const id = getUserId()
    const res = await getUnreadCount(id)
    unreadCount.value = res.data || 0
  } catch {
    unreadCount.value = 0
  }
})
</script>

<style scoped>
.main-container {
  height: 100vh;
  background-color: #f8f9fa;
}

.main-header {
  height: 64px;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  z-index: 100;
  transition: all 0.3s ease;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: #4f46e5;
  transition: all 0.3s ease;
  cursor: pointer;
}

.logo:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.logo-icon {
  font-size: 28px;
  color: #4f46e5;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  display: inline-flex;
}

.add-bill-button {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  border: none;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.add-bill-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
  background: linear-gradient(135deg, #4338ca 0%, #4f46e5 100%);
}

.user-dropdown {
  cursor: pointer;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #374151;
  transition: all 0.3s ease;
  padding: 6px 12px;
  border-radius: 8px;
}

.user-button:hover {
  background-color: #f3f4f6;
  color: #4f46e5;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

.arrow-icon {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.arrow-icon.rotate {
  transform: rotate(180deg);
}

.user-dropdown-menu {
  min-width: 160px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: none;
  padding: 8px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  transition: all 0.2s ease;
}

.dropdown-item:hover {
  background-color: #f3f4f6;
}

.dropdown-item-icon {
  font-size: 14px;
  color: #6b7280;
}

.main-body {
  flex: 1;
  overflow: hidden;
}

.main-sidebar {
  width: 256px;
  min-width: 256px;
  max-width: 256px;
  background: white;
  box-shadow: 1px 0 3px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e5e7eb;
}

.main-sidebar.collapsed {
  width: 80px;
  min-width: 80px;
  max-width: 80px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: white;
  padding: 16px 0;
}

.sidebar-menu .el-menu-item {
  height: 56px;
  line-height: 56px;
  margin: 4px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(79, 70, 229, 0.05);
  color: #4f46e5;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

.sidebar-menu .el-menu-item.is-active .el-icon {
  color: white;
}

.sidebar-menu .el-icon {
  font-size: 18px;
  margin-right: 12px;
  color: #6b7280;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover .el-icon {
  color: #4f46e5;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: center;
}

.collapse-button {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  color: #6b7280;
}

.collapse-button:hover {
  background-color: #f3f4f6;
  color: #4f46e5;
}

.main-content {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
  background-color: #f8f9fa;
  transition: all 0.3s ease;
}

.content-wrapper {
  width: 100%;
  min-height: 100%;
}

/* Page transition animation */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* Responsive design */
@media (max-width: 768px) {
  .main-sidebar {
    position: fixed;
    left: 0;
    top: 64px;
    height: calc(100vh - 64px);
    z-index: 99;
    transform: translateX(-100%);
  }
  
  .main-sidebar.show {
    transform: translateX(0);
  }
  
  .main-content {
    padding: 16px;
  }
  
  .logo-text {
    display: none;
  }
  
  .user-name {
    display: none;
  }
  
  .add-bill-button span {
    display: none;
  }
  
  .header-content {
    padding: 0 16px;
  }
}

/* Scrollbar styles */
.main-content::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.main-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

.main-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
