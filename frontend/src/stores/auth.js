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
      this.token = res.data?.token
      this.user = res.data?.user
      localStorage.setItem('oa_token', this.token)
      localStorage.setItem('oa_user', JSON.stringify(this.user))
      return res
    },

    async fetchUserInfo() {
      const res = await getUserInfo()
      this.user = res.data?.user
      this.menus = res.data?.menus || []
      localStorage.setItem('oa_user', JSON.stringify(this.user))
      localStorage.setItem('oa_menus', JSON.stringify(this.menus))
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