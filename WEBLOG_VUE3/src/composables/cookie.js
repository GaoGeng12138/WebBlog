import { useCookies } from '@vueuse/integrations/useCookies'

const cookie = useCookies()


// ============================== Token 令牌 ==============================

// 存储在 Cookie 中的 Token 的 key
const TOKEN_KEY = 'Authorization'

function getAppBasePath() {
    const base = import.meta.env.BASE_URL || '/'
    const normalizedBase = base.endsWith('/') && base.length > 1
        ? base.slice(0, -1)
        : base

    return normalizedBase || '/'
}

function getCookiePaths() {
    const pathSet = new Set(['/'])
    const basePath = getAppBasePath()

    if (basePath !== '/') {
        pathSet.add(basePath)
    }

    return Array.from(pathSet)
}

function normalizeTokenValue(token) {
    if (typeof token !== 'string') {
        return token
    }

    let normalizedToken = token.trim()

    if (
        (normalizedToken.startsWith('"') && normalizedToken.endsWith('"')) ||
        (normalizedToken.startsWith("'") && normalizedToken.endsWith("'"))
    ) {
        normalizedToken = normalizedToken.slice(1, -1).trim()
    }

    if (/^Bearer\s+/i.test(normalizedToken)) {
        normalizedToken = normalizedToken.replace(/^Bearer\s+/i, '').trim()
    }

    return normalizedToken
}

// 获取 Token 值
export function getToken() {
    return normalizeTokenValue(cookie.get(TOKEN_KEY))
}

// 设置 Token 到 Cookie 中
export function setToken(token) {
    const normalizedToken = normalizeTokenValue(token)

    getCookiePaths().forEach((path) => {
        cookie.set(TOKEN_KEY, normalizedToken, { path })
    })

    return normalizedToken
}

// 删除 Token
export function removeToken() {
    
    try {
        getCookiePaths().forEach((path) => {
            cookie.remove(TOKEN_KEY, { path })
        })
           
        // 验证 Token 是否被删除
        const tokenAfterRemove = getToken()
        
        if (tokenAfterRemove) {
            console.warn('==> Token 未被成功删除，尝试使用原生方法')
            getCookiePaths().forEach((path) => {
                document.cookie = `${TOKEN_KEY}=; Path=${path}; Expires=Thu, 01 Jan 1970 00:00:00 UTC;`
                document.cookie = `${TOKEN_KEY}=; Path=${path}; Domain=${window.location.hostname}; Expires=Thu, 01 Jan 1970 00:00:00 UTC;`
            })
        } 
    } catch (e) {
        console.error('==> 删除 Token 失败:', e)
    }
}

// ============================== 标签页 ==============================

// 存储在 Cookie 中的标签页数据的 key
const TAB_LIST_KEY = 'tabList'

// 获取 TabList
export function getTabList() {
    return cookie.get(TAB_LIST_KEY)
}

// 存储 TabList 到 Cookie 中
export function setTabList(tabList) {
    return cookie.set(TAB_LIST_KEY, tabList)
}
