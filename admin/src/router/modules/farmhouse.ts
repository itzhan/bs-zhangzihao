export default {
  path: "/farmhouse",
  redirect: "/farmhouse/list",
  meta: { icon: "ep:house", title: "农家乐管理", rank: 2 },
  children: [
    {
      path: "/farmhouse/list",
      name: "FarmhouseList",
      component: () => import("@/views/farmhouse/list.vue"),
      meta: { title: "农家乐列表" }
    },
    {
      path: "/farmhouse/packages",
      name: "PackageList",
      component: () => import("@/views/farmhouse/packages.vue"),
      meta: { title: "套餐管理" }
    },
    {
      path: "/farmhouse/schedules",
      name: "ScheduleList",
      component: () => import("@/views/farmhouse/schedules.vue"),
      meta: { title: "档期管理" }
    }
  ]
} satisfies RouteConfigsTable;
