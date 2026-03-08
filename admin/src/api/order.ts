import { http } from "@/utils/http";

export const getOrderList = (params?: object) =>
  http.request("get", "/api/admin/orders", { params });
export const processRefund = (id: number, data?: object) =>
  http.request("put", `/api/admin/orders/${id}/refund`, { data });
export const completeOrder = (id: number) =>
  http.request("put", `/api/admin/orders/${id}/complete`);
