import { http } from "@/utils/http";

export const getUserList = (params?: object) =>
  http.request("get", "/api/admin/users", { params });
export const getUserDetail = (id: number) =>
  http.request("get", `/api/admin/users/${id}`);
export const toggleUserStatus = (id: number) =>
  http.request("put", `/api/admin/users/${id}/status`);
