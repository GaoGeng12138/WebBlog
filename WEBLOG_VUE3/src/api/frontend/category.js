import axios from "@/axios";

// 获取分类分页数据
export function getCategoryList(data) {
    return axios.post("/category/list", data);
}

export function getAllCategoryList(data) {
    return axios.post("/category/list/all", data);
}
