import { http } from "@/utils/http";

export const getAllSettings = () =>
  http.request("get", "/api/admin/settings");
export const batchUpdateSettings = (data?: object) =>
  http.request("put", "/api/admin/settings", { data });
