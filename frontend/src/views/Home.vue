<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NInput, NIcon, NButton, NSpin, NEmpty, NTag } from 'naive-ui'
import { SearchOutline } from '@vicons/ionicons5'
import FarmhouseCard from '@/components/FarmhouseCard.vue'
import { getList } from '@/api/farmhouse'
import type { Farmhouse, ApiResult } from '@/types'

const keyword = ref('')
const farmhouses = ref<Farmhouse[]>([])
const loading = ref(false)

const tagFilters = ['采摘', '垂钓', '烧烤', '民宿', '农家菜', '亲子']
const activeTag = ref('')

async function loadFarmhouses() {
  loading.value = true
  try {
    const searchKeyword = activeTag.value || keyword.value || undefined
    const res = await getList(searchKeyword) as unknown as ApiResult<Farmhouse[] | { records: Farmhouse[] }>
    if (res.code === 200) {
      if (Array.isArray(res.data)) {
        farmhouses.value = res.data
      } else if (res.data && 'records' in res.data) {
        farmhouses.value = res.data.records
      }
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  activeTag.value = ''
  loadFarmhouses()
}

function handleTagClick(tag: string) {
  if (activeTag.value === tag) {
    activeTag.value = ''
  } else {
    activeTag.value = tag
  }
  keyword.value = ''
  loadFarmhouses()
}

onMounted(() => {
  loadFarmhouses()
})
</script>

<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-bg">
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content container">
        <h1 class="hero-title">发现河南最美农家乐</h1>
        <p class="hero-subtitle">享受田园慢生活，预约美好时光</p>
        <div class="hero-search">
          <NInput
            v-model:value="keyword"
            placeholder="搜索农家乐名称、地址、特色..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <NIcon :size="20" color="#9CAF9C">
                <SearchOutline />
              </NIcon>
            </template>
          </NInput>
          <NButton type="primary" size="large" @click="handleSearch">
            搜索
          </NButton>
        </div>
      </div>
    </section>

    <!-- Main Content -->
    <div class="container main-content">
      <!-- Tag Filters -->
      <div class="filter-bar">
        <span class="filter-label">热门标签：</span>
        <div class="filter-tags">
          <NTag
            v-for="tag in tagFilters"
            :key="tag"
            :type="activeTag === tag ? 'success' : 'default'"
            :bordered="activeTag !== tag"
            round
            checkable
            :checked="activeTag === tag"
            class="filter-tag"
            @click="handleTagClick(tag)"
          >
            {{ tag }}
          </NTag>
        </div>
      </div>

      <!-- Farmhouse Grid -->
      <NSpin :show="loading">
        <div v-if="farmhouses.length" class="farmhouse-grid">
          <FarmhouseCard
            v-for="item in farmhouses"
            :key="item.id"
            :farmhouse="item"
          />
        </div>
        <NEmpty
          v-else-if="!loading"
          description="暂无农家乐数据"
          style="padding: 80px 0"
        />
      </NSpin>
    </div>
  </div>
</template>

<style scoped>
.home-page {
  min-height: calc(100vh - var(--nav-height));
}

/* Hero */
.hero {
  position: relative;
  height: 380px;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(135deg, rgba(91, 140, 90, 0.08) 0%, rgba(212, 132, 90, 0.06) 50%, rgba(247, 244, 239, 0.9) 100%),
    url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 400"><defs><linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%"><stop offset="0%" style="stop-color:%235B8C5A;stop-opacity:0.05" /><stop offset="100%" style="stop-color:%23D4845A;stop-opacity:0.03" /></linearGradient></defs><rect fill="url(%23g)" width="1200" height="400"/><circle cx="100" cy="300" r="200" fill="%235B8C5A" opacity="0.03"/><circle cx="1000" cy="100" r="150" fill="%23D4845A" opacity="0.03"/></svg>');
  background-size: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, transparent 0%, var(--color-bg-body) 80%);
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  width: 100%;
}

.hero-title {
  font-size: 40px;
  font-weight: 800;
  color: var(--color-text-primary);
  margin-bottom: 12px;
  letter-spacing: 1px;
}

.hero-subtitle {
  font-size: 18px;
  color: var(--color-text-secondary);
  margin-bottom: 32px;
}

.hero-search {
  display: flex;
  gap: 12px;
  max-width: 560px;
  margin: 0 auto;
}

.hero-search :deep(.n-input) {
  --n-border: 1px solid var(--color-border) !important;
  background: #fff;
  box-shadow: var(--shadow-md);
}

/* Main Content */
.main-content {
  padding-top: 32px;
  padding-bottom: 60px;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.filter-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  flex-shrink: 0;
}

.filter-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-tag {
  cursor: pointer;
}

/* Grid */
.farmhouse-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

@media (max-width: 1024px) {
  .farmhouse-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .hero-title {
    font-size: 28px;
  }

  .hero-subtitle {
    font-size: 15px;
  }

  .hero-search {
    flex-direction: column;
  }

  .farmhouse-grid {
    grid-template-columns: 1fr;
  }
}
</style>
