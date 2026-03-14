import { getUserInfo } from '@/api/admin/user'
import { getUserInfoFrontend } from '@/api/frontend/user'
import { removeToken } from '@/composables/cookie'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
    // 用户信息
    const userInfo = ref({})
    const frontendUserInfo = ref({})

    // 设置后台用户信息
    function setUserInfo() {
        // 调用后头获取用户信息接口
        return getUserInfo().then(res => {
            if (res.success == true) {
                userInfo.value = res.data
                return res.data
            }
            throw new Error('Failed to get user info')
        })
    }

    // 设置前台用户信息
    function setFrontendUserInfo() {
        return getUserInfoFrontend().then(res => {
            if (res && res.success && res.data) {
                frontendUserInfo.value = res.data
                return res.data
            }
        }).catch((error) => {
            frontendUserInfo.value = {}
            throw error
        })
    }

    // 退出登录
    function logout() {
        // 删除 cookie 中的 token 令牌
        removeToken()
        // 删除登录用户信息
        userInfo.value = {}
        frontendUserInfo.value = {}
    }

    return { userInfo, frontendUserInfo, setUserInfo, setFrontendUserInfo, logout }
},
    {
        // 开启数据持久化
        persist: true
    }
)