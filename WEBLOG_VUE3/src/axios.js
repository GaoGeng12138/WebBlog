import axios from "axios";
import { getToken, removeToken } from "@/composables/cookie";
import { showMessage } from "@/composables/util";
import { decryptTransportData, encryptPayloadFields } from "@/utils/transportCrypto";

function getApiBaseURL() {
    if (import.meta.env.VITE_API_BASE_URL) {
        return import.meta.env.VITE_API_BASE_URL;
    }

    return import.meta.env.PROD ? "/weblog/api" : "/api";
}

// 创建 Axios 实例
const instance = axios.create({
    baseURL: getApiBaseURL(),
    timeout: 7000, // 请求超时时间
})


//请求拦截器中先是获取了 Cookie 中的 Token 令牌，在不为空的情况下，将其添加到请求头中，
// 按后端的规范，key 为 Authorization, 值为 Bearer + 中间空一格 + 令牌 的格式
instance.interceptors.request.use(async config => {
    // 在发送请求之前做些什么
    const token = getToken();
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token; // 将 token 添加到请求头中
    }

    if (config.data && config.sensitiveFields?.length) {
        config.data = await encryptPayloadFields(config.data, config.sensitiveFields);
    }
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});


// 添加响应拦截器
instance.interceptors.response.use(async response => {
    // 2xx 范围内的状态码都会触发该函数。
    const data = await decryptTransportData(response.data);
    // 正常返回数据
    return data;
}, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    let message = '请求出错，请稍后重试。'
    if (error && error.response) {
        const resp = error.response
        // 优先尝试从后端返回的数据中提取消息
        if (resp.data && resp.data.message) {
            message = resp.data.message
        }

        // 检查特定的认证错误模式 (errorCode: 20002, message包含"无访问权限，请先登录！")
        if ((resp.data && resp.data.errorCode === '20002' && message && message.includes('无访问权限')) ||
            resp.status === 401 ||
            (message && /失效|未授权|Unauthorized/i.test(message))) {
            try {
                removeToken()
            } catch (e) {
                // ignore
            }
            showMessage(message || '登录已失效，请重新登录', 'error')
            // 带上当前路径，登录后可跳回原页面
            const redirect = encodeURIComponent(window.location.pathname + window.location.search)
            window.location.href = `/login?redirect=${redirect}`
            return Promise.reject(error)
        }
    }

    showMessage(message, 'error')
    return Promise.reject(error)
});

// 暴露出去
export default instance;
