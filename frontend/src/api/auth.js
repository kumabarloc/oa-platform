import request from '@/utils/request'

export async function getCaptcha() {
  return new Promise((resolve, reject) => {
    request({
      url: '/auth/captcha',
      method: 'get',
      responseType: 'blob',
    })
      .then(async (blob) => {
        // Captcha-Id 在响应 header 里，需要从 axios response 里拿
        // 由于 axios 不能直接访问 response headers（blob 模式），
        // 用 fetch 绕过去获取 header
        try {
          const resp = await fetch('/auth/captcha')
          const key = resp.headers.get('Captcha-Id') || ''
          const imageUrl = URL.createObjectURL(await resp.blob())
          resolve({ key, image: imageUrl })
        } catch {
          reject(new Error('获取验证码失败'))
        }
      })
      .catch(reject)
  })
}

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data,
  })
}

export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get',
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}