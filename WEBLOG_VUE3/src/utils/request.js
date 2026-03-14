import axios from "axios";

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 15000,
});

// 如果你有 token，就在这里统一加
request.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// 统一处理响应
request.interceptors.response.use(
  (res) => res.data,
  (err) => {
    // 检查是否是权限错误
    if (err.response && err.response.data) {
      const errorMsg = err.response.data.message || err.response.data.errorMsg;
      const errorCode = err.response.data.errorCode;
      
      // 检查特定的认证错误模式 (errorCode: 20002, message包含"无访问权限")
      if ((errorCode === '20002' && errorMsg && errorMsg.includes('无访问权限')) ||
          (errorMsg && (errorMsg.includes('无访问权限') || errorMsg.includes('请先登录')))) {
        // 清除本地token
        localStorage.removeItem("token");
        // 重定向到登录页面
        window.location.href = '/login';
        return Promise.reject(err);
      }
    }
    return Promise.reject(err);
  }
);

export default request;