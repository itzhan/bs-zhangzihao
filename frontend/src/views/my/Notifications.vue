<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  NButton, NTag, NEmpty, NSpin, NPagination, NIcon, useMessage
} from 'naive-ui'
import { NotificationsOutline, CheckmarkDoneOutline } from '@vicons/ionicons5'
import { getMyList, markRead, markAllRead } from '@/api/notification'
import type { Notification, ApiResult, PageResult } from '@/types'

const message = useMessage()

const notifications = ref<Notification[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

const typeMap: Record<number, string> = {
  0: '系统通知',
  1: '预约通知',
  2: '订单通知',
  3: '活动通知'
}

async function loadNotifications() {
  loading.value = true
  try {
    const res = await getMyList(page.value, 20) as unknown as ApiResult<PageResult<Notification>>
    if (res.code === 200 && res.data) {
      notifications.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleMarkRead(id: number) {
  try {
    const res = await markRead(id) as unknown as ApiResult
    if (res.code === 200) {
      const item = notifications.value.find(n => n.id === id)
      if (item) item.isRead = 1
    }
  } catch {
    // ignore
  }
}

async function handleMarkAllRead() {
  try {
    const res = await markAllRead() as unknown as ApiResult
    if (res.code === 200) {
      notifications.value.forEach(n => { n.isRead = 1 })
      message.success('已全部标记为已读')
    }
  } catch {
    message.error('操作失败')
  }
}

function handlePageChange(p: number) {
  page.value = p
  loadNotifications()
}

onMounted(() => {
  loadNotifications()
})
</script>

<template>
  <div class="notifications-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">
          <NIcon :size="28" color="#5B8C5A"><NotificationsOutline /></NIcon>
          消息通知
        </h1>
        <NButton text @click="handleMarkAllRead" class="mark-all-btn">
          <template #icon>
            <NIcon><CheckmarkDoneOutline /></NIcon>
          </template>
          全部已读
        </NButton>
      </div>

      <NSpin :show="loading">
        <div v-if="notifications.length" class="notification-list">
          <div
            v-for="item in notifications"
            :key="item.id"
            :class="['notification-item', { unread: item.isRead === 0 }]"
            @click="item.isRead === 0 && handleMarkRead(item.id)"
          >
            <div class="notification-dot" v-if="item.isRead === 0"></div>
            <div class="notification-content">
              <div class="notification-top">
                <NTag :bordered="false" size="tiny" type="info">
                  {{ typeMap[item.type] || '通知' }}
                </NTag>
                <h4>{{ item.title }}</h4>
                <span class="notification-time">{{ item.createTime }}</span>
              </div>
              <p>{{ item.content }}</p>
            </div>
          </div>
        </div>
        <NEmpty v-else-if="!loading" description="暂无消息" style="padding: 80px 0" />
      </NSpin>

      <div v-if="total > 20" class="pagination-wrap">
        <NPagination
          :page="page"
          :page-size="20"
          :item-count="total"
          @update:page="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.notifications-page {
  padding: 32px 0 60px;
  min-height: calc(100vh - var(--nav-height));
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
}

.mark-all-btn {
  color: var(--color-text-secondary) !important;
  font-size: 14px;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 16px 20px;
  background: var(--color-bg-card);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.2s;
  border: 1px solid var(--color-border-light);
}

.notification-item.unread {
  background: rgba(91, 140, 90, 0.03);
  border-color: rgba(91, 140, 90, 0.1);
}

.notification-item:hover {
  background: rgba(91, 140, 90, 0.05);
}

.notification-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--color-accent);
  margin-top: 8px;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-top {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.notification-top h4 {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 12px;
  color: var(--color-text-light);
  flex-shrink: 0;
}

.notification-content p {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.pagination-wrap {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
