<template>
  <div class="register-page min-h-screen bg-[#f6f8fc] flex flex-col">
    <div class="register-shell flex-grow flex items-center justify-center py-8 px-4 sm:px-6 lg:px-8">
      <div class="w-full max-w-4xl">
        <div class="register-hero mb-6 overflow-hidden rounded-[28px] border border-[rgba(149,171,210,0.16)] bg-[rgba(255,255,255,0.78)] p-5 shadow-[0_18px_34px_rgba(120,146,186,0.08)] backdrop-blur-xl sm:p-6">
          <p class="text-[11px] font-medium uppercase tracking-[0.24em] text-[var(--cosmic-muted)]">Create account</p>
          <div class="mt-2 flex flex-col gap-2 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <h2 class="text-2xl font-black tracking-tight text-slate-900 sm:text-4xl">
                创建账户
              </h2>
              <p class="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
                把资料、头像、社交账号一次填完，注册完成后就能直接开始写作和收藏。
              </p>
            </div>
            <p class="text-sm text-slate-600">
              已有账户？
              <router-link to="/login" class="font-semibold text-blue-600 hover:text-blue-500">
                立即登录
              </router-link>
            </p>
          </div>
        </div>

        <!-- 主卡片 -->
        <div class="register-card bg-white/92 backdrop-blur border border-slate-200 shadow-xl rounded-[28px] overflow-hidden">
          <div class="p-5 sm:p-10">
            <!-- 顶部错误汇总（可选但推荐） -->
            <div
              v-if="formErrorSummary.length"
              class="mb-6 rounded-xl border border-red-200 bg-red-50 p-4"
              role="alert"
            >
              <div class="font-semibold text-red-700 text-sm mb-2">请先修正以下问题：</div>
              <ul class="list-disc pl-5 text-sm text-red-700 space-y-1">
                <li v-for="(msg, idx) in formErrorSummary" :key="idx">{{ msg }}</li>
              </ul>
            </div>

            <form class="space-y-6" @submit.prevent="onSubmit" novalidate>
              <!-- 用户名 / 昵称：两列布局 -->
              <div class="register-two-col grid grid-cols-1 sm:grid-cols-2 gap-5">
                <div>
                  <label for="username" class="block text-sm font-medium text-slate-700">
                    用户名 <span class="text-red-500">*</span>
                  </label>
                  <div class="mt-2">
                    <input
                      id="username"
                      v-model.trim="form.username"
                      name="username"
                      type="text"
                      autocomplete="username"
                      inputmode="text"
                      @blur="validateField('username')"
                      :aria-invalid="!!errors.username"
                      :class="inputClass(errors.username)"
                      class="w-full"
                      placeholder="3-20位，字母/数字/下划线"
                    />
                    <p v-if="errors.username" class="mt-1 text-sm text-red-600">{{ errors.username }}</p>
                  </div>
                </div>

                <div>
                  <label for="nickname" class="block text-sm font-medium text-slate-700">
                    昵称 <span class="text-red-500">*</span>
                  </label>
                  <div class="mt-2">
                    <input
                      id="nickname"
                      v-model.trim="form.nickname"
                      name="nickname"
                      type="text"
                      autocomplete="nickname"
                      @blur="validateField('nickname')"
                      :aria-invalid="!!errors.nickname"
                      :class="inputClass(errors.nickname)"
                      class="w-full"
                      placeholder="2-20位"
                    />
                    <p v-if="errors.nickname" class="mt-1 text-sm text-red-600">{{ errors.nickname }}</p>
                  </div>
                </div>
              </div>

              <!-- 邮箱 -->
              <div>
                <label for="email" class="block text-sm font-medium text-slate-700">
                  邮箱 <span class="text-red-500">*</span>
                </label>
                <div class="mt-2">
                  <input
                    id="email"
                    v-model.trim="form.email"
                    name="email"
                    type="email"
                    autocomplete="email"
                    inputmode="email"
                    @blur="validateField('email')"
                    :aria-invalid="!!errors.email"
                    :class="inputClass(errors.email)"
                    class="w-full"
                    placeholder="name@example.com"
                  />
                  <p v-if="errors.email" class="mt-1 text-sm text-red-600">{{ errors.email }}</p>
                </div>
              </div>

              <!-- 头像 -->
              <div>
                <label class="block text-sm font-medium text-slate-700 mb-2">
                  头像
                </label>

                <div class="register-avatar-row flex items-center gap-5">
                  <div class="relative shrink-0">
                    <div
                      v-if="avatarPreview"
                      class="w-24 h-24 rounded-full overflow-hidden border border-slate-200 ring-4 ring-slate-100"
                    >
                      <img :src="avatarPreview" alt="头像预览" class="w-full h-full object-cover" />
                    </div>
                    <div
                      v-else
                      class="w-24 h-24 rounded-full bg-slate-50 border border-dashed border-slate-300 flex items-center justify-center ring-4 ring-slate-100"
                    >
                      <svg class="w-8 h-8 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                      </svg>
                    </div>
                  </div>

                  <div class="flex-1">
                    <div class="flex flex-wrap items-center gap-3">
                      <label
                        class="cursor-pointer inline-flex items-center px-4 py-2 rounded-lg border border-slate-300 bg-white shadow-sm text-sm font-medium text-slate-700 hover:bg-slate-50 focus-within:ring-2 focus-within:ring-blue-500 focus-within:ring-offset-2 disabled:opacity-60"
                        :class="{ 'pointer-events-none opacity-60': avatarUploading }"
                      >
                        <svg class="-ml-1 mr-2 h-5 w-5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <span>{{ avatarUploading ? '上传中...' : '选择图片' }}</span>
                        <input
                          ref="avatarInputRef"
                          type="file"
                          class="sr-only"
                          accept="image/jpeg,image/png,image/jpg"
                          @change="handleAvatarChange"
                          :disabled="avatarUploading"
                        />
                      </label>

                      <button
                        v-if="avatarPreview || form.avatar"
                        type="button"
                        class="inline-flex items-center px-3 py-2 rounded-lg border border-slate-200 bg-white text-sm font-medium text-slate-600 hover:bg-slate-50"
                        @click="removeAvatar"
                        :disabled="avatarUploading || loading"
                      >
                        移除
                      </button>
                    </div>

                    <p class="mt-2 text-xs text-slate-500">支持 JPG/PNG，≤ 2MB。建议 1:1 图片效果更佳。</p>
                    <p v-if="errors.avatar" class="mt-1 text-sm text-red-600">{{ errors.avatar }}</p>
                  </div>
                </div>
              </div>

              <!-- 自我介绍 -->
              <div>
                <label for="intro" class="block text-sm font-medium text-slate-700">
                  自我介绍 <span class="text-slate-400 text-xs">（选填）</span>
                </label>
                <div class="mt-2">
                  <textarea
                    id="intro"
                    v-model="form.intro"
                    name="intro"
                    rows="4"
                    maxlength="200"
                    @blur="validateField('intro')"
                    :aria-invalid="!!errors.intro"
                    :class="textareaClass(errors.intro)"
                    class="w-full"
                    placeholder="介绍一下自己吧~（最多200字）"
                  ></textarea>

                  <div class="flex justify-between items-center mt-2">
                    <p v-if="errors.intro" class="text-sm text-red-600">{{ errors.intro }}</p>
                    <p class="text-xs text-slate-500 ml-auto">{{ introLength }}/200</p>
                  </div>
                </div>
              </div>

              <!-- 社交媒体账号（根据后台配置显示） -->
              <div v-if="hasSocialMediaFields" class="space-y-5">
                <div class="border-t border-slate-200 pt-5">
                  <h3 class="text-sm font-medium text-slate-700 mb-4">社交媒体账号 <span class="text-slate-400 text-xs">(选填)</span></h3>
                  
                  <!-- GitHub -->
                  <div v-if="siteConfig.siteInfo.githubEnabled && siteConfig.siteInfo.githubShowRegister" class="mb-4">
                    <label for="github" class="block text-sm font-medium text-slate-700 mb-2">
                      <svg class="inline w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 24 24">
                        <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                      </svg>
                      GitHub
                    </label>
                    <input
                      id="github"
                      v-model.trim="form.github"
                      type="url"
                      class="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                      placeholder="https://github.com/username"
                    />
                  </div>

                  <!-- Twitter -->
                  <div v-if="siteConfig.siteInfo.twitterEnabled && siteConfig.siteInfo.twitterShowRegister" class="mb-4">
                    <label for="twitter" class="block text-sm font-medium text-slate-700 mb-2">
                      <svg class="inline w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 24 24">
                        <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"/>
                      </svg>
                      Twitter
                    </label>
                    <input
                      id="twitter"
                      v-model.trim="form.twitter"
                      type="url"
                      class="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                      placeholder="https://twitter.com/username"
                    />
                  </div>

                  <!-- Weibo -->
                  <div v-if="siteConfig.siteInfo.weiboEnabled && siteConfig.siteInfo.weiboShowRegister">
                    <label for="weibo" class="block text-sm font-medium text-slate-700 mb-2">
                      <svg class="inline w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 24 24">
                        <path d="M9.63 17.95c-3.58.34-6.67-1.27-6.9-3.6-.24-2.32 2.48-4.48 6.06-4.82 3.58-.34 6.68 1.27 6.91 3.6.24 2.32-2.48 4.48-6.07 4.82zm10.23-4.39c-.3-.1-.51-.17-.35-.62.34-.98.38-1.82.01-2.42-.68-1.11-2.54-1.05-4.67-.03 0 0-.67.33-.5-.27.33-1.23.28-2.26-.3-2.86-1.31-1.37-4.8.05-7.79 3.17-2.24 2.33-3.54 4.79-3.54 7.03 0 4.14 5.31 6.66 10.5 6.66 6.81 0 11.33-3.95 11.33-7.09 0-1.9-1.6-2.97-3.69-2.57zm2.76-6.15c-1.56-1.74-3.88-2.4-5.99-2.01-.45.08-.73.52-.65.97.08.45.52.74.97.65 1.59-.29 3.35.2 4.53 1.51 1.18 1.31 1.57 3.11 1.18 4.72-.12.44.14.9.58 1.02.44.12.9-.14 1.02-.58.52-2.14.02-4.54-1.64-6.28z"/>
                      </svg>
                      微博
                    </label>
                    <input
                      id="weibo"
                      v-model.trim="form.weibo"
                      type="url"
                      class="w-full px-4 py-2.5 border border-slate-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition"
                      placeholder="https://weibo.com/username"
                    />
                  </div>
                </div>
              </div>

              <!-- 密码 / 确认密码：两列布局 -->
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
                <div>
                  <label for="password" class="block text-sm font-medium text-slate-700">
                    密码 <span class="text-red-500">*</span>
                  </label>

                  <div class="mt-2 relative">
                    <input
                      id="password"
                      v-model="form.password"
                      name="password"
                      :type="showPassword ? 'text' : 'password'"
                      autocomplete="new-password"
                      @blur="validateField('password')"
                      :aria-invalid="!!errors.password"
                      :class="inputClass(errors.password)"
                      class="w-full pr-12"
                      placeholder="6-20位，建议字母+数字"
                    />
                    <button
                      type="button"
                      class="absolute inset-y-0 right-2 inline-flex items-center px-2 text-slate-500 hover:text-slate-700"
                      @click="showPassword = !showPassword"
                      :aria-label="showPassword ? '隐藏密码' : '显示密码'"
                      tabindex="-1"
                    >
                      <svg v-if="showPassword" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M13.875 18.825A10.05 10.05 0 0112 19c-5.523 0-10-4.477-10-10 0-1.12.185-2.197.525-3.2M6.227 6.227A9.956 9.956 0 0112 5c5.523 0 10 4.477 10 10 0 1.7-.423 3.3-1.173 4.702M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      </svg>
                      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      </svg>
                    </button>
                  </div>

                  <p v-if="errors.password" class="mt-1 text-sm text-red-600">{{ errors.password }}</p>

                  <!-- 密码强度 -->
                  <div class="mt-3">
                    <div class="flex items-center justify-between text-xs text-slate-600 mb-1">
                      <span>密码强度：{{ passwordStrengthLabel }}</span>
                      <span class="text-slate-400">建议包含字母与数字</span>
                    </div>
                    <div class="h-2 rounded-full bg-slate-100 overflow-hidden">
                      <div
                        class="h-2 rounded-full transition-all"
                        :class="passwordStrengthBarClass"
                        :style="{ width: passwordStrengthPercent + '%' }"
                      ></div>
                    </div>
                  </div>
                </div>

                <div>
                  <label for="confirmPassword" class="block text-sm font-medium text-slate-700">
                    确认密码 <span class="text-red-500">*</span>
                  </label>

                  <div class="mt-2 relative">
                    <input
                      id="confirmPassword"
                      v-model="form.confirmPassword"
                      name="confirmPassword"
                      :type="showConfirmPassword ? 'text' : 'password'"
                      autocomplete="new-password"
                      @blur="validateField('confirmPassword')"
                      :aria-invalid="!!errors.confirmPassword"
                      :class="inputClass(errors.confirmPassword)"
                      class="w-full pr-12"
                      placeholder="请再次输入密码"
                    />
                    <button
                      type="button"
                      class="absolute inset-y-0 right-2 inline-flex items-center px-2 text-slate-500 hover:text-slate-700"
                      @click="showConfirmPassword = !showConfirmPassword"
                      :aria-label="showConfirmPassword ? '隐藏确认密码' : '显示确认密码'"
                      tabindex="-1"
                    >
                      <svg v-if="showConfirmPassword" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M13.875 18.825A10.05 10.05 0 0112 19c-5.523 0-10-4.477-10-10 0-1.12.185-2.197.525-3.2M6.227 6.227A9.956 9.956 0 0112 5c5.523 0 10 4.477 10 10 0 1.7-.423 3.3-1.173 4.702M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                      </svg>
                      <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0 3 3 0 010 6z" />
                      </svg>
                    </button>
                  </div>

                  <p v-if="errors.confirmPassword" class="mt-1 text-sm text-red-600">{{ errors.confirmPassword }}</p>
                </div>
              </div>

              <!-- 协议 -->
              <div>
                <div class="flex items-start gap-3">
                  <input
                    id="agreement"
                    v-model="form.agreement"
                    name="agreement"
                    type="checkbox"
                    class="mt-1 h-4 w-4 text-blue-600 focus:ring-blue-500 border-slate-300 rounded"
                    @change="validateField('agreement')"
                  />
                  <label for="agreement" class="text-sm text-slate-700">
                    我已阅读并同意
                    <a href="#" class="font-medium text-blue-600 hover:text-blue-500">用户协议</a>
                    和
                    <a href="#" class="font-medium text-blue-600 hover:text-blue-500">隐私政策</a>
                  </label>
                </div>
                <p v-if="errors.agreement" class="mt-1 text-sm text-red-600">{{ errors.agreement }}</p>
              </div>

              <!-- 提交按钮 -->
              <div class="pt-2">
                <button
                  type="submit"
                  :disabled="!canSubmit"
                  class="w-full inline-flex items-center justify-center py-2.5 px-4 rounded-xl shadow-sm text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition"
                >
                  <svg
                    v-if="loading"
                    class="animate-spin -ml-1 mr-3 h-5 w-5 text-white"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path
                      class="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    ></path>
                  </svg>
                  {{ loading ? '注册中...' : (avatarUploading ? '头像上传中...' : '注册') }}
                </button>

                <p class="mt-3 text-xs text-slate-500 text-center">
                  提交即表示你同意遵守相关条款。为保障账号安全，请勿使用过于简单的密码。
                </p>
              </div>
            </form>
          </div>
        </div>

        <footer class="py-8 text-center text-sm text-slate-500">
          <p>© 2023 WebLog. All rights reserved.</p>
        </footer>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { registerFrontend } from '@/api/frontend/user'
