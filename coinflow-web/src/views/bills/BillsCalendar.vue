<template>
  <div class="calendar-view">
    <div class="calendar-header">
      <el-button text @click="prevMonth">
        <el-icon><ArrowLeft /></el-icon>
      </el-button>
      <span class="month-title">{{ currentMonthLabel }}</span>
      <el-button text @click="nextMonth">
        <el-icon><ArrowRight /></el-icon>
      </el-button>
    </div>

    <div class="calendar-weekdays">
      <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
    </div>

    <div class="calendar-grid">
      <div
        v-for="cell in calendarDays"
        :key="cell.date.format('YYYY-MM-DD')"
        class="calendar-cell"
        :class="{
          'other-month': !cell.isCurrentMonth,
          today: cell.isToday,
        }"
        @click="openDayDetail(cell)"
      >
        <span class="day-number">{{ cell.date.date() }}</span>
        <div class="bill-dots">
          <span
            v-for="(bill, i) in cell.bills.slice(0, 4)"
            :key="i"
            class="dot"
            :class="bill.type.toLowerCase()"
          />
        </div>
        <div v-if="cell.incomeTotal > 0" class="total income">
          +{{ cell.incomeTotal.toFixed(2) }}
        </div>
        <div v-if="cell.expenseTotal > 0" class="total expense">
          -{{ cell.expenseTotal.toFixed(2) }}
        </div>
      </div>
    </div>

    <el-drawer
      v-model="drawerVisible"
      :title="selectedDateLabel"
      size="400px"
    >
      <div v-if="selectedDayBills.length === 0" class="empty-state">
        <el-empty description="No bills for this day" :image-size="80">
          <el-button type="primary" @click="emit('add-bill-on-date', selectedDate)">
            Add Bill
          </el-button>
        </el-empty>
      </div>
      <div v-else>
        <div class="day-summary">
          <span class="summary-item income">Income: +{{ selectedIncome.toFixed(2) }}</span>
          <span class="summary-item expense">Expense: -{{ selectedExpense.toFixed(2) }}</span>
        </div>
        <el-divider />
        <div
          v-for="bill in selectedDayBills"
          :key="bill.id"
          class="bill-row"
          @click="emit('edit-bill', bill)"
        >
          <div class="bill-info">
            <span
              class="type-dot"
              :class="bill.type.toLowerCase()"
            />
            <span class="bill-category">{{ getCategoryName(bill.categoryId) }}</span>
            <span v-if="bill.remark" class="bill-remark">{{ bill.remark }}</span>
          </div>
          <span
            class="bill-amount"
            :class="bill.type === 'INCOME' ? 'income' : 'expense'"
          >
            {{ bill.type === 'INCOME' ? '+' : '-' }}{{ Number(bill.amount).toFixed(2) }}
          </span>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="emit('add-bill-on-date', selectedDate)">
          Add Bill
        </el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import dayjs from 'dayjs'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const props = defineProps({
  bills: { type: Array, default: () => [] },
  categories: { type: Array, default: () => [] },
})

const emit = defineEmits(['edit-bill', 'add-bill-on-date', 'month-change'])

const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
const currentMonth = ref(dayjs())
const drawerVisible = ref(false)
const selectedDate = ref(null)

const currentMonthLabel = computed(() =>
  currentMonth.value.format('MMMM YYYY')
)

const selectedDateLabel = computed(() =>
  selectedDate.value ? dayjs(selectedDate.value).format('dddd, MMM D, YYYY') : ''
)

const dailyTotals = computed(() => {
  const map = {}
  for (const bill of props.bills) {
    const billDate = bill.happenedAt || bill.date
    if (!billDate) continue
    const dateKey = dayjs(billDate).format('YYYY-MM-DD')
    if (!map[dateKey]) {
      map[dateKey] = { income: 0, expense: 0, transfer: 0, bills: [] }
    }
    const amount = Number(bill.amount)
    if (bill.type === 'INCOME') map[dateKey].income += amount
    else if (bill.type === 'EXPENSE') map[dateKey].expense += amount
    else if (bill.type === 'TRANSFER') map[dateKey].transfer += amount
    map[dateKey].bills.push(bill)
  }
  return map
})

