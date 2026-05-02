<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-left">
        <div class="login-brand">
          <h1 class="brand-name">CoinFlow</h1>
          <p class="brand-slogan">Make financial management simple</p>
          <div class="brand-features">
            <div class="feature-item">
              <el-icon><Wallet /></el-icon>
              <span>Smart Bookkeeping</span>
            </div>
            <div class="feature-item">
              <el-icon><Document /></el-icon>
              <span>Detailed Reports</span>
            </div>
            <div class="feature-item">
              <el-icon><Monitor /></el-icon>
              <span>Multi-device Sync</span>
            </div>
          </div>
        </div>
      </div>
      <div class="login-right">
        <el-card class="login-card">
          <div class="login-header">
            <h2 class="login-title">{{ tabTitles[activeTab] }}</h2>
            <p class="login-subtitle">{{ tabSubtitles[activeTab] }}</p>
          </div>
          <el-tabs v-model="activeTab" class="login-tabs">
            <el-tab-pane label="Login" name="login" class="login-tab">
              <el-form :model="loginForm" @submit.prevent="onLogin" class="login-form">
                <el-form-item>
                  <el-input
                    v-model="loginForm.username"
                    placeholder="Enter username"
                    prefix-icon="User" 
                    :class="{ 'is-focused': isUsernameFocused }"
                    @focus="isUsernameFocused = true"
                    @blur="isUsernameFocused = false"
                  />
                </el-form-item>
                <el-form-item>
                  <el-input
                    type="password"
                    v-model="loginForm.password"
                    placeholder="Enter password"
                    prefix-icon="Lock" 
                    :class="{ 'is-focused': isPasswordFocused }"
                    @focus="isPasswordFocused = true"
                    @blur="isPasswordFocused = false"
                  >
                    <template #append>
                      <el-button @click="togglePasswordVisibility">
                        <el-icon v-if="!showPassword"><View /></el-icon>
                        <el-icon v-else><Hide /></el-icon>
                      </el-button>
                    </template>
                  </el-input>
                </el-form-item>
                <div class="login-options">
                  <el-checkbox v-model="loginForm.remember">Remember me</el-checkbox>
                  <el-button type="text" @click="activeTab = 'forgot'" class="forgot-password">Forgot password?</el-button>
                </div>
                <el-form-item>
                  <el-button 
                    type="primary" 
                    @click="onLogin" 
                    :loading="loading" 
                    class="login-button"
                    :class="{ 'is-loading': loading }"
                  >
                    {{ loading ? 'Logging in...' : 'Login' }}
                  </el-button>
                </el-form-item>
                <div class="login-footer">
                  <span>Don't have an account?</span>
                  <el-button type="text" @click="activeTab = 'register'" class="register-link">Sign up now</el-button>
                </div>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="Register" name="register" class="login-tab">
              <el-form :model="registerForm" @submit.prevent="onRegister" class="login-form">
                <el-form-item>
                  <el-input
                    v-model="registerForm.username"
                    placeholder="Enter username"
                    prefix-icon="User"
                  />
                </el-form-item>
                <el-form-item>
                  <el-input
                    v-model="registerForm.email"
                    placeholder="Enter email"
                    prefix-icon="Message"
                  />
                </el-form-item>
                <el-form-item>
                  <el-input
                    type="password"
                    v-model="registerForm.password"
                    placeholder="Enter password"
                    prefix-icon="Lock"
                  />
                </el-form-item>
                <el-form-item>
                  <el-input
                    type="password"
                    v-model="registerForm.confirmPassword"
                    placeholder="Confirm password"
                    prefix-icon="Lock"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    @click="onRegister"
                    :loading="loading"
                    class="login-button"
                  >
                    {{ loading ? 'Registering...' : 'Register' }}
                  </el-button>
                </el-form-item>
                <div class="login-footer">
                  <span>Already have an account?</span>
                  <el-button type="text" @click="activeTab = 'login'" class="login-link">Go to login</el-button>
                </div>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="Forgot Password" name="forgot" class="login-tab">
              <el-form :model="forgotForm" @submit.prevent="onForgotPassword" class="login-form">
                <el-form-item>
                  <el-input
                    v-model="forgotForm.email"
                    placeholder="Enter registered email"
                    prefix-icon="Message"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button
                    type="primary"
                    @click="onForgotPassword"
                    :loading="loading"
                    class="login-button"
                  >
                    {{ loading ? 'Sending...' : 'Send Reset Email' }}
                  </el-button>
                </el-form-item>
                <div class="login-footer">
                  <span>Remembered your password?</span>
                  <el-button type="text" @click="activeTab = 'login'" class="login-link">Back to login</el-button>
                </div>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, View, Hide, Wallet, Document, Monitor } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('login')
