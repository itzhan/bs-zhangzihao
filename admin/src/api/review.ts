import { http } from "@/utils/http";

export const getReviewList = (params?: object) =>
  http.request("get", "/api/admin/reviews", { params });
export const approveReview = (id: number) =>
  http.request("put", `/api/admin/reviews/${id}/approve`);
export const rejectReview = (id: number) =>
  http.request("put", `/api/admin/reviews/${id}/reject`);
export const replyReview = (id: number, data?: object) =>
  http.request("put", `/api/admin/reviews/${id}/reply`, { data });
