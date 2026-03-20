import { useUserStore } from '@/stores/user.js'
import { defineStore } from 'pinia'
import { ref, watch, computed } from 'vue'

export const useMenuStore = defineStore('menu', () => {

  // 左边栏菜单默认宽度
  const menuWidth = ref("250px")
  const baseMenus = [
    {
      'name': '仪表盘',
      'icon': 'Monitor',
      'path': '/admin/index'
    },
    {
      'name': '文章管理',
      'icon': 'Document',
      'path': '/admin/article/list',
    },
    {
      'name': '分类管理',
      'icon': 'FolderOpened',
      'path': '/admin/category/list',
    },
    {
      'name': '标签管理',
      'icon': 'PriceTag',
      'path': '/admin/tag/list',
    },
    {
      'name': '用户管理',
      'icon': 'User',
      'path': '/admin/user/list',
    },
    {
      'name': '角色管理',
      'icon': 'Lock',
      'path': '/admin/role/list',
    },
    {
      'name': '访客记录',
      'icon': 'Position',
      'path': '/admin/visitor/list',
    },
  ]
  
  const userStore = useUserStore()
  
  // 使用计算属性来动态生成菜单列表
  const menus = computed(() => {
    const menuList = [...baseMenus]
    // 检查用户角色是否包含管理员权限
    if (userStore.userInfo && userStore.userInfo.roles && userStore.userInfo.roles.includes("ROLE_ADMIN")) {
      menuList.push({
        'name': '博客设置',
        'icon': 'Setting',
        'path': '/admin/blog/setting',
      })
    }
    return menuList
  })

  // 展开或伸缩左边栏菜单
  function handleMenuWidth() {
    menuWidth.value = menuWidth.value == '250px' ? '64px' : '250px'
  }

  return { menuWidth, handleMenuWidth, menus }
},
  {
    // 开启持久化
    persist: true,
  }
)
