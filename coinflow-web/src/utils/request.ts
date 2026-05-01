// src/utils/request.ts
import axios from 'axios'

const service = axios.create({
    baseURL: '/api',
    timeout: 5000
})

// Request interceptor: automatically attach token
service.interceptors.request.use(config => {
    const tokenObj = JSON.parse(localStorage.getItem('token'));
    const token = tokenObj.token;
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
})

export default service
