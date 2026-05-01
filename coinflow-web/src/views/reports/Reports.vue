<template>
  <el-card>
    <template #header>
      <div class="flex justify-between items-center">
        <span>Reports</span>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="to"
          start-placeholder="Start date"
          end-placeholder="End date"
          @change="loadData"
        />
      </div>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="Overview">
        <div class="grid grid-cols-4 gap-4 mb-4">
          <el-card shadow="hover" class="text-center">
            <template #header>
              <div class="text-lg font-bold">Total Income</div>
            </template>
            <div class="text-2xl text-green-500">{{ overview.income.toFixed(2) }}</div>
          </el-card>
          <el-card shadow="hover" class="text-center">
            <template #header>
              <div class="text-lg font-bold">Total Expense</div>
            </template>
            <div class="text-2xl text-red-500">{{ overview.expense.toFixed(2) }}</div>
          </el-card>
          <el-card shadow="hover" class="text-center">
            <template #header>
              <div class="text-lg font-bold">Balance</div>
            </template>
            <div class="text-2xl text-blue-500">{{ (overview.income - overview.expense).toFixed(2) }}</div>
          </el-card>
          <el-card shadow="hover" class="text-center">
            <template #header>
              <div class="text-lg font-bold">Bill Count</div>
            </template>
            <div class="text-2xl text-purple-500">{{ overview.count }}</div>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Category Stats">
        <div class="grid grid-cols-2 gap-4">
          <el-card shadow="hover">
            <template #header>
              <div class="text-lg font-bold">Expense Categories</div>
            </template>
            <el-chart :option="expenseCategoryChart" style="height: 400px;" />
          </el-card>
          <el-card shadow="hover">
            <template #header>
              <div class="text-lg font-bold">Income Categories</div>
            </template>
            <el-chart :option="incomeCategoryChart" style="height: 400px;" />
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="Monthly Trend">
        <el-card shadow="hover">
          <template #header>
            <div class="text-lg font-bold">Monthly Income & Expense Trend</div>
          </template>
          <el-chart :option="monthlyChart" style="height: 400px;" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="Custom Report">
        <el-form :model="customForm" label-width="100px" class="mb-4">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="Report Type">
                <el-select v-model="customForm.reportType" placeholder="Select report type">
                  <el-option label="Income vs Expense" value="INCOME_EXPENSE" />
                  <el-option label="Category Detail" value="CATEGORY_DETAIL" />
                  <el-option label="Account Flow" value="ACCOUNT_FLOW" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="Group By">
                <el-select v-model="customForm.groupBy" placeholder="Select grouping">
                  <el-option label="By Date" value="DATE" />
                  <el-option label="By Category" value="CATEGORY" />
                  <el-option label="By Account" value="ACCOUNT" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="Action">
                <el-button type="primary" @click="generateCustomReport">Generate Report</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-card shadow="hover" v-if="customReportData.length > 0">
          <template #header>
            <div class="text-lg font-bold">Custom Report Results</div>
          </template>
          <el-table :data="customReportData" stripe>
            <el-table-column v-for="column in customReportColumns" :key="column.prop" :prop="column.prop" :label="column.label" />
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import {
  getOverview,
  getCategoryStats,
  getMonthlyStats,
  getCustomReport
} from '@/services/report'

const dateRange = ref([])
const activeTab = ref('0')
const overview = ref({ income: 0, expense: 0, count: 0 })
const categoryStats = ref({ income: [], expense: [] })
const monthlyStats = ref([])
const customForm = ref({
  reportType: 'INCOME_EXPENSE',
  groupBy: 'DATE'
})
const customReportData = ref([])
const customReportColumns = ref([])

const loadData = async () => {
  const params = {
    userId: 1,
    startDate: dateRange.value[0] ? dateRange.value[0].toISOString() : '',
    endDate: dateRange.value[1] ? dateRange.value[1].toISOString() : ''
  }
  
  // Load overview data
  const overviewRes = await getOverview(params)
  overview.value = overviewRes.data

  // Load category statistics
  const categoryRes = await getCategoryStats(params)
  categoryStats.value = categoryRes.data

  // Load monthly statistics
  const monthlyRes = await getMonthlyStats(params)
  monthlyStats.value = monthlyRes.data
}

const expenseCategoryChart = computed(() => {
  return {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    series: [
      {
        name: 'Expense Category',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: categoryStats.value.expense.map(item => ({
          value: item.amount,
          name: item.categoryName
        }))
      }
    ]
  }
})

const incomeCategoryChart = computed(() => {
  return {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      top: '5%',
      left: 'center'
    },
    series: [
      {
        name: '收入分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: categoryStats.value.income.map(item => ({
          value: item.amount,
          name: item.categoryName
        }))
      }
    ]
  }
})

const monthlyChart = computed(() => {
  return {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['收入', '支出']
    },
    xAxis: {
      type: 'category',
      data: monthlyStats.value.map(item => item.month)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收入',
        type: 'line',
        data: monthlyStats.value.map(item => item.income),
        smooth: true,
        lineStyle: {
          color: '#67c23a'
        }
      },
      {
        name: '支出',
        type: 'line',
        data: monthlyStats.value.map(item => item.expense),
        smooth: true,
        lineStyle: {
          color: '#f56c6c'
        }
      }
    ]
  }
})

const generateCustomReport = async () => {
  const params = {
    userId: 1,
    startDate: dateRange.value[0] ? dateRange.value[0].toISOString() : '',
    endDate: dateRange.value[1] ? dateRange.value[1].toISOString() : '',
    reportType: customForm.value.reportType,
    groupBy: customForm.value.groupBy
  }
  
  const res = await getCustomReport(params)
  customReportData.value = res.data.data
  customReportColumns.value = res.data.columns
}

onMounted(() => {
  // 默认加载最近30天的数据
  const endDate = new Date()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 30)
  dateRange.value = [startDate, endDate]
  loadData()
})
</script>

<style scoped>
.grid {
  display: grid;
}

.grid-cols-4 {
  grid-template-columns: repeat(4, 1fr);
}

.grid-cols-2 {
  grid-template-columns: repeat(2, 1fr);
}

.gap-4 {
  gap: 16px;
}

.mb-4 {
  margin-bottom: 16px;
}
</style>
