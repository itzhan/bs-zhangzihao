export default {
  path: "/stats",
  redirect: "/stats/dashboard",
  meta: { icon: "ep:data-analysis", title: "数据统计", rank: 5 },
  children: [
    {
      path: "/stats/dashboard",
      name: "Dashboard",
      component: () => import("@/views/stats/dashboard.vue"),
      meta: { title: "数据看板" }
    },
    {
      path: "/stats/audit-logs",
      name: "AuditLogList",
      component: () => import("@/views/stats/audit-logs.vue"),
      meta: { title: "审计日志" }
    },
    {
      path: "/stats/settings",
      name: "SystemSettings",
      component: () => import("@/views/stats/settings.vue"),
      meta: { title: "系统设置" }
    }
  ]
} satisfies RouteConfigsTable;
