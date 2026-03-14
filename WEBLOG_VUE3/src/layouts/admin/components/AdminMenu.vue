<template>
    <div class="bg-slate-800 h-screen text-white menu-container transition-all"
        :style="{ width: menuStore.menuWidth, height: '100%' }">
        <!-- 顶部 Logo, 指定高度为 64px, 和右边的 Header 头保持一样高 -->
        <div class="flex items-center justify-center h-[64px] bg-slate-900">
            <img src="@/assets/weblog-logo.png" class="h-[40px]" v-if="!isCollapse">
            <img src="@/assets/weblog-logo.png" class="h-[40px]" v-else>
        </div>

        <!-- 下方菜单 -->
        <el-menu 
            :default-active="defaultActive" 
            @select="handleSelect" 
            :collapse="isCollapse"
            :collapse-transition="false"
            background-color="#1e293b"
            text-color="#ffffff"
            active-text-color="#409eff"
            class="border-r-0"
        >
            <template v-for="(item, index) in menuStore.menus" :key="index">
                <el-menu-item :index="item.path">
                    <el-icon>
                        <!-- 动态图标 -->
                        <component :is="iconMap[item.icon]"></component>
                    </el-icon>
                    <span>{{ item.name }}</span>
                </el-menu-item>
            </template>
        </el-menu>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMenuStore } from '@/stores/menu'
import { Monitor, Document, FolderOpened, PriceTag, User, Setting, Lock } from '@element-plus/icons-vue'

// 图标映射对象
const iconMap = {
  Monitor,
  Document,
  FolderOpened,
  PriceTag,
  User,
  Setting,
  Lock
}

// 引入 useMenuStore
const menuStore = useMenuStore()

const route = useRoute()
const router = useRouter()

// 菜单选择事件
const handleSelect = (path) => {
    router.push(path)
}

// 是否折叠
const isCollapse = computed(() => !(menuStore.menuWidth == '250px'))

// 根据路由地址判断哪个菜单被选中
const defaultActive = ref(route.path)
</script>

<style scoped>
:deep(.el-menu) {
    border-right: 0 !important;
}

:deep(.el-menu-item) {
    height: 50px !important;
    line-height: 50px !important;
}

:deep(.el-menu-item:hover) {
    background-color: #334155 !important;
}

:deep(.el-menu-item.is-active) {
    background-color: #334155 !important;
    border-left: 4px solid #409eff;
}

:deep(.el-menu-item.is-active:hover) {
    background-color: #334155 !important;
}

:deep(.el-sub-menu__title) {
    height: 50px !important;
    line-height: 50px !important;
}

:deep(.el-sub-menu__title:hover) {
    background-color: #334155 !important;
}

/* 折叠菜单样式 */
:deep(.el-menu--collapse) {
    width: 64px;
}

:deep(.el-menu--collapse .el-menu-item) {
    text-align: center;
}

:deep(.el-menu--collapse .el-sub-menu__title) {
    text-align: center;
}
</style>