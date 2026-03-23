<script setup>
// 引入组件
import AdminFooter from './components/AdminFooter.vue';
import AdminHeader from './components/AdminHeader.vue';
import AdminMenu from './components/AdminMenu.vue';
import AdminTagList from './components/AdminTagList.vue';
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
const menStore = useMenuStore()
const userStore = useUserStore()
const isMobile = ref(false)

const updateViewport = () => {
    isMobile.value = window.innerWidth < 768
    if (!isMobile.value) {
        menStore.closeMobileMenu()
    }
}

onMounted(() => {
    userStore.ensureUserInfoReady().catch((error) => {
        console.error('后台用户信息初始化失败:', error)
    })

    updateViewport()
    window.addEventListener('resize', updateViewport, { passive: true })
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', updateViewport)
})

const drawerVisible = computed({
    get: () => menStore.mobileMenuOpen,
    set: (value) => {
        if (value) {
            menStore.openMobileMenu()
        } else {
            menStore.closeMobileMenu()
        }
    }
})
</script>

<template>
    <!-- 外部容器 -->
    <el-container class="admin-shell h-screen md:h-screen">
        <!-- 左边侧边栏 -->
        <el-aside 
            v-if="!isMobile"
            :width="menStore.menuWidth" 
            class="transition-all shadow-[0_18px_38px_rgba(120,146,186,0.08)]"
        >
            <AdminMenu></AdminMenu>
        </el-aside>

        <!-- 右边主内容区域 -->
        <el-container>
            <!-- 顶栏容器 -->
            <el-header class="p-0 shadow-[0_14px_32px_rgba(120,146,186,0.08)]">
                <AdminHeader></AdminHeader>
                <AdminTagList></AdminTagList>
            </el-header>

            <!-- 主内容区域 -->
            <el-main class="p-0 bg-transparent">
                <!-- 主内容（根据路由动态展示不同页面） -->
                <router-view v-slot="{ Component, route }">
                    <Transition name="fade" mode="out-in">
                        <!-- max 指定最多缓存 10 个组件 -->
                        <KeepAlive :max="10">
                            <component :is="Component" :key="route.fullPath"></component>
                        </KeepAlive>
                    </Transition>
                </router-view>
            </el-main>

            <!-- 底栏容器 -->
            <el-footer class="p-0 border-t border-[rgba(149,171,210,0.16)] bg-transparent">
                <AdminFooter></AdminFooter>
            </el-footer>
        </el-container>

        <el-drawer
            v-model="drawerVisible"
            direction="ltr"
            size="82vw"
            :with-header="false"
            class="admin-mobile-drawer"
        >
            <AdminMenu />
        </el-drawer>
    </el-container>
</template>

<style scoped>
.admin-shell {
    overflow: hidden;
}

.el-header {
    padding: 0 !important;
    height: auto;
    background-color: transparent;
}

/* 内容区域过渡动画：淡入淡出效果 */
/* 刚开始进入时 */
.fade-enter-from {
    opacity: 0;
    transform: translateX(30px);
}

/* 刚开始结束 */
.fade-enter-to {
    opacity: 1;
    transform: translateX(0);
}

/* 刚开始离开 */
.fade-leave-from {
    opacity: 1;
    transform: translateX(0);
}

/* 离开已结束 */
.fade-leave-to {
    opacity: 0;
    transform: translateX(-30px);
}

/* 离开进行中 */
.fade-leave-active {
    transition: all 0.3s ease;
}

/* 进入进行中 */
.fade-enter-active {
    transition: all 0.3s ease;
}

@media (max-width: 768px) {
    .admin-shell :deep(.el-main) {
        overflow-x: hidden;
    }

    .admin-mobile-drawer :deep(.el-drawer__body) {
        padding: 0;
    }

    .admin-mobile-drawer :deep(.el-drawer__header) {
        display: none;
    }
}
</style>
