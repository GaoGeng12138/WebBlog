import axios from '@/axios'
// 上传文件
export function uploadFile(data) {
    return axios.post("/admin/file/upload", data)
}

// 解析 Word 文档为 Markdown
export function parseWordFile(data) {
    return axios.post("/admin/file/word/parse", data)
}
