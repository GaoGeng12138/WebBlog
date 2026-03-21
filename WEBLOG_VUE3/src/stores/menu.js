import { useUserStore } from '@/stores/user.js'
import { hasAccess } from '@/composables/permission'
import { defineStore } from 'pinia'
import { ref, watch, computed } from 'vue'

export const useMenuStore = defineStore('menu', () => {

  // 左边栏菜单默认宽度
  const menuWidth = ref("250px")
  const baseMenus = [
    {
      'name': '仪表盘',
      'icon': 'Monitor',
      'path': '/admin/index',
      'permission': 'admin:dashboard:view'
    },
    {
      'name': '文章管理',
      'icon': 'Document',
      'path': '/admin/article/list',
      'permission': 'admin:article:list'
    },
    {
      'name': '分类管理',
      'icon': 'FolderOpened',
      'path': '/admin/category/list',
      'permission': 'admin:category:list'
    },
    {
      'name': '标签管理',
      'icon': 'PriceTag',
      'path': '/admin/tag/list',
      'permission': 'admin:tag:list'
    },
    {
      'name': '用户管理',
      'icon': 'User',
      'path': '/admin/user/list',
      'permission': 'admin:user:list'
    },
    {
      'name': '角色管理',
      'icon': 'Lock',
      'path': '/admin/role/list',
      'permission': 'admin:role:list'
    },
    {
      'name': '访客记录',
      'icon': 'Position',
      'path': '/admin/visitor/list',
      'permission': 'admin:visitor:list'
    },
  ]
  
  const userStore = useUserStore()
  
  // 使用计算属性来动态生成菜单列表
  const menus = computed(() => {
    const menuList = [...baseMenus, {
      'name': '博客设置',
      'icon': 'Setting',
      'path': '/admin/blog/setting',
      'permission': 'admin:setting:view'
    }]

    return menuList.filter(menu => hasAccess(userStore.userInfo, menu.permission))
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
