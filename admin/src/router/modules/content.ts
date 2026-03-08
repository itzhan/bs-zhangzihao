export default {
  path: "/content",
  redirect: "/content/announcements",
  meta: { icon: "ep:document", title: "内容管理", rank: 4 },
  children: [
    {
      path: "/content/announcements",
      name: "AnnouncementList",
      component: () => import("@/views/content/announcements.vue"),
      meta: { title: "公告管理" }
    },
    {
      path: "/content/users",
      name: "UserList",
      component: () => import("@/views/content/users.vue"),
      meta: { title: "用户管理" }
    }
  ]
} satisfies RouteConfigsTable;
