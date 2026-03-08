import { http } from "@/utils/http";

export const getStatistics = () =>
  http.request("get", "/api/admin/dashboard/statistics");
export const getReservationTrend = (params?: object) =>
  http.request("get", "/api/admin/dashboard/reservation-trend", { params });
export const getRevenueTrend = (params?: object) =>
  http.request("get", "/api/admin/dashboard/revenue-trend", { params });
export const getTopFarmhouses = (params?: object) =>
  http.request("get", "/api/admin/dashboard/top-farmhouses", { params });
export const getRatingDistribution = () =>
  http.request("get", "/api/admin/dashboard/rating-distribution");
