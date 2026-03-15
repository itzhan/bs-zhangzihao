<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NButton, NBadge, NDropdown, NAvatar, NIcon } from 'naive-ui'
import {
  LeafOutline,
  PersonCircleOutline,
  CalendarOutline,
  ReceiptOutline,
  HeartOutline,
  NotificationsOutline,
  SettingsOutline,
  LogOutOutline
} from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'
import { getUnreadCount } from '@/api/notification'
import type { ApiResult } from '@/types'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const unreadCount = ref(0)

const navLinks = [
  { label: '首页', path: '/', key: 'home' },
  { label: '公告', path: '/announcements', key: 'announcement' }
]

const dropdownOptions = [
  { label: '我的预约', key: '/my/reservations', icon: CalendarOutline },
  { label: '我的订单', key: '/my/orders', icon: ReceiptOutline },
  { label: '我的收藏', key: '/my/favorites', icon: HeartOutline },
  { label: '消息通知', key: '/my/notifications', icon: NotificationsOutline },
  { label: '个人中心', key: '/my/profile', icon: SettingsOutline },
  { type: 'divider', key: 'd1' },
  { label: '退出登录', key: 'logout', icon: LogOutOutline }
]

function isActive(link: typeof navLinks[number]) {
  const p = route.path
  if (link.key === 'home') {
    return p === '/'
  }
  if (link.key === 'farmhouse') {
    // 农家乐详情和套餐页面高亮
    return p.startsWith('/farmhouse/')
  }
  // 其他：前缀匹配
  return p === link.path || p.startsWith(link.path + '/')
}

function handleDropdownSelect(key: string) {
  if (key === 'logout') {
    userStore.logout()
  } else {
    router.push(key)
  }
}

async function loadUnreadCount() {
  if (!userStore.isLoggedIn) return
  try {
    const res = await getUnreadCount() as unknown as ApiResult<number>
    if (res.code === 200) {
      unreadCount.value = res.data || 0
    }
  } catch {
    // ignore
  }
}

onMounted(() => {
  loadUnreadCount()
})
</script>

<template>
  <header class="navbar">
    <div class="navbar-inner container">
      <!-- Logo -->
      <div class="navbar-logo" @click="router.push('/')">
        <NIcon :size="28" color="#5B8C5A">
          <LeafOutline />
        </NIcon>
        <span class="logo-text">农家乐预约平台</span>
      </div>

      <!-- Nav Links -->
      <nav class="navbar-nav">
        <a
          v-for="link in navLinks"
          :key="link.path + link.label"
          :class="['nav-link', { active: isActive(link) }]"
          @click.prevent="router.push(link.path)"
        >
          {{ link.label }}
        </a>
      </nav>

      <!-- Right Actions -->
      <div class="navbar-actions">
        <template v-if="userStore.isLoggedIn">
          <NBadge :value="unreadCount" :max="99" :offset="[-4, 4]">
            <NDropdown
              :options="dropdownOptions as any"
              trigger="click"
              @select="handleDropdownSelect"
            >
              <div class="user-trigger">
                <NAvatar
                  :size="32"
                  round
                  :src="userStore.user?.avatar"
                  style="cursor: pointer"
                >
                  <template #fallback>
                    <NIcon :size="20">
                      <PersonCircleOutline />
                    </NIcon>
                  </template>
                </NAvatar>
                <span class="user-name">{{ userStore.user?.nickname || userStore.user?.username }}</span>
              </div>
            </NDropdown>
          </NBadge>
        </template>
        <template v-else>
          <NButton text @click="router.push('/login')" class="auth-btn">
            登录
          </NButton>
          <NButton
            type="primary"
            size="small"
            round
            @click="router.push('/register')"
          >
            注册
          </NButton>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
  border-bottom: 1px solid var(--color-border);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.navbar-inner {
  display: flex;
  align-items: center;
  height: var(--nav-height);
  gap: 32px;
}

.navbar-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-primary);
  letter-spacing: 0.5px;
}

.navbar-nav {
  display: flex;
  gap: 28px;
  flex: 1;
}

.nav-link {
  font-size: 15px;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 4px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.2s ease;
}

.nav-link:hover {
  color: var(--color-primary);
}

.nav-link.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
  font-weight: 600;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.2s;
}

.user-trigger:hover {
  background: rgba(91, 140, 90, 0.06);
}

.user-name {
  font-size: 14px;
  color: var(--color-text-primary);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.auth-btn {
  color: var(--color-text-secondary) !important;
  font-size: 14px;
}

.auth-btn:hover {
  color: var(--color-primary) !important;
}
</style>
