export default {
  path: "/system-manage",
  redirect: "/system-manage/audit-logs",
  meta: { icon: "ep:setting", title: "系统管理", rank: 6 },
  children: [
    {
      path: "/system-manage/audit-logs",
      name: "AuditLogList",
      component: () => import("@/views/stats/audit-logs.vue"),
      meta: { title: "审计日志" }
    },
    {
      path: "/system-manage/settings",
      name: "SystemSettings",
      component: () => import("@/views/stats/settings.vue"),
      meta: { title: "系统设置" }
    }
  ]
} satisfies RouteConfigsTable;
