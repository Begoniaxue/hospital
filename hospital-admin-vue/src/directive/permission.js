import { useUserStore } from '@/store/modules/user'

export const permission = {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions

    if (value && value.length > 0) {
      const hasPermission = permissions.some(p => {
        return value.includes(p)
      })
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  }
}

export function hasPermission(value) {
  const userStore = useUserStore()
  const permissions = userStore.permissions
  if (value && value.length > 0) {
    return permissions.some(p => value.includes(p))
  }
  return true
}

export default {
  install(app) {
    app.directive('permission', permission)
    app.config.globalProperties.$hasPermission = hasPermission
  }
}
