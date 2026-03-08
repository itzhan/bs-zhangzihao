type Result = {
  code: number;
  message: string;
  data: Array<any>;
};

export const getAsyncRoutes = (): Promise<Result> => {
  // 使用静态路由，不需要后端返回动态路由
  return Promise.resolve({ code: 0, message: "success", data: [] });
};
