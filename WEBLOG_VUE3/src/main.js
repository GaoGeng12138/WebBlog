import '@/assets/main.css'
import { createApp } from 'vue'
import App from '@/App.vue'
// 导入路由
import router from '@/router'
//导入Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
//导入Animate.css 动画库的样式
import 'animate.css'
//导入NProgress的样式
import 'nprogress/nprogress.css'
// 引入全局状态管理 Pinia
import pinia from '@/stores/index.js'
// 引入网站配置 store
import { useSiteConfigStore } from '@/stores/siteConfig'
// 图片点击放大
import 'viewerjs/dist/viewer.css'
import VueViewer from 'v-viewer'

const app = createApp(App)
//引入图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 应用路由
app.use(router)
app.use(pinia)
app.use(VueViewer)

// 初始化网站配置
const siteConfigStore = useSiteConfigStore()
siteConfigStore.initConfig()

app.mount('#app')

