<template>
  <div class="notifications-container">
    <el-card class="notify-card">
      <template #header>
        <div class="card-header">
          <h2 class="card-title">Notifications</h2>
          <div>
            <el-button type="primary" @click="handleMarkAllRead">Mark All Read</el-button>
          </div>
        </div>
      </template>

      <div v-if="notifications.length === 0" class="empty-state">
        <el-empty description="No notifications" />
      </div>

      <div v-else class="notify-list">
        <div
          v-for="notify in notifications"
          :key="notify.id"
          :class="['notify-item', { unread: !notify.isRead }]"
          @click="handleClick(notify)"
        >
          <div class="notify-header">
            <el-tag :type="getTypeColor(notify.type)" size="small">{{ notify.type }}</el-tag>
            <span class="notify-title" :class="{ bold: !notify.isRead }">{{ notify.title }}</span>
          </div>
          <p class="notify-content">{{ notify.content }}</p>
          <div class="notify-footer">
            <span class="notify-time">{{ formatDate(notify.createdAt) }}</span>
            <el-button type="danger" size="small" text @click.stop="handleDelete(notify.id)">Delete</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listNotifications,
  markAsRead,
  markAllAsRead,
  deleteNotification
} from '@/services/notification'

const notifications = ref([])
const userId = 1

const getTypeColor = (type) => {
  switch (type) {
    case 'BUDGET': return 'warning'
    case 'BILL': return 'info'
    case 'SYSTEM': return ''
    default: return 'info'
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString().slice(0, 5)
}

const loadData = async () => {
  const res = await listNotifications(userId)
  notifications.value = res.data
}

const handleClick = async (notify) => {
  if (!notify.isRead) {
    await markAsRead(notify.id)
    notify.isRead = true
  }
}

const handleMarkAllRead = async () => {
  await markAllAsRead(userId)
  ElMessage.success('All marked as read')
  await loadData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('Are you sure?', 'Confirm')
  await deleteNotification(id)
  ElMessage.success('Deleted')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.notifications-container {
  animation: fadeIn 0.6s ease;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.notify-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notify-item {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.notify-item:hover {
  background-color: #f9fafb;
}

.notify-item.unread {
  border-left: 3px solid #4f46e5;
  background-color: #f5f3ff;
}

.notify-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.notify-title {
  font-weight: 500;
  color: #374151;
}

.notify-title.bold {
  font-weight: 700;
}

.notify-content {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.notify-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notify-time {
  color: #9ca3af;
  font-size: 12px;
}

.empty-state {
  padding: 40px 0;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