const calendarDays = computed(() => {
  const startOfMonth = currentMonth.value.startOf('month')
  const startDate = startOfMonth.startOf('week')
  const today = dayjs().format('YYYY-MM-DD')
  const days = []

  for (let i = 0; i < 42; i++) {
    const date = startDate.add(i, 'day')
    const dateKey = date.format('YYYY-MM-DD')
    const totals = dailyTotals.value[dateKey] || { income: 0, expense: 0, bills: [] }
    days.push({
      date,
      isCurrentMonth: date.month() === currentMonth.value.month(),
      isToday: dateKey === today,
      bills: totals.bills,
      incomeTotal: totals.income,
      expenseTotal: totals.expense,
    })
  }
  return days
})

const selectedDayBills = computed(() => {
  if (!selectedDate.value) return []
  const dateKey = dayjs(selectedDate.value).format('YYYY-MM-DD')
  return dailyTotals.value[dateKey]?.bills || []
})

const selectedIncome = computed(() => {
  return selectedDayBills.value.reduce((sum, b) => sum + (b.type === 'INCOME' ? Number(b.amount) : 0), 0)
})

const selectedExpense = computed(() => {
  return selectedDayBills.value.reduce((sum, b) => sum + (b.type === 'EXPENSE' ? Number(b.amount) : 0), 0)
})

function getCategoryName(categoryId) {
  const cat = props.categories.find(c => c.id === categoryId)
  return cat?.name || 'Uncategorized'
}

function openDayDetail(cell) {
  if (!cell.isCurrentMonth) return
  selectedDate.value = cell.date.toDate()
  drawerVisible.value = true
}

function prevMonth() {
  currentMonth.value = currentMonth.value.subtract(1, 'month')
  emitMonthChange()
}

function nextMonth() {
  currentMonth.value = currentMonth.value.add(1, 'month')
  emitMonthChange()
}

function emitMonthChange() {
  emit('month-change', {
    year: currentMonth.value.year(),
    month: currentMonth.value.month(),
  })
}
</script>

<style scoped>
.calendar-view {
  padding: 16px;
  user-select: none;
}

.calendar-header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.month-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 4px;
}

.weekday {
  text-align: center;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  padding: 8px 0;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.calendar-cell {
  min-height: 90px;
  padding: 6px 8px;
  border: 1px solid var(--border-light);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: var(--bg-primary);
}

.calendar-cell:hover:not(.other-month) {
  background: var(--bg-secondary);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.calendar-cell.today {
  border-color: var(--primary-color);
  background: var(--bg-secondary);
}

.calendar-cell.other-month {
  opacity: 0.35;
  cursor: default;
}

.day-number {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.today .day-number {
  color: var(--primary-color);
}

.bill-dots {
  display: flex;
  gap: 3px;
  margin-top: 4px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  display: inline-block;
}

.dot.expense {
  background: var(--danger-color);
}

.dot.income {
  background: var(--success-color);
}

.dot.transfer {
  background: #E6A23C;
}

.total {
  font-size: 11px;
  font-weight: 500;
  margin-top: 2px;
}

.total.income {
  color: var(--success-color);
}

.total.expense {
  color: var(--danger-color);
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.day-summary {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.summary-item {
  font-size: 14px;
  font-weight: 600;
}

.summary-item.income {
  color: var(--success-color);
}

.summary-item.expense {
  color: var(--danger-color);
}

.bill-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.15s ease;
}

.bill-row:hover {
  background: var(--bg-secondary);
}

.bill-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.type-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.type-dot.expense {
  background: var(--danger-color);
}

.type-dot.income {
  background: var(--success-color);
}

.type-dot.transfer {
  background: #E6A23C;
}

.bill-category {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bill-remark {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bill-amount {
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
  margin-left: 12px;
}

.bill-amount.income {
  color: var(--success-color);
}

.bill-amount.expense {
  color: var(--danger-color);
}
</style>
