import axios from '@/axios'
// 上传文件
export function uploadFile(data) {
    return axios.post("/admin/file/upload", data)
}