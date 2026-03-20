<template>
    <!-- 设置背景色为白色、高度为 64px，padding-right 为4，border-bootom 为slate 100通过 flex 指定水平布局 -->
    <div class="h-[64px] flex pr-4 border-b border-[rgba(149,171,210,0.18)] shadow-[0_12px_24px_rgba(120,146,186,0.1)] bg-[linear-gradient(180deg,rgba(248,251,255,0.94),rgba(240,246,253,0.86))] backdrop-blur-xl">
        <div class="w-[64px] h-[64px] cursor-pointer flex items-center justify-center text-[var(--cosmic-text-light)] transition-colors"
            @click="handleMenuCollapse">
            <!-- 左边栏收缩、展开 -->
            <div class="admin-header-icon-button admin-header-icon-button--menu">
                <el-icon class="text-[18px]">
                <Fold v-if="userMenu.menuWidth == '250px'" />
                <Expand v-else />
                </el-icon>
            </div>
        </div>
        
        <!-- 右边容器，通过 ml-auto 让其在父容器的右边 -->
        <div class="ml-auto flex items-center gap-2">
            <!-- 点击刷新页面 -->
            <el-tooltip class="box-item" effect="dark" content="刷新" placement="bottom">
                <div class="admin-header-action"
                    @click="handleRefresh">
                    <span class="admin-header-action__halo"></span>
                    <el-icon class="admin-header-action__icon text-[17px]">
                        <RefreshRight />
                    </el-icon>
                </div>
            </el-tooltip>

            <!-- 点击全屏展示 -->
            <el-tooltip class="box-item" effect="dark" content="全屏" placement="bottom">
                <div class="admin-header-action"
                    @click="toggle">
                    <span class="admin-header-action__halo"></span>
                    <el-icon class="admin-header-action__icon text-[17px]">
                        <FullScreen v-if="!isFullscreen" />
                        <ScaleToOriginal v-else />
                    </el-icon>
                </div>
            </el-tooltip>

            <!-- Go to Frontend Button -->
            <el-tooltip class="box-item" effect="dark" content="回到前台" placement="bottom">
                <div class="admin-header-action"
                    @click="goToFrontend">
                    <span class="admin-header-action__halo"></span>
                    <el-icon class="admin-header-action__icon text-[17px]">
                        <TopRight />
                    </el-icon>
                </div>
            </el-tooltip>

            <!-- 登录用户头像 -->
            <el-dropdown class="flex items-center justify-center h-full" @command="handleCommand">
                <span class="el-dropdown-link flex items-center justify-center text-[var(--cosmic-text-light)] text-sm cursor-pointer h-full px-3 transition-colors rounded-2xl outline-none hover:bg-[rgba(255,255,255,0.58)]" style="outline: none !important;">
                    <!-- 头像 Avatar -->
                    <img
                        :src="displayAvatar"
                        @error="handleAvatarError"
                        alt="avatar"
                        class="mr-2 w-8 h-8 rounded-full object-cover border border-[rgba(149,171,210,0.28)] shadow-[0_0_0_4px_rgba(255,255,255,0.42)]" />
                    <span class="hidden md:inline mr-1">{{ userStore.userInfo.username }}</span>
                    <el-icon class="el-icon--right hidden md:inline">
                        <arrow-down />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="updatePassword">修改密码</el-dropdown-item>
                        <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>

            <el-dialog v-model="dialogVisible" title="修改密码" width="40%" :draggable="true" append-to-body
                :close-on-click-modal="false" :close-on-press-escape="false">
                <el-form ref="formRef" :rules="rules" :model="form">
                    <el-form-item label="用户名" prop="username" label-width="120px">
                        <!-- 输入框组件 -->
                        <el-input size="large" v-model="form.username" placeholder="请输入用户名" clearable disabled />
                    </el-form-item>
                    <el-form-item label="旧密码" prop="oldPassword" label-width="120px">
                        <el-input size="large" type="password" v-model="form.oldPassword" placeholder="请输入旧密码" clearable
                            show-password />
                    </el-form-item>
                    <el-form-item label="密码" prop="password" label-width="120px">
                        <el-input size="large" type="password" v-model="form.password" placeholder="请输入新密码" clearable
                            show-password />
                    </el-form-item>
                    <el-form-item label="确认密码" prop="confirmPassword" label-width="120px">
                        <el-input size="large" type="password" v-model="form.confirmPassword" placeholder="请确认新密码"
                            clearable show-password />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="onSubmit">
                            提交
                        </el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<script setup>
import { useMenuStore } from '@/stores/menu';
// 引入 useFullscreen
import { showMessage, showModel } from '@/composables/util';
import { useUserStore } from '@/stores/user.js';
import { useFullscreen } from '@vueuse/core';
import { computed, reactive, ref, watch, onBeforeUnmount, onMounted, } from 'vue';
import { useRouter } from 'vue-router';
import { updateAdminPassword } from '@/api/admin/user.js';
import { RefreshRight, ScaleToOriginal, TopRight } from '@element-plus/icons-vue';

