import request from '@/utils/request'

export async function getCaptcha() {
  try {
    const resp = await fetch('/api/auth/captcha')
    const key = resp.headers.get('Captcha-Id') || ''
    const imageUrl = URL.createObjectURL(await resp.blob())
    return { key, image: imageUrl }
  } catch {
    throw new Error('获取验证码失败')
  }
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