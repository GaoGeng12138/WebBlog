<template>
    <div class="grid min-h-screen grid-cols-1 bg-[linear-gradient(180deg,#f7fbff_0%,#eef4fb_100%)] lg:grid-cols-[minmax(0,1.05fr)_minmax(420px,0.95fr)]">
        <div class="relative order-2 overflow-hidden border-t border-[rgba(149,171,210,0.16)] bg-[radial-gradient(circle_at_top_left,rgba(173,202,243,0.32),transparent_28%),radial-gradient(circle_at_bottom_right,rgba(255,255,255,0.56),transparent_24%),linear-gradient(160deg,#edf4fb_0%,#e5eef9_48%,#f7fbff_100%)] px-8 py-12 lg:order-1 lg:border-r lg:border-t-0 lg:px-14 lg:py-16">
            <div class="absolute inset-0 pointer-events-none">
                <span class="absolute left-[12%] top-[18%] h-32 w-32 rounded-full bg-[rgba(148,176,231,0.24)] blur-3xl"></span>
                <span class="absolute right-[10%] top-[12%] h-40 w-40 rounded-full bg-[rgba(197,218,248,0.24)] blur-3xl"></span>
                <span class="absolute left-[18%] bottom-[16%] h-36 w-36 rounded-full bg-[rgba(255,255,255,0.55)] blur-3xl"></span>
                <span class="login-orb login-orb--one"></span>
                <span class="login-orb login-orb--two"></span>
                <span class="login-orb login-orb--three"></span>
            </div>

            <div class="relative flex h-full flex-col justify-center animate__animated animate__fadeInLeft animate__fast">
                <div class="max-w-[560px]">
                    <div class="ui-caps inline-flex items-center gap-3 rounded-full border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.66)] px-4 py-2 text-[11px] text-[var(--cosmic-muted)] shadow-[0_10px_24px_rgba(120,146,186,0.08)] backdrop-blur">
                        <span class="h-2 w-2 rounded-full bg-[var(--cosmic-blue)]"></span>
                        Unified Access
                    </div>

                    <div class="mt-10">
                        <h2 class="brand-wordmark text-[2.1rem] text-slate-800 lg:text-[2.7rem]">ThoughtFlow</h2>
                        <p class="mt-2 text-[14px] leading-6 text-[var(--cosmic-text-light-muted)]">记录灵感，继续写下去</p>
                    </div>

                    <div class="mt-8 max-w-[520px]">
                        <h3 class="text-[2.15rem] font-semibold leading-[1.14] tracking-[-0.06em] text-[var(--cosmic-text-light)] lg:text-[2.85rem]">
                            为今天的表达，
                            <span class="bg-gradient-to-r from-[var(--cosmic-blue-deep)] via-[var(--cosmic-blue)] to-[var(--cosmic-blue-soft)] bg-clip-text text-transparent">留出一页安静空间。</span>
                        </h3>
                        <p class="mt-5 max-w-[460px] text-[15px] leading-8 text-[var(--cosmic-text-light-muted)]">
                            登录前不必急着把一切想清楚。
                            先记下一句感受、一个标题、一个正在发酵的念头，
                            很多作品都是从这样轻的一笔开始生长。
                        </p>
                    </div>

                    <div class="mt-8 grid gap-4 md:grid-cols-2">
                        <div class="login-scene-card login-scene-card--primary">
                            <p class="ui-caps text-[11px] text-[var(--cosmic-muted)]">Daily Note</p>
                            <h4 class="mt-3 text-[1.05rem] font-semibold text-slate-800">“{{ todayQuote.text }}”</h4>
                            <p class="mt-2 text-[13px] leading-6 text-slate-500">今日主题：{{ todayQuote.tag }} · {{ todayLabel }}</p>
                        </div>
                        <div class="login-scene-card">
                            <p class="ui-caps text-[11px] text-[var(--cosmic-muted)]">Writing Prompt</p>
                            <h4 class="mt-3 text-[1.05rem] font-semibold text-slate-800">{{ writingPrompt.title }}</h4>
                            <p class="mt-2 text-[13px] leading-6 text-slate-500">{{ writingPrompt.description }}</p>
                        </div>
                    </div>

                    <div class="mt-8 flex flex-wrap gap-3">
                        <span v-for="item in loginHighlights" :key="item" class="login-highlight-chip">{{ item }}</span>
                    </div>

                    <div class="pointer-events-none relative mt-10 hidden min-h-[160px] lg:block">
                        <div class="login-float-card left-4 top-0">
                            <span class="login-float-card__label">Moment</span>
                            <strong>先写一句</strong>
                            <p>一句完整的话，往往就是一篇内容最初的入口。</p>
                        </div>
                        <div class="login-float-card login-float-card--secondary left-[210px] top-10">
                            <span class="login-float-card__label">Rhythm</span>
                            <strong>保持更新</strong>
                            <p>创作不必盛大，稳定输出本身就会形成节奏。</p>
                        </div>
                        <div class="login-float-card login-float-card--tertiary left-[110px] top-[92px]">
                            <span class="login-float-card__label">Spark</span>
                            <strong>留下火花</strong>
                            <p>把稍纵即逝的念头写下来，它就有机会长成作品。</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="order-1 bg-[rgba(248,252,255,0.92)] lg:order-2">
            <div class="flex h-full items-center justify-center px-6 py-12 lg:px-12">
                <div class="w-full max-w-[460px] animate__animated animate__fadeInRight animate__fast">
                    <div class="rounded-[32px] border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.8)] px-8 py-10 shadow-[0_26px_60px_rgba(120,146,186,0.1)] backdrop-blur-xl sm:px-10">
                        <h1 class="brand-wordmark text-[2.15rem] text-[var(--cosmic-text-light)]">欢迎回来</h1>
                        <p class="mt-2 text-[14px] leading-6 text-[var(--cosmic-text-light-muted)]">登录你的 ThoughtFlow 账号</p>

                        <div v-if="siteConfig.isFeatureEnabled('userRegisterEnabled')" class="mt-7 flex items-center justify-center gap-2 text-[13px] text-[var(--cosmic-muted)]">
                            <span class="h-px w-12 bg-[rgba(149,171,210,0.22)]"></span>
                            <span>还没有账号？</span>
                            <router-link to="/register" class="font-medium text-[var(--cosmic-blue-deep)] transition-colors hover:text-[var(--cosmic-blue)]">注册一个账号</router-link>
                            <span class="h-px w-12 bg-[rgba(149,171,210,0.22)]"></span>
                        </div>

                        <el-form class="login-form mt-8" ref="formRef" :rules="rules" :model="form">
                            <el-form-item prop="username">
                                <el-input size="large" v-model="form.username" placeholder="请输入用户名" :prefix-icon="User"
                                    clearable />
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码"
                                    :prefix-icon="Lock" clearable show-password />
                            </el-form-item>
                            <el-form-item class="mb-0">
                                <el-button class="theme-btn-primary w-full !h-[46px] !rounded-2xl !border-0 text-[14px] font-medium tracking-[0.02em] transition-all duration-300"
                                    :loading="loading" type="primary" @click="onSubmit">登录</el-button>
                            </el-form-item>
                        </el-form>

                        <div class="mt-6 flex items-center justify-between rounded-2xl border border-[rgba(149,171,210,0.14)] bg-[rgba(244,248,252,0.72)] px-4 py-3 text-[12px] text-[var(--cosmic-muted)]">
                            <span>今天也适合写下一点什么</span>
                            <span class="inline-flex items-center gap-1.5 text-[var(--cosmic-blue-deep)]">
                                <span class="h-2 w-2 rounded-full bg-[var(--cosmic-blue)] animate-pulse"></span>
                                Ready to Write
                            </span>
                        </div>
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