// 引入了用户 Store
const userStore = useUserStore()
const router = useRouter()
const defaultAvatar = `${import.meta.env.BASE_URL}default-avatar.svg`
const displayAvatar = computed(() => userStore.userInfo?.avatar || defaultAvatar)

// 对话框是否显示
const dialogVisible = ref(false)

// isFullscreen 表示当前是否处于全屏；toggle 用于动态切换全屏、非全屏
const { isFullscreen, toggle } = useFullscreen()

// 刷新页面
const handleRefresh = () => location.reload()

const handleAvatarError = (event) => {
    if (event.target.src.endsWith('default-avatar.svg')) {
        return
    }
    event.target.src = defaultAvatar
}

// 回到前台页面
const goToFrontend = () => {
    const frontendHome = router.resolve({ path: '/' }).href
    window.open(frontendHome, '_blank')
}

//引入菜单 store
const userMenu = useMenuStore();

//icon 点击事件
const handleMenuCollapse = () => {
    userMenu.handleMenuWidth();
};

// 表单引用
const formRef = ref(null)

// 修改用户密码表单对象
const form = reactive({
    username: userStore.userInfo.username || '',
    oldPassword: "",
    password: '',
    confirmPassword: ''
})

// 规则校验
const rules = {
    username: [
        {
            required: true,
            message: '用户名不能为空',
            trigger: 'blur'
        }
    ],
    oldPassword: [
        {
            required: true,
            message: '旧密码不能为空',
            trigger: 'blur',
        },
    ],
    password: [
        {
            required: true,
            message: '新密码不能为空',
            trigger: 'blur',
        },
    ],
    confirmPassword: [
        {
            required: true,
            message: '确认新密码不能为空',
            trigger: 'blur',
        },
    ]
}


// 下拉菜单事件处理
const handleCommand = (command) => {
    // 更新密码
    if (command == 'updatePassword') {
        // 显示对话框
        dialogVisible.value = true
    } else if (command == 'logout') {
        // 退出登录
        logout()
    }
}

// 监听 Pinia store 中的某个值的变化
watch(() => userStore.userInfo.username, (newValue, oldValue) => {
    // 在这里处理变化后的值
    form.username = newValue;
})

// 页面卸载前清除定时器
onBeforeUnmount(() => {

})

onMounted(() => {

})

// 修改密码提交
const onSubmit = () => {
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证不通过')
            return false
        }

        updateAdminPassword(form).then((res) => {
            if (res.success == true) {
                showMessage('密码修改成功')
                dialogVisible.value = false
            } else {
                // 获取服务端返回的错误消息
                let message = res.message
                // 提示错误消息
                showMessage(message, 'error')
            }
        })
    })
}

// 退出登录
const logout = () => {
    showModel("是否确定要退出登录？").then(() => {
        // 清除用户信息
        userStore.logout()
        // 跳转到登录页
        router.push("/login")
    }).catch(() => {

    })
}
</script>

<style scoped>
.admin-header-icon-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: 16px;
    border: 1px solid rgba(149, 171, 210, 0.16);
    background: rgba(255, 255, 255, 0.54);
    box-shadow: 0 10px 24px rgba(120, 146, 186, 0.08);
    transition: transform 0.28s ease, background 0.28s ease, box-shadow 0.28s ease, border-color 0.28s ease;
}

.admin-header-icon-button:hover,
.admin-header-action:hover {
    transform: translateY(-1px);
    background: rgba(255, 255, 255, 0.82);
    border-color: rgba(116, 149, 195, 0.22);
    box-shadow: 0 16px 30px rgba(120, 146, 186, 0.12);
}

.admin-header-action {
    position: relative;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 42px;
    height: 42px;
    cursor: pointer;
    color: var(--cosmic-text-light);
    border-radius: 18px;
    border: 1px solid rgba(149, 171, 210, 0.18);
    background:
        linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(244, 248, 253, 0.82));
    box-shadow:
        inset 0 1px 0 rgba(255, 255, 255, 0.82),
        0 10px 24px rgba(120, 146, 186, 0.08);
    transition: transform 0.28s ease, background 0.28s ease, box-shadow 0.28s ease, border-color 0.28s ease, color 0.28s ease;
}

.admin-header-action::before {
    content: "";
    position: absolute;
    inset: 1px;
    border-radius: 17px;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.62), rgba(255, 255, 255, 0.16));
    opacity: 0.7;
    pointer-events: none;
}

.admin-header-action__halo {
    position: absolute;
    inset: auto auto -14px -8px;
    width: 38px;
    height: 20px;
    border-radius: 999px;
    background: radial-gradient(circle, rgba(148, 176, 231, 0.32), transparent 72%);
    opacity: 0;
    transition: opacity 0.28s ease, transform 0.28s ease;
    pointer-events: none;
}

.admin-header-action__icon {
    position: relative;
    z-index: 1;
    transition: transform 0.28s ease, color 0.28s ease;
}

.admin-header-action:hover .admin-header-action__icon {
    transform: scale(1.06);
    color: var(--cosmic-blue-deep);
}

.admin-header-action:hover .admin-header-action__halo {
    opacity: 1;
    transform: translate3d(2px, -2px, 0);
}
</style>
