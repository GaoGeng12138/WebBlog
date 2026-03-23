import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/axios'

export const useSiteConfigStore = defineStore('siteConfig', () => {
    // 网站基本信息
    const siteInfo = ref({
        title: '',
        description: '',
        logoUrl: '',
        frontendArticlePageSize: 12,
        activityLevelRules: '',
        activityScoreRules: '',
        githubEnabled: false,
        githubShowFront: false,
        githubShowRegister: false,
        twitterEnabled: false,
        twitterShowFront: false,
        twitterShowRegister: false,
        weiboEnabled: false,
        weiboShowFront: false,
        weiboShowRegister: false
    })

    // 网站权限配置
    const permissions = ref({
        commentEnabled: true,
        likeEnabled: true,
        favoriteEnabled: true,
        userRegisterEnabled: false,
        userPublishEnabled: false,
        articleReviewRequired: false,
        commentReviewRequired: false,
        anonymousCommentEnabled: false
    })

    const loading = ref(false)
    const initialized = ref(false)
    let initPromise = null

    const fetchSiteInfo = async () => {
        try {
            const response = await axios.get('/site/info')
            if (response.success && response.data) {
                siteInfo.value = {
                    title: response.data.title || '',
                    description: response.data.description || '',
                    logoUrl: response.data.logoUrl || '',
                    frontendArticlePageSize: response.data.frontendArticlePageSize || 12,
                    activityLevelRules: response.data.activityLevelRules || '',
                    activityScoreRules: response.data.activityScoreRules || '',
                    githubEnabled: response.data.githubEnabled || false,
                    githubShowFront: response.data.githubShowFront || false,
                    githubShowRegister: response.data.githubShowRegister || false,
                    twitterEnabled: response.data.twitterEnabled || false,
                    twitterShowFront: response.data.twitterShowFront || false,
                    twitterShowRegister: response.data.twitterShowRegister || false,
                    weiboEnabled: response.data.weiboEnabled || false,
                    weiboShowFront: response.data.weiboShowFront || false,
                    weiboShowRegister: response.data.weiboShowRegister || false
                }
            }
        } catch (error) {
            console.error('获取网站信息失败:', error)
        }
    }

    const fetchPermissions = async () => {
        try {
            const response = await axios.post('/site/permissions')
            if (response.success && response.data) {
                permissions.value = {
                    commentEnabled: response.data.commentEnabled !== undefined ? response.data.commentEnabled : true,
                    likeEnabled: response.data.likeEnabled !== undefined ? response.data.likeEnabled : true,
                    favoriteEnabled: response.data.favoriteEnabled !== undefined ? response.data.favoriteEnabled : true,
                    userRegisterEnabled: response.data.userRegisterEnabled !== undefined ? response.data.userRegisterEnabled : false,
                    userPublishEnabled: response.data.userPublishEnabled !== undefined ? response.data.userPublishEnabled : false,
                    articleReviewRequired: response.data.articleReviewRequired !== undefined ? response.data.articleReviewRequired : false,
                    commentReviewRequired: response.data.commentReviewRequired !== undefined ? response.data.commentReviewRequired : false,
                    anonymousCommentEnabled: response.data.anonymousCommentEnabled !== undefined ? response.data.anonymousCommentEnabled : false
                }
            }
        } catch (error) {
            console.error('获取权限配置失败:', error)
        }
    }

    const initConfig = async () => {
        loading.value = true
        try {
            await Promise.all([
                fetchSiteInfo(),
                fetchPermissions()
            ])
            initialized.value = true
        } finally {
            loading.value = false
        }
    }

    const ensureConfigReady = async () => {
        if (initialized.value) {
            return
        }

        if (!initPromise) {
            initPromise = initConfig().finally(() => {
                initPromise = null
            })
        }

        await initPromise
    }

    const isFeatureEnabled = (featureName) => {
        return permissions.value[featureName] === true
    }

    return {
        siteInfo,
        permissions,
        loading,
        initialized,
        fetchSiteInfo,
        fetchPermissions,
        initConfig,
        ensureConfigReady,
        isFeatureEnabled
    }
}, {
    persist: true
})
