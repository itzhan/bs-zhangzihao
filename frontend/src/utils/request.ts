import axios from 'axios'
import { getToken, clearAuth } from './auth'
import router from '@/router'

const request = axios.create({
  baseURL: '',
  timeout: 15000
})

// Request interceptor: attach Bearer token
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor: unwrap the outer Result wrapper
request.interceptors.response.use(
  (response) => {
    const res = response.data
    // Return the full { code, data, message } object
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      clearAuth()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default request