const today = new Date()

const quotePool = [
    { text: '别急着写得完整，先把最想说的那一句留下来。', tag: '起笔' },
    { text: '写作不是证明自己，而是把心里的轮廓慢慢照亮。', tag: '表达' },
    { text: '持续记录的人，最终会拥有比灵感更可靠的节奏。', tag: '沉淀' },
    { text: '很多好作品的开始，只是认真对待了一次微小感受。', tag: '灵感' },
    { text: '先写下草稿，再给它时间，文字会自己长出方向。', tag: '生长' },
    { text: '别怕不够成熟，能被记录下来的想法就已经值得。', tag: '创作' }
]

const promptPool = [
    {
        title: '如果今天只写 100 字，你最想留下什么？',
        description: '可以是一段情绪、一句判断，或者一个尚未成形的标题。'
    },
    {
        title: '最近哪件小事，让你突然想说点什么？',
        description: '从最具体的场景开始，往往比直接讲大道理更有力量。'
    },
    {
        title: '把一个模糊念头，先写成一句清晰的话。',
        description: '当它能被完整说出来，作品就已经开始了。'
    }
]

const quoteIndex = (today.getFullYear() * 10000 + (today.getMonth() + 1) * 100 + today.getDate()) % quotePool.length
const promptIndex = (today.getFullYear() * 10000 + (today.getMonth() + 1) * 100 + today.getDate()) % promptPool.length

const todayQuote = quotePool[quoteIndex]
const writingPrompt = promptPool[promptIndex]
const todayLabel = new Intl.DateTimeFormat('zh-CN', {
    month: 'numeric',
    day: 'numeric'
}).format(today)

