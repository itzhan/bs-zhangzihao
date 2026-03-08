export default {
  path: "/business",
  redirect: "/business/reservations",
  meta: { icon: "ep:calendar", title: "业务管理", rank: 3 },
  children: [
    {
      path: "/business/reservations",
      name: "ReservationList",
      component: () => import("@/views/business/reservations.vue"),
      meta: { title: "预约管理" }
    },
    {
      path: "/business/orders",
      name: "OrderList",
      component: () => import("@/views/business/orders.vue"),
      meta: { title: "订单管理" }
    },
    {
      path: "/business/reviews",
      name: "ReviewList",
      component: () => import("@/views/business/reviews.vue"),
      meta: { title: "评价管理" }
    }
  ]
} satisfies RouteConfigsTable;
