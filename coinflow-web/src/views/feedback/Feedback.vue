<template>
  <div class="feedback-container">
    <el-card class="feedback-card">
      <template #header>
        <h2 class="card-title">Feedback & Suggestions</h2>
      </template>

      <div class="submit-section">
        <h3>Submit Feedback</h3>
        <el-input
          v-model="content"
          type="textarea"
          :rows="4"
          placeholder="Describe your feedback or suggestion..."
          class="feedback-input"
        />
        <el-button type="primary" @click="handleSubmit" :disabled="!content.trim()" class="submit-button">
          Submit
        </el-button>
      </div>

      <div class="history-section">
        <h3>My Feedback History</h3>
        <div v-if="feedbacks.length === 0" class="empty-state">
          <el-empty description="No feedback submitted yet" />
        </div>
        <div v-else class="feedback-list">
          <div v-for="item in feedbacks" :key="item.id" class="feedback-item">
            <div class="feedback-header">
              <el-tag :type="getStatusType(item.status)" size="small">{{ item.status }}</el-tag>
              <span class="feedback-time">{{ formatDate(item.createdAt) }}</span>
            </div>
            <p class="feedback-content">{{ item.content }}</p>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listFeedback, createFeedback } from '@/services/feedback'

const content = ref('')
const feedbacks = ref([])
const userId = 1

const getStatusType = (status) => {
  switch (status) {
    case 'PENDING': return 'info'
    case 'PROCESSING': return 'warning'
    case 'DONE': return 'success'
    default: return 'info'
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString().slice(0, 5)
}

const loadData = async () => {
  const res = await listFeedback(userId)
  feedbacks.value = res.data
}

const handleSubmit = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('Please enter your feedback')
    return
  }
  await createFeedback({ userId, content: content.value })
  ElMessage.success('Feedback submitted')
  content.value = ''
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.feedback-container {
  animation: fadeIn 0.6s ease;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.submit-section {
  margin-bottom: 32px;
}

.submit-section h3,
.history-section h3 {
  font-size: 16px;
  margin-bottom: 12px;
  color: #374151;
}

.feedback-input {
  margin-bottom: 12px;
}

.submit-button {
  border-radius: 8px;
}

.history-section {
  border-top: 1px solid #e5e7eb;
  padding-top: 24px;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feedback-item {
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.feedback-time {
  color: #9ca3af;
  font-size: 12px;
}

.feedback-content {
  color: #374151;
  margin: 0;
  line-height: 1.6;
}

.empty-state {
  padding: 20px 0;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
