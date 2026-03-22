<template>
    <div class="h-screen text-[var(--cosmic-text-light)] menu-container transition-all bg-[linear-gradient(180deg,rgba(247,250,255,0.95),rgba(238,245,253,0.92))] border-r border-[rgba(149,171,210,0.16)]"
        :style="{ width: menuStore.menuWidth, height: '100%' }">
        <!-- 顶部 Logo, 指定高度为 64px, 和右边的 Header 头保持一样高 -->
        <div class="flex h-[64px] items-center justify-center bg-[linear-gradient(180deg,rgba(242,247,254,0.96),rgba(235,243,252,0.88))]">
            <div v-if="!isCollapse" class="admin-brand admin-brand--icon-only">
                <img :src="brandLogo" class="admin-brand__logo admin-brand__logo--large" alt="ThoughtFlow" />
            </div>
            <div v-else class="admin-brand admin-brand--collapsed">
                <img :src="brandMark" class="admin-brand__mark" alt="ThoughtFlow" />
            </div>
        </div>

        <!-- 下方菜单 -->
        <el-menu 
            :default-active="defaultActive" 
            @select="handleSelect" 
            :collapse="isCollapse"
            :collapse-transition="false"
            background-color="transparent"
            text-color="#586f90"
            active-text-color="#4f70b3"
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMenuStore } from '@/stores/menu'
import { Monitor, Document, FolderOpened, PriceTag, User, Setting, Lock, Position, ChatLineRound } from '@element-plus/icons-vue'

// 图标映射对象
const iconMap = {
  Monitor,
  Document,
  FolderOpened,
  PriceTag,
  User,
  Setting,
  Lock,
  Position,
  ChatLineRound
}

// 引入 useMenuStore
const menuStore = useMenuStore()
const brandLogo = `${import.meta.env.BASE_URL}thoughtflow_logo.png`
const brandMark = `${import.meta.env.BASE_URL}thoughtflow-admin.svg`

const route = useRoute()
const router = useRouter()

// 菜单选择事件
const handleSelect = (path) => {
    router.push(path)
}

// 是否折叠
const isCollapse = computed(() => !(menuStore.menuWidth == '250px'))

// 根据路由地址判断哪个菜单被选中
const defaultActive = computed(() => route.path)
</script>

<style scoped>
.admin-brand {
    display: inline-flex;
    align-items: center;
    gap: 0.55rem;
    border-radius: 9999px;
    padding: 0.42rem 0.9rem;
    background: rgba(255, 255, 255, 0.72);
    border: 1px solid rgba(149, 171, 210, 0.16);
    box-shadow: 0 12px 28px rgba(120, 146, 186, 0.08);
}

.admin-brand--collapsed {
    padding: 0.45rem;
}

.admin-brand__logo {
    height: 26px;
    width: auto;
    object-fit: contain;
}

.admin-brand__logo--large {
    height: 30px;
}

.admin-brand__mark {
    height: 24px;
    width: 24px;
    object-fit: contain;
}

.admin-brand--icon-only {
    padding: 0.48rem 0.7rem;
}

:deep(.el-menu) {
    border-right: 0 !important;
}

:deep(.el-menu-item) {
    height: 50px !important;
    line-height: 50px !important;
}

:deep(.el-menu-item:hover) {
    background-color: rgba(148, 176, 231, 0.1) !important;
}

:deep(.el-menu-item.is-active) {
    background: linear-gradient(90deg, rgba(148, 176, 231, 0.2), rgba(255, 255, 255, 0.3)) !important;
    border-left: 4px solid #6e92d8;
}

:deep(.el-menu-item.is-active:hover) {
    background: linear-gradient(90deg, rgba(148, 176, 231, 0.24), rgba(255, 255, 255, 0.34)) !important;
}

:deep(.el-sub-menu__title) {
    height: 50px !important;
    line-height: 50px !important;
}

:deep(.el-sub-menu__title:hover) {
    background-color: rgba(148, 176, 231, 0.1) !important;
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
