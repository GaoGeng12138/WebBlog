<template>
    <div class="min-h-screen bg-[linear-gradient(180deg,#f7fbff_0%,#eef4fb_100%)]">
        <div class="grid min-h-screen grid-cols-1 lg:grid-cols-[minmax(0,1.05fr)_minmax(420px,0.95fr)]">
        <div class="relative hidden overflow-hidden border-t border-[rgba(149,171,210,0.16)] bg-[radial-gradient(circle_at_top_left,rgba(173,202,243,0.32),transparent_28%),radial-gradient(circle_at_bottom_right,rgba(255,255,255,0.56),transparent_24%),linear-gradient(160deg,#edf4fb_0%,#e5eef9_48%,#f7fbff_100%)] px-8 py-12 lg:block lg:border-r lg:border-t-0 lg:px-14 lg:py-16">
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

        <div class="relative bg-[rgba(248,252,255,0.92)]">
            <div class="flex h-full items-center justify-center px-4 py-6 sm:px-6 sm:py-10 lg:px-12 lg:py-12">
                <div class="w-full max-w-[460px] animate__animated animate__fadeInRight animate__fast">
                    <div class="mobile-login-hero md:hidden">
                        <div class="mobile-login-hero__badge">
                            <span class="h-2 w-2 rounded-full bg-[var(--cosmic-blue)]"></span>
                            Unified Access
                        </div>
                        <h1 class="brand-wordmark mobile-login-hero__title">ThoughtFlow</h1>
                        <p class="mobile-login-hero__subtitle">记录灵感，继续写下去</p>
                        <p class="mobile-login-hero__lead">今天先登录，再把想法慢慢写成作品。</p>
                        <div class="mobile-login-hero__chips">
                            <span v-for="item in loginHighlights.slice(0, 3)" :key="item" class="mobile-login-hero__chip">
                                {{ item }}
                            </span>
                        </div>
                    </div>

                    <div class="login-card rounded-[28px] border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.84)] px-5 py-6 shadow-[0_26px_60px_rgba(120,146,186,0.1)] backdrop-blur-xl sm:px-8 sm:py-9 md:rounded-[32px] md:px-8 md:py-10">
                        <h1 class="brand-wordmark text-[2rem] leading-none text-[var(--cosmic-text-light)] sm:text-[2.15rem]">欢迎回来</h1>
                        <p class="mt-2 text-[14px] leading-6 text-[var(--cosmic-text-light-muted)]">登录你的 ThoughtFlow 账号</p>

                        <div v-if="siteConfig.isFeatureEnabled('userRegisterEnabled')" class="mt-5 flex items-center justify-center gap-2 text-[13px] text-[var(--cosmic-muted)] sm:mt-7">
                            <span class="hidden h-px w-12 bg-[rgba(149,171,210,0.22)] sm:block"></span>
                            <span>还没有账号？</span>
                            <router-link to="/register" class="font-medium text-[var(--cosmic-blue-deep)] transition-colors hover:text-[var(--cosmic-blue)]">注册一个账号</router-link>
                            <span class="hidden h-px w-12 bg-[rgba(149,171,210,0.22)] sm:block"></span>
                        </div>

                        <el-form class="login-form mt-6 sm:mt-8" ref="formRef" :rules="rules" :model="form">
                            <el-form-item prop="username">
                                <div ref="usernameShellRef" class="login-cyber-input" :class="{ 'is-typing': typingField === 'username' }">
                                    <div class="login-cyber-input__burst-layer" aria-hidden="true">
                                        <span
                                            v-for="particle in usernameBurstParticles"
                                            :key="particle.id"
                                            class="login-cyber-input__burst-particle"
                                            :class="particle.className"
                                            :style="particle.style"
                                        ></span>
                                    </div>
                                    <span class="login-cyber-input__pixel login-cyber-input__pixel--a"></span>
                                    <span class="login-cyber-input__pixel login-cyber-input__pixel--b"></span>
                                    <span class="login-cyber-input__pixel login-cyber-input__pixel--c"></span>
                                    <span class="login-cyber-input__pixel login-cyber-input__pixel--d"></span>
                                    <el-input size="large" v-model="form.username" placeholder="请输入用户名" :prefix-icon="User"
                                        clearable @input="triggerTypingEffect('username')" />
                                </div>
                            </el-form-item>
                            <el-form-item prop="password">
                                <div ref="passwordShellRef" class="login-cyber-input" :class="{ 'is-typing': typingField === 'password' }">
                                    <div class="login-cyber-input__burst-layer" aria-hidden="true">
                                        <span
                                            v-for="particle in passwordBurstParticles"
                                            :key="particle.id"
                                            class="login-cyber-input__burst-particle"
                                            :class="particle.className"
                                            :style="particle.style"
                                        ></span>
                                    </div>
                                    <span class="login-cyber-input__pixel login-cyber-input__pixel--a"></span>
                                    <span class="login-cyber-input__pixel login-cyber-input__pixel--b"></span>
                                    <span class="login-cyber-input__pixel login-cyber-input__pixel--c"></span>
                                    <span class="login-cyber-input__pixel login-cyber-input__pixel--d"></span>
                                    <el-input size="large" type="password" v-model="form.password" placeholder="请输入密码"
                                        :prefix-icon="Lock" clearable show-password @input="triggerTypingEffect('password')" />
                                </div>
                            </el-form-item>
                            <el-form-item class="mb-0">
                                <el-button class="theme-btn-primary w-full !h-[48px] !rounded-2xl !border-0 text-[14px] font-medium tracking-[0.02em] transition-all duration-300"
                                    :loading="loading" type="primary" @click="onSubmit">登录</el-button>
                            </el-form-item>
                        </el-form>

                        <div class="login-footnote mt-5 flex items-center justify-between rounded-2xl border border-[rgba(149,171,210,0.14)] bg-[rgba(244,248,252,0.72)] px-4 py-3 text-[12px] text-[var(--cosmic-muted)]">
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
    </div>
