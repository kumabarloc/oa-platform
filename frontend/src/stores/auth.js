import { defineStore } from 'pinia'
import { login as apiLogin, getUserInfo, logout as apiLogout } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('oa_token') || '',
    user: JSON.parse(localStorage.getItem('oa_user') || 'null'),
    menus: JSON.parse(localStorage.getItem('oa_menus') || '[]'),
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userInfo: (state) => state.user,
    userMenus: (state) => state.menus,
  },

  actions: {
    async login(credentials) {
      const res = await apiLogin(credentials)
      // 后端返回 R.ok(LoginVo)，request 拦截器已解包为 LoginVo：{ token, userInfo }
      const token = res?.token
      const user = res?.userInfo
      if (!token) {
        throw new Error('登录失败：未获取到 token')
      }
      this.token = token
      this.user = user
      localStorage.setItem('oa_token', token)
      localStorage.setItem('oa_user', JSON.stringify(user))
      return res
    },

    async fetchUserInfo() {
      const res = await getUserInfo()
      // 后端返回 R.ok(LoginUser)，request 拦截器已解包为 LoginUser
      this.user = res || null
      localStorage.setItem('oa_user', JSON.stringify(this.user))
      // menus 目前前端用 mock，后端返回菜单后再在此落库
      return res
    },

    logout() {
      apiLogout()
      this.token = ''
      this.user = null
      this.menus = []
      localStorage.removeItem('oa_token')
      localStorage.removeItem('oa_user')
      localStorage.removeItem('oa_menus')
    },
  },
})