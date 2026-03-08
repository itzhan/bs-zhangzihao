import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/farmhouses'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/farmhouses',
    name: 'Farmhouses',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/farmhouse/:id',
    name: 'FarmhouseDetail',
    component: () => import('@/views/FarmhouseDetail.vue'),
    meta: { title: '农家乐详情' }
  },
  {
    path: '/farmhouse/:id/package/:packageId',
    name: 'PackageDetail',
    component: () => import('@/views/PackageDetail.vue'),
    meta: { title: '套餐预约' }
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: () => import('@/views/Announcements.vue'),
    meta: { title: '公告' }
  },
  {
    path: '/my/reservations',
    name: 'MyReservations',
    component: () => import('@/views/my/Reservations.vue'),
    meta: { title: '我的预约', requiresAuth: true }
  },
  {
    path: '/my/orders',
    name: 'MyOrders',
    component: () => import('@/views/my/Orders.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/my/favorites',
    name: 'MyFavorites',
    component: () => import('@/views/my/Favorites.vue'),
    meta: { title: '我的收藏', requiresAuth: true }
  },
  {
    path: '/my/notifications',
    name: 'MyNotifications',
    component: () => import('@/views/my/Notifications.vue'),
    meta: { title: '我的消息', requiresAuth: true }
  },
  {
    path: '/my/profile',
    name: 'MyProfile',
    component: () => import('@/views/my/Profile.vue'),
    meta: { title: '个人中心', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// Route guard
router.beforeEach((to, _from, next) => {
  const title = to.meta.title as string
  if (title) {
    document.title = `${title} - 农家乐预约平台`
  }

  if (to.meta.requiresAuth && !getToken()) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