import { uploadFile } from '@/api/frontend/file'
import { showMessage } from '@/composables/util'
import { useSiteConfigStore } from '@/stores/siteConfig'

const router = useRouter()
const siteConfig = useSiteConfigStore()

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  intro: '',
  github: '',
  twitter: '',
  weibo: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

const errors = reactive({
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  intro: '',
  password: '',
  confirmPassword: '',
  agreement: ''
})

// 检查是否有社交媒体字段需要显示
const hasSocialMediaFields = computed(() => {
  return (siteConfig.siteInfo.githubEnabled && siteConfig.siteInfo.githubShowRegister) || 
         (siteConfig.siteInfo.twitterEnabled && siteConfig.siteInfo.twitterShowRegister) || 
         (siteConfig.siteInfo.weiboEnabled && siteConfig.siteInfo.weiboShowRegister)
})

const loading = ref(false)
const avatarUploading = ref(false)
const avatarPreview = ref('')
const avatarInputRef = ref(null)

const showPassword = ref(false)
const showConfirmPassword = ref(false)

/** 样式帮助 */
const inputClass = (hasError) => {
  return [
    'appearance-none block w-full px-3 py-2.5 rounded-lg shadow-sm border bg-white placeholder-slate-400',
    'focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500',
    hasError ? 'border-red-300 ring-0' : 'border-slate-300'
  ].join(' ')
}
const textareaClass = (hasError) => {
  return [
    'appearance-none block w-full px-3 py-2.5 rounded-lg shadow-sm border bg-white placeholder-slate-400',
    'focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500',
    'min-h-[96px] resize-y',
    hasError ? 'border-red-300 ring-0' : 'border-slate-300'
  ].join(' ')
}

