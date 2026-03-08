import { http } from "@/utils/http";

// 农家乐管理
export const getFarmhouseList = (params?: object) =>
  http.request("get", "/api/admin/farmhouses", { params });
export const createFarmhouse = (data?: object) =>
  http.request("post", "/api/admin/farmhouses", { data });
export const updateFarmhouse = (id: number, data?: object) =>
  http.request("put", `/api/admin/farmhouses/${id}`, { data });
export const deleteFarmhouse = (id: number) =>
  http.request("delete", `/api/admin/farmhouses/${id}`);
export const toggleFarmhouseStatus = (id: number) =>
  http.request("put", `/api/admin/farmhouses/${id}/status`);
