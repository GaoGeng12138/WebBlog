<template>
   <router-view></router-view>
</template>

<script setup>
import { watch } from 'vue'
import { useRoute } from 'vue-router'
import { useSiteConfigStore } from '@/stores/siteConfig'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const siteConfig = useSiteConfigStore()
const userStore = useUserStore()

// 监听路由变化，动态更新标题
watch(
  () => route.meta.title,
  (newTitle) => {
    if (newTitle) {
      // 后台管理系统
      if (route.path.startsWith('/admin')) {
        document.title = `${newTitle} - 后台管理系统`
      } 
      // 前台页面
      else {
        const userInfo = userStore.frontendUserInfo
        const nickname = userInfo?.nickname
        const siteTitle = siteConfig.config?.title || 'WebLog'
        
        // 如果用户已登录，显示昵称
        if (nickname) {
          document.title = `${newTitle} - ${nickname}のBlog`
        } else {
          document.title = `${newTitle} - ${siteTitle}`
        }
      }
    }
  },
  { immediate: true }
)

// 监听整个路由对象，处理动态路由（如文章详情）
watch(
  () => route.fullPath,
  () => {
    // 如果是文章详情页，稍后会通过 route.meta.title 更新
    if (route.meta.title) {
      const newTitle = route.meta.title
      if (route.path.startsWith('/admin')) {
        document.title = `${newTitle} - 后台管理系统`
      } else {
        const userInfo = userStore.frontendUserInfo
        const nickname = userInfo?.nickname
        const siteTitle = siteConfig.config?.title || 'WebLog'
        
        if (nickname) {
          document.title = `${newTitle} - ${nickname}のBlog`
        } else {
          document.title = `${newTitle} - ${siteTitle}`
        }
      }
    }
  }
)
</script>

<style scoped>

</style>


<style>
/* 自定义顶部加载 Loading 颜色 */
#nprogress .bar {
   background: #409eff!important;
}
</style>