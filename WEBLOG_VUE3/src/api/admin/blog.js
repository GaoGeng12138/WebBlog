import axios from "@/axios";

// 获取博客设置
export function getBlogSettings() {
    return axios.post("/admin/blog/settings/get")
}

// 更新博客设置
export function updateBlogSettings(data) {
    return axios.post("/admin/blog/settings/update", data)
}