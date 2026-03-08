import { http } from "@/utils/http";

export const getPackageList = (params?: object) =>
  http.request("get", "/api/admin/packages", { params });
export const createPackage = (data?: object) =>
  http.request("post", "/api/admin/packages", { data });
export const updatePackage = (id: number, data?: object) =>
  http.request("put", `/api/admin/packages/${id}`, { data });
export const deletePackage = (id: number) =>
  http.request("delete", `/api/admin/packages/${id}`);
