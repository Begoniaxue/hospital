import { defineStore } from 'pinia'
import { login, logout, getUserInfo, getMenu } from '@/api/auth'
import { resetRouter, resetAddRoutesFlag, generateDynamicRoutes } from '@/router'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null,
    menuList: [],
    permissions: []
  }),
  actions: {
    async login(loginForm) {
      const res = await login(loginForm)
      this.token = res.data.token
      this.userInfo = res.data.user
      this.permissions = res.data.permissions
      await this.getMenu()
      const dynamicRoutes = generateDynamicRoutes(this.menuList)
      dynamicRoutes.forEach(route => {
        router.addRoute(route)
      })
      resetAddRoutesFlag()
      return res
    },
    async getUserInfo() {
      const res = await getUserInfo()
      this.userInfo = res.data
      return res
    },
    async getMenu() {
      const res = await getMenu()
      this.menuList = res.data
      return res
    },
    async loadUserData() {
      try {
        const [userRes, menuRes] = await Promise.all([
          getUserInfo(),
          getMenu()
        ])
        this.userInfo = userRes.data
        this.menuList = menuRes.data
        this.permissions = userRes.data.roles?.flatMap(r => r.permissions?.map(p => p.code) || []) || []
        const dynamicRoutes = generateDynamicRoutes(this.menuList)
        dynamicRoutes.forEach(route => {
          router.addRoute(route)
        })
        resetAddRoutesFlag()
        return true
      } catch (e) {
        return false
      }
    },
    async logout() {
      try {
        await logout()
      } catch (e) {}
      this.token = ''
      this.userInfo = null
      this.menuList = []
      this.permissions = []
      resetRouter()
      resetAddRoutesFlag()
    }
  },
  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['token', 'userInfo']
  }
})
