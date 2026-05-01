import { createApp } from 'vue'
import App from './App.vue'

// Import Element Plus and styles
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// Chinese language pack
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'dayjs/locale/zh-cn'

// Import router
import router from './router'

// Import theme management
import { initTheme } from './services/theme'

// Create app instance
const app = createApp(App)

// Initialize theme
initTheme()

app.use(router)
// Mount Element Plus and set language to Chinese
app.use(ElementPlus, {
    locale: zhCn
})

app.mount('#app')
