/**
 * 纯 JS 获取访问者所在城市（IP 定位）
 * 无依赖，可直接在浏览器使用
 */

function fetchWithTimeout(url, timeout = 3000) {
  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeout)

  return fetch(url, { signal: controller.signal })
    .finally(() => clearTimeout(timer))
}

/**
 * IP 定位服务列表（按顺序兜底）
 */
const providers = [
  async () => {
    const res = await fetchWithTimeout('https://ipapi.co/json/')
    const data = await res.json()
    return {
      ip: data.ip,
      city: data.city,
      province: data.region,
      country: data.country_name
    }
  },
  async () => {
    const res = await fetchWithTimeout('https://ipinfo.io/json')
    const data = await res.json()
    return {
      ip: data.ip,
      city: data.city,
      province: data.region,
      country: data.country
    }
  }
]

/**
 * 主函数
 */
export async function getCityLocation() {
  for (const provider of providers) {
    try {
      const result = await provider()
      if (result.city) return result
    } catch (e) {
      // 自动切换下一个
    }
  }

  return {
    ip: '',
    city: '未知',
    province: '',
    country: ''
  }
}
