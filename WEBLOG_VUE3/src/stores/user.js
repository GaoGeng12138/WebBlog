import { getUserInfo } from '@/api/admin/user'
import { getUserInfoFrontend } from '@/api/frontend/user'
import { removeToken } from '@/composables/cookie'
import { decryptTransportData, isEncryptedTransportValue } from '@/utils/transportCrypto'
import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useUserStore = defineStore('user', () => {
    // 用户信息
    const userInfo = ref({})
    const frontendUserInfo = ref({})

    function hasEncryptedValue(target) {
        if (target === null || target === undefined) {
            return false
        }

        if (typeof target === 'string') {
            return isEncryptedTransportValue(target)
        }

        if (Array.isArray(target)) {
            return target.some(hasEncryptedValue)
        }

        if (typeof target === 'object') {
            return Object.values(target).some(hasEncryptedValue)
        }

        return false
    }

    async function normalizeUserRef(targetRef) {
        const currentValue = targetRef.value
        if (!currentValue || !hasEncryptedValue(currentValue)) {
            return currentValue
        }

        const decryptedValue = await decryptTransportData(currentValue)
        targetRef.value = decryptedValue
        return decryptedValue
    }

    // 设置后台用户信息
    function setUserInfo() {
        // 调用后头获取用户信息接口
        return getUserInfo().then(async res => {
            if (res.success == true) {
                userInfo.value = res.data
                return normalizeUserRef(userInfo)
            }
            throw new Error('Failed to get user info')
        })
    }

    // 设置前台用户信息
    function setFrontendUserInfo() {
        return getUserInfoFrontend().then(async res => {
            if (res && res.success && res.data) {
                frontendUserInfo.value = res.data
                return normalizeUserRef(frontendUserInfo)
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

    watch(userInfo, () => {
        normalizeUserRef(userInfo)
    }, { deep: true, immediate: true })

    watch(frontendUserInfo, () => {
        normalizeUserRef(frontendUserInfo)
    }, { deep: true, immediate: true })

    return { userInfo, frontendUserInfo, setUserInfo, setFrontendUserInfo, logout }
},
    {
        // 开启数据持久化
        persist: true
    }
)
