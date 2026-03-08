import { http } from "@/utils/http";

export const getScheduleList = (params?: object) =>
  http.request("get", "/api/admin/schedules", { params });
export const createSchedule = (data?: object) =>
  http.request("post", "/api/admin/schedules", { data });
export const updateSchedule = (id: number, data?: object) =>
  http.request("put", `/api/admin/schedules/${id}`, { data });
export const deleteSchedule = (id: number) =>
  http.request("delete", `/api/admin/schedules/${id}`);
