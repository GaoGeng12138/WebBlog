import axios from "@/axios";

export function getVisitorLogList(params) {
    return axios.post("/admin/visitor/list", params)
}
