import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/auth/Login.vue'  // Assuming you put the login page here
import MainLayout from '@/layouts/MainLayout.vue'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        component: Login
    },
    {
        path: '/main',
        component: MainLayout,
        children: [
            { path: '', redirect: '/main/dashboard' },
            { path: 'dashboard', component: () => import('@/views/dashboard/Dashboard.vue')},
            { path: 'bills', component: () => import('@/views/bills/Bills.vue')},
            { path: 'account-books', component: () => import('@/views/account-books/AccountBookList.vue')},
            { path: 'accounts', component: () => import('@/views/accounts/AccountList.vue')},
            { path: 'categories', component: () => import('@/views/categories/CategoryList.vue')},
            { path: 'budgets', component: () => import('@/views/budgets/Budgets.vue')},
            { path: 'templates', component: () => import('@/views/templates/Templates.vue')},
            { path: 'recurring', component: () => import('@/views/recurring/Recurring.vue')},
            { path: 'reports', component: () => import('@/views/reports/Reports.vue')},
            { path: 'settings', component: () => import('@/views/settings/Settings.vue')},
            { path: 'transfer-records', component: () => import('@/views/transfer-records/TransferRecords.vue')},
            { path: 'notifications', component: () => import('@/views/notifications/Notifications.vue')},
            { path: 'feedback', component: () => import('@/views/feedback/Feedback.vue')}
        ],
        // You can add route guards later to check login status
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