/** 友好长度显示 */
const introLength = computed(() => (form.intro?.length || 0))

/** 密码强度（简单可用版） */
const passwordStrengthScore = computed(() => {
  const p = form.password || ''
  let score = 0
  if (p.length >= 6) score++
  if (p.length >= 10) score++
  if (/[A-Za-z]/.test(p) && /\d/.test(p)) score++
  if (/[^A-Za-z0-9]/.test(p)) score++
  return Math.min(score, 4) // 0-4
})

const passwordStrengthPercent = computed(() => Math.round((passwordStrengthScore.value / 4) * 100))

const passwordStrengthLabel = computed(() => {
  const s = passwordStrengthScore.value
  if (s <= 1) return '弱'
  if (s === 2) return '中'
  return '强'
})

const passwordStrengthBarClass = computed(() => {
  const s = passwordStrengthScore.value
  if (s <= 1) return 'bg-red-400'
  if (s === 2) return 'bg-amber-400'
  return 'bg-emerald-500'
})

/** 校验逻辑 */
function clearErrors() {
  Object.keys(errors).forEach(k => (errors[k] = ''))
}

function validateField(field) {
  // 单字段校验，便于 blur / change
  switch (field) {
    case 'username': {
      const v = (form.username || '').trim()
      if (!v) return (errors.username = '请输入用户名')
      if (v.length < 3 || v.length > 20) return (errors.username = '用户名长度应在 3-20 个字符之间')
      if (!/^[A-Za-z0-9_]+$/.test(v)) return (errors.username = '用户名仅支持字母/数字/下划线')
      errors.username = ''
      return
    }
    case 'nickname': {
      const v = (form.nickname || '').trim()
      if (!v) return (errors.nickname = '请输入昵称')
      if (v.length < 2 || v.length > 20) return (errors.nickname = '昵称长度应在 2-20 个字符之间')
      errors.nickname = ''
      return
    }
    case 'email': {
      const v = (form.email || '').trim().toLowerCase()
      form.email = v
      if (!v) return (errors.email = '请输入邮箱')
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)) return (errors.email = '请输入有效的邮箱地址')
      errors.email = ''
      return
    }
    case 'intro': {
      const v = form.intro || ''
      if (v.length > 200) return (errors.intro = '自我介绍不能超过 200 字')
      errors.intro = ''
      return
    }
    case 'password': {
      const v = form.password || ''
      if (!v) return (errors.password = '请输入密码')
      if (v.length < 6 || v.length > 20) return (errors.password = '密码长度应在 6-20 个字符之间')
      // 建议性规则：不强制，但可提升安全性（如需强制可改为 return error）
      // if (!(/[A-Za-z]/.test(v) && /\d/.test(v))) return (errors.password = '建议密码包含字母和数字')
      errors.password = ''
      return
    }
    case 'confirmPassword': {
      const v = form.confirmPassword || ''
      if (!v) return (errors.confirmPassword = '请再次输入密码')
      if (form.password !== v) return (errors.confirmPassword = '两次输入的密码不一致')
      errors.confirmPassword = ''
      return
    }
    case 'agreement': {
      if (!form.agreement) return (errors.agreement = '请同意用户协议和隐私政策')
      errors.agreement = ''
      return
    }
    default:
      return
  }
}

