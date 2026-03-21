import axios from '@/axios'

/**
 * 获取角色列表
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export function getRoleList(params) {
    return axios.post('/admin/role/list', params)
}

/**
 * 添加角色
 * @param {Object} data - 角色数据
 * @returns {Promise}
 */
export function addRole(data) {
    return axios.post('/admin/role/add', data)
}

/**
 * 更新角色
 * @param {number} id - 角色ID
 * @param {Object} data - 角色数据
 * @returns {Promise}
 */
export function updateRole(id, data) {
    return axios.post('/admin/role/update', { id, ...data })
}

/**
 * 删除角色
 * @param {number} id - 角色ID
 * @returns {Promise}
 */
export function deleteRole(id) {
    return axios.post('/admin/role/delete', { id })
}

/**
 * 获取角色权限列表
 * @param {number} roleId - 角色ID
 * @returns {Promise}
 */
export function getRolePermissions(roleId) {
    return axios.post('/admin/role/permissions', { roleId })
}

/**
 * 更新角色权限
 * @param {Object} data - { roleId, permissionIds }
 * @returns {Promise}
 */
export function updateRolePermissions(data) {
    return axios.post('/admin/role/permissions/update', data)
}

/**
 * 获取所有权限列表（用于分配权限）
 * @returns {Promise}
 */
export function getAllPermissions() {
    return axios.post('/admin/permissions/all')
}

/**
 * 分配角色
 * @param {*} data 
 * @returns 
 */
export function assignRole(data) {
    return axios.post('/admin/role/assign', data)
}