const showPassword = ref(false)
const isUsernameFocused = ref(false)
const isPasswordFocused = ref(false)

const tabTitles = {
  login: 'Welcome Back',
  register: 'Create Account',
  forgot: 'Reset Password'
}

const tabSubtitles = {
  login: 'Please log in to your account',
  register: 'Join CoinFlow and start managing your finances',
  forgot: 'Enter your email and we will send a reset link'
}

// Login form
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// Register form
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

// Forgot password form
const forgotForm = reactive({
  email: ''
})

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

const onLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('Please enter username and password')
    return
  }
  
  loading.value = true
  try {
    const res = await axios.post('http://localhost:8080/api/users/login', loginForm)
    const token = JSON.stringify(res.data.data)
    localStorage.setItem('token', token)
    ElMessage.success('Login successful')
    router.push('/main')
  } catch (err) {
    ElMessage.error('Login failed: ' + (err.response?.data?.message || err.message))
  } finally {
    loading.value = false
  }
}

const onRegister = async () => {
  if (!registerForm.username || !registerForm.email || !registerForm.password) {
    ElMessage.warning('Please fill in all required fields')
    return
  }
  
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('Passwords do not match')
    return
  }
  
  loading.value = true
  try {
    await axios.post('http://localhost:8080/api/users/register', registerForm)
    ElMessage.success('Registration successful, please log in')
    activeTab.value = 'login'
  } catch (err) {
    ElMessage.error('Registration failed: ' + (err.response?.data?.message || err.message))
  } finally {
    loading.value = false
  }
}

const onForgotPassword = async () => {
  if (!forgotForm.email) {
    ElMessage.warning('Please enter your email address')
    return
  }
  
  loading.value = true
  try {
    await axios.post('http://localhost:8080/api/users/forgot-password', forgotForm)
    ElMessage.success('Reset email sent (simulated)')
    activeTab.value = 'login'
  } catch (err) {
    ElMessage.error('Request failed: ' + (err.response?.data?.message || err.message))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1200px;
  margin: auto;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: fadeIn 0.5s ease-out;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
}

.login-brand {
  text-align: center;
  max-width: 300px;
}

.brand-name {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 16px;
  animation: slideInLeft 0.6s ease-out;
}

.brand-slogan {
  font-size: 1.1rem;
  margin-bottom: 40px;
  opacity: 0.9;
  animation: slideInLeft 0.6s ease-out 0.2s both;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  animation: slideInLeft 0.6s ease-out 0.4s both;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1rem;
  opacity: 0.9;
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateX(10px);
}

.feature-item el-icon {
  font-size: 1.2rem;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border: none;
  box-shadow: none;
  animation: slideInRight 0.6s ease-out;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-title {
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.login-subtitle {
  font-size: 0.9rem;
  color: #666;
  margin: 0;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-tab {
  padding: 0;
}

.login-form {
  width: 100%;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-input {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-form .el-input.is-focused {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  font-size: 0.9rem;
}

.forgot-password {
  color: #667eea;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #764ba2;
}

.login-button {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: 500;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    transition: all 0.3s ease;
  }

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.login-button.is-loading {
  background: linear-gradient(135deg, #5a6fd8 0%, #684297 100%);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 0.9rem;
  color: #666;
}

.register-link,
.login-link {
  color: #667eea;
  margin-left: 4px;
  transition: color 0.3s ease;
}

.register-link:hover,
.login-link:hover {
  color: #764ba2;
}

/* Animations */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Responsive Design */
@media (max-width: 768px) {
  .login-wrapper {
    flex-direction: column;
    margin: 20px;
    border-radius: 12px;
  }
  
  .login-left {
    padding: 30px;
  }
  
  .login-right {
    padding: 30px;
  }
  
  .brand-name {
    font-size: 2rem;
  }
  
  .login-title {
    font-size: 1.5rem;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }
  
  .login-wrapper {
    margin: 10px;
  }
  
  .login-left,
  .login-right {
    padding: 20px;
  }
  
  .brand-name {
    font-size: 1.8rem;
  }
  
  .login-title {
    font-size: 1.3rem;
  }
}
</style>
