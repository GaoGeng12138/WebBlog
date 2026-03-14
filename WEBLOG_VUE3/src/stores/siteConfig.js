import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/axios'

export const useSiteConfigStore = defineStore('siteConfig', () => {
    // 网站基本信息
    const siteInfo = ref({
        title: '',
        slogan: '',
        description: '',
        logoUrl: '',
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
        commentEnabled: true,           // 允许评论
        likeEnabled: true,              // 允许点赞
        favoriteEnabled: true,          // 允许收藏
        userRegisterEnabled: false,     // 允许用户注册
        userPublishEnabled: false,      // 允许用户发布文章
        articleReviewRequired: false,   // 文章需要审核
        commentReviewRequired: false,   // 评论需要审核
        anonymousCommentEnabled: false  // 允许匿名评论
    })

    // 加载状态
    const loading = ref(false)

    // 获取网站基本信息
    const fetchSiteInfo = async () => {
        try {
            const response = await axios.get('/site/info')
            if (response.success && response.data) {
                siteInfo.value = {
                    title: response.data.title || '',
                    slogan: response.data.slogan || '',
                    description: response.data.description || '',
                    logoUrl: response.data.logoUrl || '',
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

    // 获取网站权限配置
    const fetchPermissions = async () => {
        try {
            console.log('开始获取网站权限配置...')
            const response = await axios.post('/site/permissions')
            console.log('权限配置接口返回:', response)
            
            if (response.success && response.data) {
                console.log('权限配置数据:', response.data)
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
                console.log('权限配置已更新:', permissions.value)
            } else {
                console.warn('权限配置接口返回异常:', response)
            }
        } catch (error) {
            console.error('获取权限配置失败:', error)
        }
    }

    // 初始化配置
    const initConfig = async () => {
        loading.value = true
        await Promise.all([
            fetchSiteInfo(),
            fetchPermissions()
        ])
        loading.value = false
    }

    // 检查功能是否启用
    const isFeatureEnabled = (featureName) => {
        const enabled = permissions.value[featureName] === true
        console.log(`检查功能 [${featureName}]:`, enabled, '当前权限:', permissions.value)
        return enabled
    }

    return {
        siteInfo,
        permissions,
        loading,
        fetchSiteInfo,
        fetchPermissions,
        initConfig,
        isFeatureEnabled
    }
}, {
    persist: true // 开启持久化
})
