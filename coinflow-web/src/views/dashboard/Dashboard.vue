<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1 class="dashboard-title">Welcome back, {{ userName }}</h1>
      <p class="dashboard-subtitle">Today is {{ currentDate }}, let's manage your finances</p>
    </div>

    <div class="overview-cards">
      <el-card class="overview-card income-card" hoverable>
        <div class="card-content">
          <div class="card-icon income-icon">
            <el-icon class="icon"><TrendCharts /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-label">Total Income</p>
            <p class="card-value">¥{{ totalIncome.toFixed(2) }}</p>
            <p class="card-change positive">
              <el-icon class="change-icon"><ArrowUp /></el-icon>
              {{ incomeChange }}%
            </p>
          </div>
        </div>
      </el-card>

      <el-card class="overview-card expense-card" hoverable>
        <div class="card-content">
          <div class="card-icon expense-icon">
            <el-icon class="icon"><Goods /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-label">Total Expense</p>
            <p class="card-value">¥{{ totalExpense.toFixed(2) }}</p>
            <p class="card-change negative">
              <el-icon class="change-icon"><ArrowUp /></el-icon>
              {{ expenseChange }}%
            </p>
          </div>
        </div>
      </el-card>

      <el-card class="overview-card balance-card" hoverable>
        <div class="card-content">
          <div class="card-icon balance-icon">
            <el-icon class="icon"><Wallet /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-label">Balance</p>
            <p class="card-value">¥{{ balance.toFixed(2) }}</p>
            <p class="card-change" :class="balanceChange >= 0 ? 'positive' : 'negative'">
              <el-icon class="change-icon">{{ balanceChange >= 0 ? 'ArrowUp' : 'ArrowDown' }}</el-icon>
              {{ Math.abs(balanceChange) }}%
            </p>
          </div>
        </div>
      </el-card>

      <el-card class="overview-card bill-card" hoverable>
        <div class="card-content">
          <div class="card-icon bill-icon">
            <el-icon class="icon"><Document /></el-icon>
          </div>
          <div class="card-info">
            <p class="card-label">This Month's Bills</p>
            <p class="card-value">{{ billCount }} records</p>
          </div>
        </div>
      </el-card>
    </div>

    <div class="charts-container">
      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span class="chart-title">Expense Categories</span>
            <el-select v-model="chartPeriod" size="small" class="period-select">
              <el-option label="This Month" value="month" />
              <el-option label="This Quarter" value="quarter" />
              <el-option label="This Year" value="year" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <v-chart v-if="categoryChartData.length > 0" :option="categoryChart" autoresize style="height: 300px;" />
          <el-empty v-else description="No data available" />
        </div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="chart-header">
            <span class="chart-title">Income & Expense Trend</span>
            <el-select v-model="trendPeriod" size="small" class="period-select" @change="loadMonthlyStats">
              <el-option label="Last 6 Months" value="6" />
              <el-option label="Last 12 Months" value="12" />
            </el-select>
          </div>
        </template>
        <div class="chart-content">
          <v-chart v-if="trendChartData.months.length > 0" :option="trendChart" autoresize style="height: 300px;" />
          <el-empty v-else description="No data available" />
        </div>
      </el-card>
    </div>

    <el-card class="recent-bills-card">
      <template #header>
        <div class="bills-header">
          <span class="bills-title">Recent Bills</span>
          <el-button type="primary" size="small" class="view-all-button" @click="goToBills">
            <span>View All</span>
            <el-icon class="arrow-icon"><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <el-table :data="recentBills" style="width: 100%" class="bills-table" v-loading="loading">
        <el-table-column prop="date" label="Date" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.happenedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="Type" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 'INCOME' ? 'success' : 'danger'" class="type-tag">
              {{ scope.row.type === 'INCOME' ? 'Income' : 'Expense' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Category">
          <template #default="scope">
            {{ categoryName(scope.row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="Amount" width="120">
          <template #default="scope">
            <span :class="scope.row.type === 'INCOME' ? 'text-success' : 'text-danger'" class="amount-text">
              {{ scope.row.type === 'INCOME' ? '+' : '-' }}¥{{ Number(scope.row.amount).toFixed(2) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="Account" width="120">
          <template #default="scope">
            {{ accountName(scope.row.assetAccountId) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="Description" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  TrendCharts, Goods, Wallet, Document, ArrowUp, ArrowDown, ArrowRight
} from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import 'echarts'
import { getOverview, getCategoryStats, getMonthlyStats } from '@/services/report'
import request from '@/utils/request'
import { listBills } from '@/services/bill'
import { listCategories } from '@/services/category'
import { listAccounts } from '@/services/account'

const router = useRouter()
const loading = ref(false)

const userId = 1 // TODO: Extract from JWT token
const chartPeriod = ref('month')
const trendPeriod = ref('6')

const userName = ref('User')
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
  })
})

// Overview data
const totalIncome = ref(0)
const totalExpense = ref(0)
const incomeChange = ref(0)
const expenseChange = ref(0)
const billCount = ref(0)
const balance = computed(() => totalIncome.value - totalExpense.value)
const balanceChange = ref(0)

// Category data
const categoryChartData = ref([])
const categoryChart = computed(() => {
  const colors = ['#4f46e5', '#10b981', '#f59e0b', '#ef4444', '#3b82f6', '#8b5cf6', '#ec4899', '#14b8a6']
  return {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      name: 'Expense Category',
      type: 'pie',
      radius: ['45%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 12, borderColor: '#fff', borderWidth: 2 },
      label: { show: false, position: 'center' },
      emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
      data: categoryChartData.value.map((item, i) => ({
        name: item.category_name,
        value: Number(item.total_amount),
        itemStyle: { color: colors[i % colors.length] }
      }))
    }]
  }
})

// Trend data
const trendChartData = ref({ months: [], income: [], expense: [] })
const trendChart = computed(() => {
  return {
    tooltip: { trigger: 'axis' },
    legend: { data: ['Income', 'Expense'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: trendChartData.value.months },
    yAxis: { type: 'value' },
    series: [
      { name: 'Income', type: 'line', data: trendChartData.value.income, smooth: true, lineStyle: { width: 3 }, itemStyle: { color: '#10b981' } },
      { name: 'Expense', type: 'line', data: trendChartData.value.expense, smooth: true, lineStyle: { width: 3 }, itemStyle: { color: '#ef4444' } }
    ]
  }
})

// Recent bills
const recentBills = ref([])

// Category and account mapping
const categoryMap = ref(new Map())
const accountMap = ref(new Map())

function categoryName(id) {
  return categoryMap.value.get(id) || '-'
}

function accountName(id) {
  return accountMap.value.get(id) || '-'
}

function formatDate(date) {
  if (!date) return '-'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

// Load overview data
async function loadOverview() {
  try {
    const { data } = await getOverview({ userId, period: chartPeriod.value })
    totalIncome.value = Number(data.totalIncome || 0)
    totalExpense.value = Number(data.totalExpense || 0)
    incomeChange.value = Number(data.incomeChange || 0)
    expenseChange.value = Number(data.expenseChange || 0)
    billCount.value = Number(data.billCount || 0)
    const bal = totalIncome.value - totalExpense.value
    balanceChange.value = bal !== 0 ? Math.round((bal / (totalIncome.value || 1)) * 100) : 0
  } catch (e) {
    console.error('loadOverview failed', e)
  }
}

// Load category statistics
async function loadCategoryStats() {
  try {
    const { data } = await getCategoryStats({ userId, type: 'EXPENSE', period: chartPeriod.value })
    categoryChartData.value = data.data || []
  } catch (e) {
    console.error('loadCategoryStats failed', e)
  }
}

// Load monthly statistics
async function loadMonthlyStats() {
  try {
    const { data } = await getMonthlyStats({ userId, months: trendPeriod.value })
    trendChartData.value = {
      months: data.months || [],
      income: (data.income || []).map(v => Number(v)),
      expense: (data.expense || []).map(v => Number(v))
    }
  } catch (e) {
    console.error('loadMonthlyStats failed', e)
  }
}

// Load recent bills
async function loadRecentBills() {
  try {
    const { data } = await request.get('/bills/recent', { params: { userId, limit: 5 } })
    recentBills.value = data || []
  } catch (e) {
    console.error('loadRecentBills failed', e)
  }
}

// Load category and account name mapping
async function loadMeta() {
  try {
    const [catRes, accRes] = await Promise.all([
      listCategories(userId),
      listAccounts(userId)
    ])
    catRes.data?.forEach(c => categoryMap.value.set(c.id, c.name))
    accRes.data?.forEach(a => accountMap.value.set(a.id, a.name))
  } catch (e) {
    console.error('loadMeta failed', e)
  }
}

function goToBills() {
  router.push('/main/bills')
}

onMounted(async () => {
  loading.value = true
  await Promise.all([loadOverview(), loadCategoryStats(), loadMonthlyStats(), loadMeta()])
  await loadRecentBills()
  loading.value = false
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
  background-color: #f8f9fa;
  min-height: 100%;
}
.dashboard-header {
  padding: 0 32px 32px;
  margin-top: 0;
  animation: fadeIn 0.6s ease;
}
.dashboard-title {
  font-size: 28px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 8px;
}
.dashboard-subtitle {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}
.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  padding: 0 32px 32px;
  animation: fadeInUp 0.6s ease 0.2s both;
}
.overview-card {
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  overflow: hidden;
  border: 1px solid #e5e7eb;
  background: white;
}
.overview-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: #4f46e5;
}
.card-content {
  display: flex;
  align-items: center;
  padding: 28px;
}
.card-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}
.income-icon { background: linear-gradient(135deg, rgba(16, 185, 129, 0.1), rgba(16, 185, 129, 0.05)); }
.expense-icon { background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(239, 68, 68, 0.05)); }
.balance-icon { background: linear-gradient(135deg, rgba(79, 70, 229, 0.1), rgba(79, 70, 229, 0.05)); }
.bill-icon { background: linear-gradient(135deg, rgba(245, 158, 11, 0.1), rgba(245, 158, 11, 0.05)); }
.card-icon .icon { font-size: 28px; }
.income-icon .icon { color: #10b981; }
.expense-icon .icon { color: #ef4444; }
.balance-icon .icon { color: #4f46e5; }
.bill-icon .icon { color: #f59e0b; }
.card-info { flex: 1; }
.card-label { font-size: 14px; color: #6b7280; margin: 0 0 8px 0; }
.card-value { font-size: 24px; font-weight: 700; color: #111827; margin: 0 0 8px 0; }
.card-change { font-size: 13px; margin: 0; display: flex; align-items: center; gap: 4px; }
.change-icon { font-size: 12px; }
.card-change.positive { color: #10b981; }
.card-change.negative { color: #ef4444; }
.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 24px;
  padding: 0 32px 32px;
  animation: fadeInUp 0.6s ease 0.4s both;
}
.chart-card {
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #e5e7eb;
  background: white;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
}
.chart-title { font-size: 16px; font-weight: 600; color: #111827; }
.chart-content { padding: 24px; }
.recent-bills-card {
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #e5e7eb;
  background: white;
  margin: 0 32px 32px;
  animation: fadeInUp 0.6s ease 0.6s both;
}
.bills-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
}
.bills-title { font-size: 16px; font-weight: 600; color: #111827; }
.view-all-button {
  border-radius: 8px;
  background: #4f46e5;
  border: none;
  transition: all 0.3s ease;
}
.view-all-button:hover {
  background: #4338ca;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}
.arrow-icon { font-size: 14px; }
.bills-table th { background-color: #f9fafb; border-bottom: 1px solid #f3f4f6; font-weight: 600; }
.bills-table td { border-bottom: 1px solid #f3f4f6; }
.bills-table tr:hover { background-color: #f9fafb; }
.type-tag { border-radius: 12px; font-size: 12px; padding: 2px 8px; }
.amount-text { font-weight: 600; font-size: 14px; }
.text-success { color: #10b981; }
.text-danger { color: #ef4444; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
@media (max-width: 768px) {
  .dashboard-header { padding: 0 16px 24px; }
  .overview-cards { grid-template-columns: 1fr; padding: 0 16px 24px; }
  .charts-container { grid-template-columns: 1fr; padding: 0 16px 24px; }
  .recent-bills-card { margin: 0 16px 16px; }
  .dashboard-title { font-size: 24px; }
}
</style>
