import { http } from "@/utils/http";

export const getAuditLogList = (params?: object) =>
  http.request("get", "/api/admin/audit-logs", { params });
