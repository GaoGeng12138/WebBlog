<script setup>
// 引入组件
import AdminFooter from './components/AdminFooter.vue';
import AdminHeader from './components/AdminHeader.vue';
import AdminMenu from './components/AdminMenu.vue';
import AdminTagList from './components/AdminTagList.vue';
import { useMenuStore } from '@/stores/menu'
const menStore = useMenuStore()
</script>

<template>
    <!-- 外部容器 -->
    <el-container class="admin-shell h-screen">
        <!-- 左边侧边栏 -->
        <el-aside 
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
                <router-view v-slot="{ Component }">
                    <Transition name="fade" mode="out-in">
                        <!-- max 指定最多缓存 10 个组件 -->
                        <KeepAlive :max="10">
                            <component :is="Component"></component>
                        </KeepAlive>
                    </Transition>
                </router-view>
            </el-main>

            <!-- 底栏容器 -->
            <el-footer class="p-0 border-t border-[rgba(149,171,210,0.16)] bg-transparent">
                <AdminFooter></AdminFooter>
            </el-footer>
        </el-container>
    </el-container>
</template>

<style scoped>
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
</style>
