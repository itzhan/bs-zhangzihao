<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NSpin, NEmpty, NPagination, NIcon } from 'naive-ui'
import { HeartOutline } from '@vicons/ionicons5'
import FarmhouseCard from '@/components/FarmhouseCard.vue'
import { getMyList } from '@/api/favorite'
import type { Farmhouse, ApiResult, PageResult } from '@/types'

const favorites = ref<Farmhouse[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

async function loadFavorites() {
  loading.value = true
  try {
    const res = await getMyList(page.value, 12) as unknown as ApiResult<PageResult<Farmhouse>>
    if (res.code === 200 && res.data) {
      favorites.value = res.data.records || []
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
  loadFavorites()
}

onMounted(() => {
  loadFavorites()
})
</script>

<template>
  <div class="favorites-page">
    <div class="container">
      <h1 class="page-title">
        <NIcon :size="28" color="#5B8C5A"><HeartOutline /></NIcon>
        我的收藏
      </h1>

      <NSpin :show="loading">
        <div v-if="favorites.length" class="favorites-grid">
          <FarmhouseCard
            v-for="item in favorites"
            :key="item.id"
            :farmhouse="item"
          />
        </div>
        <NEmpty v-else-if="!loading" description="暂无收藏" style="padding: 80px 0" />
      </NSpin>

      <div v-if="total > 12" class="pagination-wrap">
        <NPagination
          :page="page"
          :page-size="12"
          :item-count="total"
          @update:page="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.favorites-page {
  padding: 32px 0 60px;
  min-height: calc(100vh - var(--nav-height));
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 24px;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

@media (max-width: 1024px) {
  .favorites-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}

.pagination-wrap {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