function validateForm() {
  clearErrors()
  validateField('username')
  validateField('nickname')
  validateField('email')
  validateField('intro')
  validateField('password')
  validateField('confirmPassword')
  validateField('agreement')
  // avatar 可选：只校验上传错误（由上传流程产生）
  return Object.values(errors).every(v => !v)
}

/** 顶部错误汇总 */
const formErrorSummary = computed(() => {
  return Object.values(errors).filter(Boolean)
})

/** 提交可用性（防止无效点击/重复提交） */
const canSubmit = computed(() => {
  const requiredFilled =
    form.username.trim() &&
    form.nickname.trim() &&
    form.email.trim() &&
    form.password &&
    form.confirmPassword

  const noErrors = Object.values(errors).every(v => !v)

  return (
    !!requiredFilled &&
    form.agreement &&
    noErrors &&
    !loading.value &&
    !avatarUploading.value
  )
})

/** 输入变化时：如果用户在修正，及时清除对应错误（提升体验） */
watch(() => form.username, () => { if (errors.username) validateField('username') })
watch(() => form.nickname, () => { if (errors.nickname) validateField('nickname') })
watch(() => form.email, () => { if (errors.email) validateField('email') })
watch(() => form.password, () => {
  if (errors.password) validateField('password')
  // 密码变化时，确认密码也可能变为不一致
  if (form.confirmPassword) validateField('confirmPassword')
})
watch(() => form.confirmPassword, () => { if (errors.confirmPassword) validateField('confirmPassword') })
watch(() => form.agreement, () => { if (errors.agreement) validateField('agreement') })