</template>



<script setup>
// 引入 Element Plus 中的用户、锁图标
import { login } from '@/api/admin/user';
import { setToken } from '@/composables/cookie.js';
import { showMessage } from '@/composables/util';
import { useUserStore } from '@/stores/user';
import { useSiteConfigStore } from '@/stores/siteConfig';
import { decryptTransportValue, isEncryptedTransportValue } from '@/utils/transportCrypto';
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

const typingField = ref('')
let typingEffectTimer = null
const usernameShellRef = ref(null)
const passwordShellRef = ref(null)
const usernameBurstParticles = ref([])
const passwordBurstParticles = ref([])
const burstCleanupTimers = []
let burstSeed = 0

function randomBetween(min, max) {
    return min + Math.random() * (max - min)
}

function getBurstShell(field) {
    return field === 'password' ? passwordShellRef.value : usernameShellRef.value
}

function getBurstBucket(field) {
    return field === 'password' ? passwordBurstParticles : usernameBurstParticles
}

function createTypingBurstParticle(field, index, originX, originY) {
    const spreadAngle = randomBetween(-1.2, 1.2) + (index % 2 === 0 ? 0.35 : -0.35)
    const distance = randomBetween(18, 72) + index * 1.8
    const dx = Math.cos(spreadAngle) * distance * (index % 3 === 0 ? 1.08 : 0.9)
    const dy = Math.sin(spreadAngle) * distance - randomBetween(8, 24)
    const size = randomBetween(3, 7) + (index % 4 === 0 ? 1.2 : 0)
    const duration = randomBetween(480, 760)
    const delay = randomBetween(0, 95)
    const opacity = randomBetween(0.58, 0.96)
    const huePalette = ['#7aa8ff', '#88f0ff', '#ff8fd8', '#b6c7ff', '#f8fcff']
    const palette = huePalette[index % huePalette.length]

    return {
        id: `burst-${field}-${Date.now()}-${burstSeed++}-${index}`,
        className: index % 3 === 0 ? 'is-line' : index % 5 === 0 ? 'is-glow' : 'is-square',
        style: `
            left: ${originX}px;
            top: ${originY}px;
            --dx: ${dx.toFixed(2)}px;
            --dy: ${dy.toFixed(2)}px;
            --size: ${size.toFixed(2)}px;
            --duration: ${duration.toFixed(0)}ms;
            --delay: ${delay.toFixed(0)}ms;
            --opacity: ${opacity.toFixed(2)};
            --particle-color: ${palette};
        `
    }
}

