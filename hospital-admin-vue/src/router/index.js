import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/modules/user'

const Layout = () => import('@/layout/index.vue')

const modules = import.meta.glob('@/views/**/*.vue')

export const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes
})

export function resetRouter() {
  const newRouter = createRouter({
    history: createWebHistory(),
    routes: constantRoutes
  })
  router.matcher = newRouter.matcher
}

export function generateDynamicRoutes(menus) {
  const routes = []
  menus.forEach(menu => {
    const route = {
      path: menu.path,
      component: Layout,
      redirect: menu.children && menu.children.length > 0 ? menu.children[0].path : null,
      meta: { title: menu.name, icon: menu.icon },
      children: []
    }
    if (menu.children && menu.children.length > 0) {
      menu.children.forEach(child => {
        if (child.component) {
          const componentPath = `/src/views/${child.component}.vue`
          route.children.push({
            path: child.path.replace(menu.path + '/', ''),
            name: child.code,
            component: modules[componentPath] || (() => import('@/views/dashboard/index.vue')),
            meta: { title: child.name, icon: child.icon, code: child.code }
          })
        }
      })
    }
    if (route.children.length > 0) {
      routes.push(route)
    }
  })
  routes.push({ path: '/:pathMatch(.*)*', redirect: '/404', hidden: true })
  return routes
}

let addRoutesFlag = false

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  if (to.path === '/login') {
    if (userStore.token) {
      next('/')
    } else {
      next()
    }
  } else {
    if (userStore.token) {
      if (!addRoutesFlag) {
        try {
          await userStore.loadUserData()
          addRoutesFlag = true
          next({ ...to, replace: true })
        } catch (error) {
          await userStore.logout()
          next('/login')
        }
      } else {
        next()
      }
    } else {
      next('/login?redirect=' + to.fullPath)
    }
  }
})

export function resetAddRoutesFlag() {
  addRoutesFlag = false
}

export default router
