import { http } from "@/utils/http";

export const getAnnouncementList = (params?: object) =>
  http.request("get", "/api/admin/announcements", { params });
export const getAnnouncementDetail = (id: number) =>
  http.request("get", `/api/admin/announcements/${id}`);
export const createAnnouncement = (data?: object) =>
  http.request("post", "/api/admin/announcements", { data });
export const updateAnnouncement = (id: number, data?: object) =>
  http.request("put", `/api/admin/announcements/${id}`, { data });
export const deleteAnnouncement = (id: number) =>
  http.request("delete", `/api/admin/announcements/${id}`);