function spawnTypingBurst(field) {
    const shell = getBurstShell(field)

    if (!shell) {
        return
    }

    const rect = shell.getBoundingClientRect()
    const originX = Math.max(22, rect.width - randomBetween(34, 54))
    const originY = rect.height / 2 + randomBetween(-10, 10)
    const count = field === 'password' ? 14 : 12
    const bucket = getBurstBucket(field)
    const particles = Array.from({ length: count }, (_, index) => createTypingBurstParticle(field, index, originX, originY))

    bucket.value = [...bucket.value, ...particles]

    const cleanupTimer = setTimeout(() => {
        const ids = new Set(particles.map((item) => item.id))
        bucket.value = bucket.value.filter((item) => !ids.has(item.id))
        const timerIndex = burstCleanupTimers.indexOf(cleanupTimer)
        if (timerIndex !== -1) {
            burstCleanupTimers.splice(timerIndex, 1)
        }
    }, 900)

    burstCleanupTimers.push(cleanupTimer)
}




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
    if (e.key == 'Enter') {
        onSubmit()
    }
}

// 添加键盘监听
onMounted(() => {
    document.addEventListener('keyup', onKeyUp)
})

// 移除键盘监听
onBeforeUnmount(() => {
    document.removeEventListener('keyup', onKeyUp)
    if (typingEffectTimer) {
        clearTimeout(typingEffectTimer)
        typingEffectTimer = null
    }
    burstCleanupTimers.forEach((timer) => clearTimeout(timer))
    burstCleanupTimers.length = 0
})

//表单引用
const formRef = ref(null);
//登录加载按钮
const loading = ref(false);

const router = useRouter();
const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore);

function triggerTypingEffect(field) {
    typingField.value = field
    spawnTypingBurst(field)
    if (typingEffectTimer) {
        clearTimeout(typingEffectTimer)
    }
    typingEffectTimer = setTimeout(() => {
        typingField.value = ''
        typingEffectTimer = null
    }, 240)
}

