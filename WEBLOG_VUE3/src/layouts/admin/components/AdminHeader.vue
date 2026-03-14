<template>
    <!-- 设置背景色为白色、高度为 64px，padding-right 为4，border-bootom 为slate 100通过 flex 指定水平布局 -->
    <div class="bg-white h-[64px] flex pr-4 border-b border-gray-200 shadow-sm">
        <div class="w-[64px] h-[64px] cursor-pointer flex items-center justify-center text-gray-700 hover:bg-gray-100 transition-colors"
            @click="handleMenuCollapse">
            <!-- 左边栏收缩、展开 -->
            <el-icon class="text-xl">
                <Fold v-if="userMenu.menuWidth == '250px'" />
                <Expand v-else />
            </el-icon>
        </div>
        
        <!-- 右边容器，通过 ml-auto 让其在父容器的右边 -->
        <div class="ml-auto flex items-center gap-2">
            <!-- 点击刷新页面 -->
            <el-tooltip class="box-item" effect="dark" content="刷新" placement="bottom">
                <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-gray-700 hover:bg-gray-100 transition-colors rounded-md"
                    @click="handleRefresh">
                    <el-icon class="text-lg">
                        <Refresh />
                    </el-icon>
                </div>
            </el-tooltip>

            <!-- 点击全屏展示 -->
            <el-tooltip class="box-item" effect="dark" content="全屏" placement="bottom">
                <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-gray-700 hover:bg-gray-100 transition-colors rounded-md"
                    @click="toggle">
                    <el-icon class="text-lg">
                        <FullScreen v-if="!isFullscreen" />
                        <CircleClose v-else />
                    </el-icon>
                </div>
            </el-tooltip>

            <!-- Go to Frontend Button -->
            <el-tooltip class="box-item" effect="dark" content="回到前台" placement="bottom">
                <div class="w-[42px] h-[64px] cursor-pointer flex items-center justify-center text-gray-700 hover:bg-gray-100 transition-colors rounded-md"
                    @click="goToFrontend">
                    <el-icon class="text-lg">
                        <House />
                    </el-icon>
                </div>
            </el-tooltip>

            <!-- 登录用户头像 -->
            <el-dropdown class="flex items-center justify-center h-full" @command="handleCommand">
                <span class="el-dropdown-link flex items-center justify-center text-gray-700 text-sm cursor-pointer h-full px-3 hover:bg-gray-100 transition-colors rounded-md">
                    <!-- 头像 Avatar -->
                    <el-avatar class="mr-2" :size="32"
                        src="https://img.quanxiaoha.com/quanxiaoha/f97361c0429d4bb1bc276ab835843065.jpg" />
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
import { reactive, ref, watch, onBeforeUnmount, onMounted, } from 'vue';
import { useRouter } from 'vue-router';
import { updateAdminPassword } from '@/api/admin/user.js';
// Import House icon
import { House } from '@element-plus/icons-vue';

// 引入了用户 Store
const userStore = useUserStore()
const router = useRouter()

// 对话框是否显示
const dialogVisible = ref(false)

// isFullscreen 表示当前是否处于全屏；toggle 用于动态切换全屏、非全屏
const { isFullscreen, toggle } = useFullscreen()

// 刷新页面
const handleRefresh = () => location.reload()

// 回到前台页面
const goToFrontend = () => {
    // Navigate to the frontend home page
    window.open('/', '_blank');
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