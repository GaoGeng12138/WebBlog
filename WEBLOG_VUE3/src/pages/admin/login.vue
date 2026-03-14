<template>
    <!-- 使用 grid 网格布局，并指定列数为 2，高度占满全屏 -->
    <div class="grid grid-cols-2 h-screen">

        <!-- 默认占两列，order 用于指定排列顺序，md 用于适配非移动端（PC 端） -->
        <div class="col-span-2 order-2 p-10 md:col-span-1 md:order-1 bg-slate-900">
            <!-- 指定为 flex 布局，并设置为屏幕垂直水平居中，高度为 100% -->
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__backInLeft animate__fast">
                <h2 class="font-bold text-4xl mb-7 text-white">Weblog 博客登录</h2>
                <p class="text-white">一款由 Spring Boot + Mybaits Plus + Vue 3.2 + Vite 4 开发的前后端分离博客。</p>
                <!-- 指定图片宽度为父级元素的 1/2 -->
                <img src="@/pics/developer.png" class="w-1/2 mt-10">
            </div>
        </div>


        <div class="col-span-2 order-1 md:col-span-1 md:order-2 bg-white">
            <!-- flex-col 是用来 指定子元素垂直排列 -->
            <div
                class="flex justify-center items-center h-full flex-col animate__animated animate__backInRight animate__fast">
                <h1 class="font-bold text-4xl mb-5">欢迎回来</h1>
                <div v-if="siteConfig.isFeatureEnabled('userRegisterEnabled')" class="flex items-center justify-center mb-7 text-gray-400 space-x-2">
                    <!-- 左边横线，高度为 1px, 宽度为 16，背景色设置 -->
                    <span class="h-[1px] w-16 bg-gray-200"></span>
                    <span class="text-gray-500">还没有账号？</span>
                    <router-link to="/register" class="text-blue-500 hover:underline">注册一个账号</router-link>
                    <!-- 右边横线 -->
                    <span class="h-[1px] w-16 bg-gray-200"></span>
                </div>
                <!-- 引入 Element Plus 表单组件，移动端设置宽度为 5/6，PC 端设置为 2/5 -->
                <el-form class="w-5/6 md:w-2/5" ref="formRef" :rules="rules" :model="form">
                    <el-form-item prop="username">
                        <!-- 输入框组件 -->
                        <el-input size="large" v-model="form.username" placeholder="请输入用户名" :prefix-icon="User"
                            clearable />
                    </el-form-item>
                    <el-form-item prop="password">
                        <!-- 密码框组件 -->
                        <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码"
                            :prefix-icon="Lock" clearable show-password />
                    </el-form-item>
                    <el-form-item>
                        <!-- 登录按钮，宽度设置为 100% -->
                        <el-button class="w-full mt-2" size="large" :loading="loading" type="primary"
                            @click="onSubmit">登录</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>



<script setup>
// 引入 Element Plus 中的用户、锁图标
import { login } from '@/api/admin/user';
import { setToken } from '@/composables/cookie.js';
import { showMessage } from '@/composables/util';
import { useUserStore } from '@/stores/user';
import { useSiteConfigStore } from '@/stores/siteConfig';
import { Lock, User } from '@element-plus/icons-vue';
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { storeToRefs } from 'pinia'
import { hasAdminRole } from '@/composables/role.js'

// 网站配置
const siteConfig = useSiteConfigStore()




// 定义表单验证规则
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符之间', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符之间', trigger: 'blur' }
    ]
}

// 定义表单数据
const form = reactive({
    username: '',
    password: ''
});

// 按回车键后，执行登录事件
function onKeyUp(e) {
    // console.log(e)
    if (e.key == 'Enter') {
        onSubmit()
    }
}

// 添加键盘监听
onMounted(() => {
    console.log('添加键盘监听')
    document.addEventListener('keyup', onKeyUp)
})

// 移除键盘监听
onBeforeUnmount(() => {
    document.removeEventListener('keyup', onKeyUp)
})

//表单引用
const formRef = ref(null);
//登录加载按钮
const loading = ref(false);

const router = useRouter();
const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore);

// 提交表单函数
const onSubmit = () => {

    //验证form表单字段
    formRef.value.validate((valid) => {
        if (!valid) {
            console.log('表单验证失败');
            return false;
        }
        //开始加载
        loading.value = true;

        //调用登录接口
        login(form.username, form.password).then(async response => {
            if (response.success) {
                // 登录成功后的逻辑
                showMessage('登录成功！');

                //存储token到cookie
                let token = response.data.token;
                setToken(token);

                //获取用户信息并存储到 Pinia (等待异步完成)
                await userStore.setUserInfo()
                console.log('用户信息:', userInfo.value);

                const isAdmin = hasAdminRole(userInfo.value);

                if (isAdmin) {
                    // 是管理员 -> 放行
                    router.push('/admin/index'); // 跳转到后台首页
                } else {
                    // 不是管理员 -> 踢回前台
                    router.push('/'); // 跳转到前台首页
                }

            } else {
                // 登录失败后的逻辑
                showMessage(response.message || '登录失败，请重试。', 'error');
            }
        }).catch(error => {
            console.error('登录失败:', error);
        }).finally(() => {
            //停止加载
            loading.value = false;
        });

    });
};
</script>
