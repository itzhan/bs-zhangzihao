import { http } from "@/utils/http";

export const getReservationList = (params?: object) =>
  http.request("get", "/api/admin/reservations", { params });
export const confirmReservation = (id: number) =>
  http.request("put", `/api/admin/reservations/${id}/confirm`);
export const completeReservation = (id: number) =>
  http.request("put", `/api/admin/reservations/${id}/complete`);
