import axios from "@/axios";

// 获取分类分页数据
export function getCategoryPageList(data) {
    return axios.post("/admin/category/list", data)
}

// 添加分类
export function addCategory(data) {
    return axios.post("/admin/category/add", data)
}

// 删除分类
export function deleteCategory(id) {
    return axios.post("/admin/category/delete", { id })
}

// 更新前台展示状态
export function updateCategoryShowOnFront(data) {
    return axios.post("/admin/category/update/showOnFront", data)
}

// 更新分类可见范围
export function updateCategoryVisibility(data) {
    return axios.post("/admin/category/update/visibility", data)
}

// 获取分类下拉列表
export function getCategorySelectList() {
    return axios.post("/admin/category/select/list")
}