const loginHighlights = [
    '每日一言',
    '写作提示',
    '灵感记录',
    '持续创作'
]




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
            const message = error?.response?.data?.message || error?.message || '登录请求未成功发出，请检查接口地址或网络配置。'
            showMessage(message, 'error');
        }).finally(() => {
            //停止加载
            loading.value = false;
        });

    });
};
</script>

<style scoped>
.login-orb {
    position: absolute;
    border-radius: 9999px;
    filter: blur(12px);
    opacity: 0.55;
    animation: loginFloat 8s ease-in-out infinite;
}

.login-orb--one {
    left: 10%;
    bottom: 18%;
    width: 14px;
    height: 14px;
    background: rgba(116, 149, 195, 0.35);
}

.login-orb--two {
    right: 18%;
    top: 28%;
    width: 12px;
    height: 12px;
    background: rgba(162, 187, 226, 0.4);
    animation-delay: -2s;
}

.login-orb--three {
    left: 42%;
    top: 16%;
    width: 10px;
    height: 10px;
    background: rgba(255, 255, 255, 0.82);
    animation-delay: -4s;
}

.login-scene-card {
    position: relative;
    overflow: hidden;
    border-radius: 24px;
    border: 1px solid rgba(149, 171, 210, 0.16);
    background: rgba(255, 255, 255, 0.7);
    padding: 1.1rem 1.15rem;
    box-shadow: 0 18px 40px rgba(120, 146, 186, 0.08);
    backdrop-filter: blur(18px);
    transition: transform 0.35s ease, box-shadow 0.35s ease, border-color 0.35s ease;
}

.login-scene-card::after {
    content: "";
    position: absolute;
    inset: auto -20% -55% auto;
    width: 120px;
    height: 120px;
    border-radius: 9999px;
    background: radial-gradient(circle, rgba(116, 149, 195, 0.16), transparent 70%);
}

.login-scene-card:hover {
    transform: translateY(-3px);
    border-color: rgba(116, 149, 195, 0.24);
    box-shadow: 0 22px 46px rgba(120, 146, 186, 0.13);
}

.login-scene-card--primary {
    background: linear-gradient(160deg, rgba(255, 255, 255, 0.82), rgba(238, 244, 252, 0.94));
}

.login-highlight-chip {
    display: inline-flex;
    align-items: center;
    border-radius: 9999px;
    border: 1px solid rgba(149, 171, 210, 0.18);
    background: rgba(255, 255, 255, 0.66);
    padding: 0.55rem 0.85rem;
    font-size: 12px;
    line-height: 1;
    color: #607089;
    box-shadow: 0 10px 24px rgba(120, 146, 186, 0.06);
    backdrop-filter: blur(12px);
}

.login-float-card {
    position: absolute;
    width: 176px;
    border-radius: 22px;
    border: 1px solid rgba(149, 171, 210, 0.16);
    background: rgba(255, 255, 255, 0.72);
    padding: 0.95rem 1rem;
    box-shadow: 0 22px 48px rgba(120, 146, 186, 0.12);
    backdrop-filter: blur(20px);
    animation: driftCard 9s ease-in-out infinite;
}

.login-float-card--secondary {
    animation-delay: -3s;
}

.login-float-card--tertiary {
    animation-delay: -6s;
}

.login-float-card__label {
    display: block;
    margin-bottom: 0.45rem;
    font-size: 10px;
    font-weight: 600;
    letter-spacing: 0.2em;
    text-transform: uppercase;
    color: #8195b6;
}

.login-float-card strong {
    display: block;
    font-size: 15px;
    font-weight: 600;
    color: #223752;
}

.login-float-card p {
    margin-top: 0.4rem;
    font-size: 12px;
    line-height: 1.65;
    color: #6a7b93;
}

.login-form :deep(.el-input__wrapper) {
    min-height: 48px;
    border-radius: 18px;
    box-shadow: 0 0 0 1px rgba(149, 171, 210, 0.16);
    background: rgba(250, 252, 255, 0.92);
    transition: box-shadow 0.3s ease, transform 0.3s ease, background 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
    background: white;
    box-shadow: 0 0 0 1px rgba(116, 149, 195, 0.22);
}

.login-form :deep(.is-focus .el-input__wrapper) {
    background: white;
    box-shadow: 0 0 0 4px rgba(116, 149, 195, 0.12);
    transform: translateY(-1px);
}

@keyframes loginFloat {
    0%, 100% {
        transform: translate3d(0, 0, 0);
    }

    50% {
        transform: translate3d(0, -8px, 0);
    }
}

@keyframes driftCard {
    0%, 100% {
        transform: translate3d(0, 0, 0);
    }

    50% {
        transform: translate3d(0, -6px, 0);
    }
}
</style>