/** 头像上传 */
async function handleAvatarChange(event) {
  const file = event.target.files?.[0]
  if (!file) return

  // 允许重复选择同一文件：立刻 reset input
  if (avatarInputRef.value) avatarInputRef.value.value = ''

  // 类型校验
  const okType = ['image/jpeg', 'image/png', 'image/jpg'].includes(file.type)
  if (!okType) {
    errors.avatar = '头像图片只能是 JPG/PNG 格式'
    return
  }

  // 大小校验
  const okSize = file.size / 1024 / 1024 < 2
  if (!okSize) {
    errors.avatar = '头像图片大小不能超过 2MB'
    return
  }

  errors.avatar = ''

  // 本地预览
  const reader = new FileReader()
  reader.onload = (e) => { avatarPreview.value = e.target.result }
  reader.readAsDataURL(file)

  avatarUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)

    const response = await uploadFile(formData)
    if (response?.success) {
      form.avatar = response.data?.url || ''
      showMessage('头像上传成功', 'success')
    } else {
      errors.avatar = response?.message || '头像上传失败'
      showMessage(errors.avatar, 'error')
      avatarPreview.value = ''
      form.avatar = ''
    }
  } catch (err) {
    console.error('头像上传失败:', err)
    errors.avatar = '头像上传失败，请重试'
    showMessage(errors.avatar, 'error')
    avatarPreview.value = ''
    form.avatar = ''
  } finally {
    avatarUploading.value = false
  }
}

