import { useCookies } from '@vueuse/integrations/useCookies'

const cookie = useCookies()


// ============================== Token 令牌 ==============================

// 存储在 Cookie 中的 Token 的 key
const TOKEN_KEY = 'Authorization'

// 获取 Token 值
export function getToken() {
    return cookie.get(TOKEN_KEY)
}

// 设置 Token 到 Cookie 中
export function setToken(token) {
    return cookie.set(TOKEN_KEY, token)
}

// 删除 Token
export function removeToken() {
    
    try {
        // 使用 @vueuse 的 cookie 操作删除
        cookie.remove(TOKEN_KEY)
           
        // 验证 Token 是否被删除
        const tokenAfterRemove = getToken()
        
        if (tokenAfterRemove) {
            console.warn('==> Token 未被成功删除，尝试使用原生方法')
            // 使用原生 document.cookie 作为兜底方案
            document.cookie = `${TOKEN_KEY}=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 UTC;`
            document.cookie = `${TOKEN_KEY}=; Path=/; Domain=${window.location.hostname}; Expires=Thu, 01 Jan 1970 00:00:00 UTC;`
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
