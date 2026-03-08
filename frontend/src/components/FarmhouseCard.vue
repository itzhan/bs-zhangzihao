<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { NTag, NRate, NIcon } from 'naive-ui'
import { LocationOutline } from '@vicons/ionicons5'
import type { Farmhouse } from '@/types'

const props = defineProps<{
  farmhouse: Farmhouse
}>()

const router = useRouter()

const tags = computed(() => {
  if (!props.farmhouse.tags) return []
  return props.farmhouse.tags.split(',').filter(Boolean).slice(0, 3)
})

function handleClick() {
  router.push(`/farmhouse/${props.farmhouse.id}`)
}
</script>

<template>
  <div class="farmhouse-card" @click="handleClick">
    <div class="card-cover">
      <img
        v-if="farmhouse.coverImage"
        :src="farmhouse.coverImage"
        :alt="farmhouse.name"
        class="cover-img"
      />
      <div v-else class="cover-placeholder">
        <span>{{ farmhouse.name?.charAt(0) || '农' }}</span>
      </div>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ farmhouse.name }}</h3>
      <p class="card-desc">{{ farmhouse.shortDesc || farmhouse.description }}</p>
      <div class="card-location">
        <NIcon :size="14" color="#9CAF9C">
          <LocationOutline />
        </NIcon>
        <span>{{ farmhouse.address }}</span>
      </div>
      <div class="card-footer">
        <div class="card-rating">
          <NRate :value="farmhouse.rating" readonly allow-half :size="14" />
          <span class="review-count">{{ farmhouse.reviewCount || 0 }}条评价</span>
        </div>
        <div class="card-tags" v-if="tags.length">
          <NTag
            v-for="tag in tags"
            :key="tag"
            size="small"
            :bordered="false"
            type="success"
          >
            {{ tag }}
          </NTag>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.farmhouse-card {
  background: var(--color-bg-card);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border-light);
}

.farmhouse-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-hover);
}

.card-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
  position: relative;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}

.farmhouse-card:hover .cover-img {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8e2d8 0%, #d4c5a9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-placeholder span {
  font-size: 48px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-body {
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-location {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--color-text-light);
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.review-count {
  font-size: 12px;
  color: var(--color-text-light);
}

.card-tags {
  display: flex;
  gap: 4px;
}
</style>