function removeAvatar() {
  avatarPreview.value = ''
  form.avatar = ''
  errors.avatar = ''
}

/** 提交注册 */
async function onSubmit() {
  if (!validateForm()) return

  loading.value = true
  try {
    const requestData = {
      username: form.username.trim(),
      nickname: form.nickname.trim(),
      email: form.email.trim().toLowerCase(),
      avatar: form.avatar,
      intro: (form.intro || '').trim(),
      password: form.password
    }

    // 只在后台配置允许时才添加社交媒体字段
    if (siteConfig.siteInfo.githubEnabled && siteConfig.siteInfo.githubShowRegister && form.github) {
      requestData.github = form.github.trim()
    }
    if (siteConfig.siteInfo.twitterEnabled && siteConfig.siteInfo.twitterShowRegister && form.twitter) {
      requestData.twitter = form.twitter.trim()
    }
    if (siteConfig.siteInfo.weiboEnabled && siteConfig.siteInfo.weiboShowRegister && form.weibo) {
      requestData.weibo = form.weibo.trim()
    }

    const response = await registerFrontend(requestData)
    if (response?.success) {
      showMessage('注册成功！请登录您的账户。', 'success')
      router.push('/login')
    } else {
      showMessage(response?.message || '注册失败，请稍后重试。', 'error')
    }
  } catch (err) {
    console.error('注册失败:', err)
    showMessage('注册失败，请稍后重试。', 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  position: relative;
}

.register-shell {
  background:
    radial-gradient(circle at top left, rgba(148, 176, 231, 0.12), transparent 28%),
    radial-gradient(circle at bottom right, rgba(255, 155, 227, 0.08), transparent 20%);
}

.register-hero {
  position: relative;
}

.register-hero::after {
  content: "";
  position: absolute;
  inset: auto -3rem -3rem auto;
  width: 10rem;
  height: 10rem;
  border-radius: 9999px;
  background: radial-gradient(circle, rgba(148, 176, 231, 0.16), transparent 72%);
  pointer-events: none;
}

@media (max-width: 640px) {
  .register-shell {
    padding-inline: 0.75rem;
  }

  .register-card {
    border-radius: 22px;
  }

  .register-two-col {
    gap: 1rem;
  }

  .register-avatar-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .register-card :deep(input),
  .register-card :deep(textarea),
  .register-card :deep(.el-input__wrapper) {
    border-radius: 14px;
  }

  .register-card :deep(button) {
    width: 100%;
  }
}
</style>
