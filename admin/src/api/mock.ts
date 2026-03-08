import { http } from "@/utils/http";

/** 表单上传（模拟） */
export const formUpload = (data: FormData) => {
  return http.request("post", "/upload", {
    data,
    headers: { "Content-Type": "multipart/form-data" }
  });
};

/** 地图数据（模拟） */
export const mapJson = () => {
  return http.request("get", "/map-info");
};
