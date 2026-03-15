import { http } from "@/utils/http";

export type UserResult = {
  code: number;
  message: string;
  data: {
    token: string;
    user: {
      id: number;
      username: string;
      nickname: string;
      role: string;
      avatar: string;
    };
  };
};

export type UserInfo = {
  /** 头像 */
  avatar: string;
  /** 用户名 */
  username: string;
  /** 昵称 */
  nickname: string;
  /** 邮箱 */
  email: string;
  /** 联系电话 */
  phone: string;
  /** 简介 */
  description: string;
};

export type UserInfoResult = {
  code: number;
  message: string;
  data: UserInfo;
};

/** 登录 */
export const getLogin = (data?: object) => {
  return http.request<UserResult>("post", "/api/auth/login", { data });
};

/** 刷新token（复用登录接口） */
export const refreshTokenApi = (data?: object) => {
  return http.request<UserResult>("post", "/api/auth/login", { data });
};

/** 获取当前登录用户信息 */
export const getMine = () => {
  return http.request<UserInfoResult>("get", "/api/auth/info");
};

/** 获取当前用户安全日志 */
export const getMineLogs = (params?: object) => {
  return http.request("get", "/api/admin/audit-logs", { params });
};
