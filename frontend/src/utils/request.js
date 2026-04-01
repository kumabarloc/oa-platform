import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

// 请求拦截器：自动附加 token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('oa_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一错误处理 + 401 重定向
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端标准响应：{ code, message, data }
    // 兼容少量非标准响应（无 code 字段）直接透传整个 response.data
    if (typeof res?.code === 'number' && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return typeof res?.code === 'number' ? res.data : res
  },
  (error) => {
    if (error.response) {
      if (error.response.status === 401) {
        localStorage.removeItem('oa_token')
        localStorage.removeItem('oa_user')
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(error.response.data?.message || '服务器错误')
      }
    } else {
      ElMessage.error('网络错误，请检查连接')
    }
    return Promise.reject(error)
  }
)

export default service