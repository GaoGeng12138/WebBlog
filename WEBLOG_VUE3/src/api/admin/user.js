import axios from "@/axios";

// 登录接口
export function login(username, password) {
    return axios.post("/login", { username, password }, {
        sensitiveFields: ['username', 'password']
    })
}

// 获取登录用户信息
export function getUserInfo() {
    return axios.post("/admin/user/info")
}

// 修改用户密码
export function updateAdminPassword(data) {
    return axios.post("/admin/password/update", data, {
        sensitiveFields: ['username', 'oldPassword', 'password', 'confirmPassword']
    })
}

//验证码接口
export function getCaptchaCode() {
    return axios.post("/admin/captcha/code")
}

// 获取用户列表
export function getUserList(params) {
    return axios.post("/admin/user/list", { params })
}

// 新增用户
export function addUser(data) {
    return axios.post("/admin/user/create", data, {
        sensitiveFields: ['username', 'password', 'email', 'nickname']
    })
}

// 更新用户
export function updateUser(data) {
    return axios.post(`/admin/user/info/update`, data, {
        sensitiveFields: ['username', 'password', 'email', 'nickname']
    })
}

// 删除用户
export function deleteUser(id) {
    return axios.post('/admin/user/delete', { id })
}

// 启用/停用用户
export function updateUserStatus(data) {
    return axios.post(`/admin/user/status/update`, data)
}