// 提交表单函数
const onSubmit = () => {

    //验证form表单字段
    formRef.value.validate((valid) => {
        if (!valid) {
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
                try {
                    if (isEncryptedTransportValue(token)) {
                        token = await decryptTransportValue(token)
                    }
                } catch (error) {
                    showMessage('登录凭证解密失败，请检查前后端加密配置。', 'error')
                    return
                }
                setToken(token);

                // 先进入后台，用户信息在后台静默补齐，避免 dev 环境登录后卡在此处
                router.replace('/admin/index')

                userStore.ensureUserInfoReady(true).then((resolvedUserInfo) => {

                    if (!hasAdminRole(resolvedUserInfo || userInfo.value)) {
                        router.replace('/')
                    }
                }).catch((error) => {
                    showMessage('已登录，但加载后台用户信息较慢，请稍候重试', 'warning')
                })

            } else {
                // 登录失败后的逻辑
                showMessage(response.message || '登录失败，请重试。', 'error');
            }
        }).catch(error => {
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
    position: relative;
    min-height: 48px;
    border-radius: 18px;
    box-shadow: 0 0 0 1px rgba(149, 171, 210, 0.16);
    background: rgba(250, 252, 255, 0.92);
    transition: box-shadow 0.3s ease, transform 0.3s ease, background 0.3s ease, border-color 0.3s ease;
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

.login-form :deep(.el-input__inner) {
    color: #334155;
    font-weight: 500;
}

.login-form :deep(.el-input__inner::placeholder) {
    color: rgba(100, 116, 139, 0.65);
}

.login-form :deep(.el-input__prefix-inner),
.login-form :deep(.el-input__suffix-inner) {
    color: rgba(100, 116, 139, 0.7);
}

.login-cyber-input {
    position: relative;
    width: 100%;
    border-radius: 18px;
    overflow: hidden;
}

.login-cyber-input::before {
    content: "";
    position: absolute;
    inset: auto 22px 6px;
    height: 2px;
    border-radius: 999px;
    background: linear-gradient(90deg, rgba(122, 168, 255, 0), rgba(122, 168, 255, 0.82), rgba(255, 144, 220, 0.72), rgba(122, 168, 255, 0));
    opacity: 0;
    pointer-events: none;
    transform: scaleX(0.35);
    transform-origin: center;
    transition: opacity 0.22s ease, transform 0.22s ease;
}

.login-cyber-input::after {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: inherit;
    padding: 1px;
    background: linear-gradient(120deg, rgba(122, 168, 255, 0), rgba(122, 168, 255, 0.78), rgba(255, 149, 225, 0.72), rgba(122, 168, 255, 0));
    opacity: 0;
    pointer-events: none;
    -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
    mask-composite: exclude;
}

.login-cyber-input.is-typing::before {
    opacity: 1;
    transform: scaleX(1);
    animation: typingUnderlinePulse 0.24s ease-out;
}

.login-cyber-input__pixel {
    position: absolute;
    right: 24px;
    top: 50%;
    width: 6px;
    height: 6px;
    border-radius: 1px;
    opacity: 0;
    pointer-events: none;
    transform: translate3d(-4px, -50%, 0) scale(0.7);
    z-index: 2;
}

.login-cyber-input__pixel--a {
    right: 54px;
    background: #7aa8ff;
}

.login-cyber-input__pixel--b {
    right: 42px;
    width: 5px;
    height: 5px;
    background: #88f0ff;
}

.login-cyber-input__pixel--c {
    right: 30px;
    width: 5px;
    height: 5px;
    background: #ff8fd8;
}

.login-cyber-input__pixel--d {
    right: 20px;
    width: 4px;
    height: 4px;
    background: #a9b8ff;
}

.login-cyber-input.is-typing::after {
    opacity: 1;
    animation: typingBorderSweep 0.24s ease-out;
}

.login-cyber-input__burst-layer {
    position: absolute;
    inset: 0;
    pointer-events: none;
    z-index: 2;
    overflow: hidden;
    border-radius: inherit;
    mix-blend-mode: screen;
}

.login-cyber-input__burst-particle {
    position: absolute;
    width: var(--size);
    height: var(--size);
    opacity: 0;
    transform: translate3d(0, 0, 0) scale(0.6);
    animation: typingBurstFly var(--duration) cubic-bezier(0.12, 0.78, 0.22, 1) both;
    animation-delay: var(--delay);
    will-change: transform, opacity, filter;
}

.login-cyber-input__burst-particle.is-square {
    border-radius: 2px;
    background: linear-gradient(135deg, var(--particle-color), rgba(255, 255, 255, 0.92));
    box-shadow:
        0 0 8px rgba(122, 168, 255, 0.35),
        0 0 14px rgba(122, 168, 255, 0.2);
}

.login-cyber-input__burst-particle.is-line {
    width: calc(var(--size) * 1.9);
    height: calc(var(--size) * 0.65);
    border-radius: 999px;
    background: linear-gradient(90deg, rgba(255, 255, 255, 0), var(--particle-color), rgba(255, 255, 255, 0));
    box-shadow: 0 0 10px rgba(122, 168, 255, 0.25);
}

.login-cyber-input__burst-particle.is-glow {
    border-radius: 999px;
    background: radial-gradient(circle, var(--particle-color) 0%, rgba(255, 255, 255, 0.16) 62%, rgba(255, 255, 255, 0) 72%);
    filter: blur(0.3px);
    box-shadow:
        0 0 12px rgba(122, 168, 255, 0.28),
        0 0 18px rgba(255, 143, 216, 0.16);
}

.login-cyber-input.is-typing .login-cyber-input__pixel--a {
    animation: typingPixelBurst 0.42s ease-out;
}

.login-cyber-input.is-typing .login-cyber-input__pixel--b {
    animation: typingPixelBurst 0.42s ease-out 0.04s;
}

.login-cyber-input.is-typing .login-cyber-input__pixel--c {
    animation: typingPixelBurst 0.42s ease-out 0.08s;
}

.login-cyber-input.is-typing .login-cyber-input__pixel--d {
    animation: typingPixelBurst 0.42s ease-out 0.12s;
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

@keyframes typingPixelBurst {
    0% {
        opacity: 0;
        transform: translate3d(-6px, -50%, 0) scale(0.55);
    }

    25% {
        opacity: 1;
    }

    100% {
        opacity: 0;
        transform: translate3d(14px, calc(-50% - 10px), 0) scale(1);
    }
}

@keyframes typingUnderlinePulse {
    0% {
        opacity: 0;
        transform: scaleX(0.2);
    }

    100% {
        opacity: 1;
        transform: scaleX(1);
    }
}

@keyframes typingBorderSweep {
    0% {
        opacity: 0;
        filter: blur(4px);
    }

    35% {
        opacity: 1;
    }

    100% {
        opacity: 0;
        filter: blur(0);
    }
}

@keyframes typingBurstFly {
    0% {
        opacity: 0;
        transform: translate3d(0, 0, 0) scale(0.5) rotate(0deg);
        filter: blur(0);
    }

    12% {
        opacity: var(--opacity);
    }

    45% {
        opacity: calc(var(--opacity) * 0.96);
    }

    100% {
        opacity: 0;
        transform: translate3d(var(--dx), var(--dy), 0) scale(1.12) rotate(28deg);
        filter: blur(0.25px);
    }
}

@media (max-width: 1024px) {
    .login-float-card {
        display: none;
    }
}

@media (max-width: 768px) {
    .grid.min-h-screen {
        min-height: 100svh;
    }

    .mobile-login-hero {
        margin-bottom: 1rem;
        border: 1px solid rgba(149, 171, 210, 0.16);
        border-radius: 26px;
        background:
            radial-gradient(circle at top left, rgba(173, 202, 243, 0.24), transparent 36%),
            linear-gradient(145deg, rgba(255, 255, 255, 0.92), rgba(236, 244, 252, 0.9));
        padding: 1rem 1rem 1.1rem;
        box-shadow: 0 16px 36px rgba(120, 146, 186, 0.08);
        backdrop-filter: blur(18px);
    }

    .mobile-login-hero__badge {
        display: inline-flex;
        align-items: center;
        gap: 0.55rem;
        border-radius: 9999px;
        border: 1px solid rgba(149, 171, 210, 0.16);
        background: rgba(255, 255, 255, 0.82);
        padding: 0.45rem 0.8rem;
        font-size: 11px;
        font-weight: 700;
        letter-spacing: 0.16em;
        text-transform: uppercase;
        color: var(--cosmic-muted);
    }

    .mobile-login-hero__title {
        margin-top: 0.85rem;
        font-size: 1.7rem;
        line-height: 1.02;
        color: var(--cosmic-text-light);
    }

    .mobile-login-hero__subtitle {
        margin-top: 0.4rem;
        font-size: 0.9rem;
        color: var(--cosmic-text-light-muted);
    }

    .mobile-login-hero__lead {
        margin-top: 0.8rem;
        font-size: 0.9rem;
        line-height: 1.7;
        color: #5f718a;
    }

    .mobile-login-hero__chips {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
        margin-top: 0.95rem;
    }

    .mobile-login-hero__chip {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        min-height: 2rem;
        padding: 0.25rem 0.7rem;
        border-radius: 9999px;
        border: 1px solid rgba(149, 171, 210, 0.16);
        background: rgba(255, 255, 255, 0.8);
        font-size: 12px;
        color: var(--cosmic-text-light-muted);
    }

    .login-card {
        position: relative;
        overflow: hidden;
    }

    .login-card::before {
        content: '';
        position: absolute;
        inset: 0;
        pointer-events: none;
        background:
            radial-gradient(circle at top left, rgba(173, 202, 243, 0.14), transparent 28%),
            radial-gradient(circle at bottom right, rgba(255, 255, 255, 0.28), transparent 24%);
        opacity: 0.6;
    }

    .login-card > * {
        position: relative;
        z-index: 1;
    }

    .login-form :deep(.el-form-item) {
        margin-bottom: 12px;
    }

    .login-form :deep(.el-input__wrapper) {
        min-height: 50px;
        border-radius: 16px;
    }

    .login-form :deep(.el-input__inner) {
        font-size: 15px;
    }

    .login-form :deep(.el-button) {
        min-height: 48px;
        border-radius: 16px;
    }

    .login-footnote {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.35rem;
    }

    .login-card {
        padding: 1rem 0.95rem 1rem;
        border-radius: 24px;
        box-shadow: 0 20px 42px rgba(120, 146, 186, 0.08);
    }

    .login-orb {
        filter: blur(8px);
        opacity: 0.28;
    }

    .mobile-login-hero__chip:nth-child(3) {
        flex-basis: 100%;
        justify-content: flex-start;
    }

    .login-scene-card {
        padding: 0.9rem;
        border-radius: 18px;
    }

    .login-highlight-chip {
        padding: 0.48rem 0.72rem;
    }
}

@media (prefers-reduced-motion: reduce) {
    .login-orb,
    .login-float-card,
    .login-cyber-input::before,
    .login-cyber-input::after,
    .login-cyber-input__pixel,
    .login-cyber-input__burst-particle {
        animation: none !important;
        transition: none !important;
    }

    .login-cyber-input__burst-particle {
        display: none;
    }
}
</style>
