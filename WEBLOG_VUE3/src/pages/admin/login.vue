<template>
    <div class="grid min-h-screen grid-cols-1 bg-[#fcf6ea] lg:grid-cols-[minmax(0,1.05fr)_minmax(420px,0.95fr)]">
        <div class="relative order-2 overflow-hidden border-t border-[#eadfca]/70 bg-[radial-gradient(circle_at_top_left,rgba(255,255,255,0.7),transparent_28%),linear-gradient(160deg,#fbf4e6_0%,#f7edd9_48%,#fdf8ef_100%)] px-8 py-12 lg:order-1 lg:border-r lg:border-t-0 lg:px-14 lg:py-16">
            <div class="absolute inset-0 pointer-events-none">
                <span class="absolute left-[12%] top-[18%] h-32 w-32 rounded-full bg-amber-200/20 blur-3xl"></span>
                <span class="absolute right-[10%] top-[12%] h-40 w-40 rounded-full bg-sky-200/18 blur-3xl"></span>
                <span class="absolute left-[18%] bottom-[16%] h-36 w-36 rounded-full bg-white/45 blur-3xl"></span>
            </div>

            <div class="relative flex h-full flex-col justify-between animate__animated animate__fadeInLeft animate__fast">
                <div class="max-w-[580px]">
                    <div class="inline-flex items-center gap-3 rounded-full border border-white/70 bg-white/65 px-4 py-2 text-[11px] font-medium uppercase tracking-[0.22em] text-slate-500 shadow-[0_10px_24px_rgba(15,23,42,0.04)] backdrop-blur">
                        <span class="h-2 w-2 rounded-full bg-amber-400"></span>
                        Thought Journal
                    </div>

                    <div class="mt-8 flex items-center gap-4">
                        <img src="@/assets/weblog-logo.png" class="h-12 w-auto object-contain lg:h-14" alt="ThoughtFlow" />
                        <div>
                            <h2 class="text-[2rem] font-semibold tracking-[-0.04em] text-slate-800 lg:text-[2.4rem]">ThoughtFlow</h2>
                            <p class="mt-1 text-[14px] leading-6 text-slate-500">记录 · 沉淀 · 让思考持续流动</p>
                        </div>
                    </div>

                    <div class="mt-10 max-w-[520px]">
                        <h3 class="text-[2.2rem] font-semibold leading-[1.18] tracking-[-0.05em] text-slate-800 lg:text-[2.75rem]">
                            管理你的内容宇宙，
                            <span class="bg-gradient-to-r from-amber-600 via-orange-500 to-sky-500 bg-clip-text text-transparent">让灵感有序落地。</span>
                        </h3>
                        <p class="mt-5 text-[15px] leading-8 text-slate-500">
                            在 ThoughtFlow 后台中整理文章、发布更新、维护分类与内容节奏。
                            整体风格保持轻盈、安静、可持续编辑的品牌感。
                        </p>
                    </div>
                </div>

                <div class="relative mt-12 grid gap-4 md:max-w-[560px] md:grid-cols-2">
                    <div class="rounded-[24px] border border-white/75 bg-white/72 p-5 shadow-[0_18px_40px_rgba(15,23,42,0.05)] backdrop-blur-xl">
                        <p class="text-[11px] font-medium uppercase tracking-[0.2em] text-slate-400">Flow Note</p>
                        <p class="mt-3 text-[16px] font-medium leading-7 text-slate-700">
                            把零散灵感整理成结构化文章，让每次发布都更有节奏。
                        </p>
                    </div>
                    <div class="rounded-[24px] border border-white/75 bg-white/68 p-5 shadow-[0_18px_40px_rgba(15,23,42,0.04)] backdrop-blur-xl">
                        <p class="text-[11px] font-medium uppercase tracking-[0.2em] text-slate-400">Brand Tone</p>
                        <p class="mt-3 text-[16px] font-medium leading-7 text-slate-700">
                            米白背景、轻字体、克制留白，保持属于 ThoughtFlow 的阅读气质。
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <div class="order-1 bg-[#fffdfa] lg:order-2">
            <div class="flex h-full items-center justify-center px-6 py-12 lg:px-12">
                <div class="w-full max-w-[460px] animate__animated animate__fadeInRight animate__fast">
                    <div class="rounded-[32px] border border-white/80 bg-white/88 px-8 py-10 shadow-[0_26px_70px_rgba(15,23,42,0.08)] backdrop-blur-xl sm:px-10">
                        <h1 class="text-[2.15rem] font-semibold tracking-[-0.04em] text-slate-800">欢迎回来</h1>
                        <p class="mt-2 text-[14px] leading-6 text-slate-400">登录你的 ThoughtFlow 账号</p>

                        <div v-if="siteConfig.isFeatureEnabled('userRegisterEnabled')" class="mt-7 flex items-center justify-center gap-2 text-[13px] text-slate-400">
                            <span class="h-px w-12 bg-[#eadfca]"></span>
                            <span>还没有账号？</span>
                            <router-link to="/register" class="font-medium text-amber-600 transition-colors hover:text-amber-500">注册一个账号</router-link>
                            <span class="h-px w-12 bg-[#eadfca]"></span>
                        </div>

                        <el-form class="mt-8" ref="formRef" :rules="rules" :model="form">
                            <el-form-item prop="username">
                                <el-input size="large" v-model="form.username" placeholder="请输入用户名" :prefix-icon="User"
                                    clearable />
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码"
                                    :prefix-icon="Lock" clearable show-password />
                            </el-form-item>
                            <el-form-item class="mb-0">
                                <el-button class="w-full !h-[46px] !rounded-2xl !border-0 !bg-slate-900 text-[14px] font-medium tracking-[0.02em] !shadow-[0_14px_30px_rgba(15,23,42,0.12)] transition-all duration-300 hover:!bg-amber-500 hover:!shadow-[0_18px_34px_rgba(245,158,11,0.22)]"
                                    :loading="loading" type="primary" @click="onSubmit">登录</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
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
