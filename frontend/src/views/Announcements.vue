<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NCard, NTag, NEmpty, NSpin, NPagination, NIcon } from 'naive-ui'
import { MegaphoneOutline, CalendarOutline } from '@vicons/ionicons5'
import { getList } from '@/api/announcement'
import type { Announcement, ApiResult, PageResult } from '@/types'

const announcements = ref<Announcement[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const expandedId = ref<number | null>(null)

const typeMap: Record<number, { label: string; type: 'info' | 'warning' | 'success' }> = {
  0: { label: '公告', type: 'info' },
  1: { label: '活动', type: 'warning' },
  2: { label: '通知', type: 'success' }
}

function getTypeInfo(type: number) {
  return typeMap[type] || typeMap[0]
}

function toggleExpand(id: number) {
  expandedId.value = expandedId.value === id ? null : id
}

async function loadAnnouncements() {
  loading.value = true
  try {
    const res = await getList(page.value, 10) as unknown as ApiResult<PageResult<Announcement>>
    if (res.code === 200 && res.data) {
      announcements.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

function handlePageChange(p: number) {
  page.value = p
  loadAnnouncements()
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<template>
  <div class="announcements-page">
    <div class="container">
      <div class="page-header">
        <h1>
          <NIcon :size="28" color="#5B8C5A"><MegaphoneOutline /></NIcon>
          公告信息
        </h1>
        <p>了解最新活动与平台动态</p>
      </div>

      <NSpin :show="loading">
        <div v-if="announcements.length" class="announcements-list">
          <NCard
            v-for="item in announcements"
            :key="item.id"
            class="announcement-card"
            hoverable
            @click="toggleExpand(item.id)"
          >
            <div class="announcement-header">
              <NTag :type="getTypeInfo(item.type).type" size="small" :bordered="false" round>
                {{ getTypeInfo(item.type).label }}
              </NTag>
              <h3>{{ item.title }}</h3>
              <div class="announcement-time">
                <NIcon :size="14"><CalendarOutline /></NIcon>
                <span>{{ item.startTime || '暂无时间' }}</span>
              </div>
            </div>
            <p class="announcement-preview" :class="{ expanded: expandedId === item.id }">
              {{ item.content }}
            </p>
            <span class="expand-btn">{{ expandedId === item.id ? '收起' : '展开全文' }}</span>
          </NCard>
        </div>
        <NEmpty v-else-if="!loading" description="暂无公告" style="padding: 80px 0" />
      </NSpin>

      <div v-if="total > 10" class="pagination-wrap">
        <NPagination
          :page="page"
          :page-size="10"
          :item-count="total"
          @update:page="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.announcements-page {
  padding: 32px 0 60px;
  min-height: calc(100vh - var(--nav-height));
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.page-header p {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-left: 38px;
}

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.announcement-card {
  cursor: pointer;
}

.announcement-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.announcement-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  flex: 1;
}

.announcement-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-text-light);
}

.announcement-preview {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.announcement-preview.expanded {
  display: block;
  -webkit-line-clamp: unset;
}

.expand-btn {
  display: inline-block;
  margin-top: 8px;
  font-size: 13px;
  color: var(--color-primary);
  cursor: pointer;
}

.expand-btn:hover {
  color: var(--color-primary-hover);
}

.pagination-wrap {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
