<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NButton, NTag, NRate, NIcon, NSpin, NEmpty, NCard,
  NDivider, NPagination, NAvatar, useMessage
} from 'naive-ui'
import {
  LocationOutline, CallOutline, TimeOutline,
  HeartOutline, Heart, StarOutline
} from '@vicons/ionicons5'
import { getDetail, getPackages, getReviews } from '@/api/farmhouse'
import { add as addFavorite, remove as removeFavorite, check as checkFavorite } from '@/api/favorite'
import { useUserStore } from '@/stores/user'
import type { Farmhouse, FarmhousePackage, Review, ApiResult, PageResult } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const farmhouseId = Number(route.params.id)
const farmhouse = ref<Farmhouse | null>(null)
const packages = ref<FarmhousePackage[]>([])
const reviews = ref<Review[]>([])
const reviewTotal = ref(0)
const reviewPage = ref(1)
const isFavorited = ref(false)
const loading = ref(true)

const tags = computed(() => {
  if (!farmhouse.value?.tags) return []
  return farmhouse.value.tags.split(',').filter(Boolean)
})

const features = computed(() => {
  if (!farmhouse.value?.features) return []
  try {
    const parsed = JSON.parse(farmhouse.value.features)
    if (Array.isArray(parsed)) return parsed
  } catch {}
  return farmhouse.value.features.split(',').filter(Boolean)
})

const imageList = computed(() => {
  if (!farmhouse.value?.images) return []
  try {
    const parsed = JSON.parse(farmhouse.value.images)
    if (Array.isArray(parsed)) return parsed
  } catch {}
  return farmhouse.value.images.split(',').filter(Boolean)
})

async function loadFarmhouse() {
  loading.value = true
  try {
    const res = await getDetail(farmhouseId) as unknown as ApiResult<Farmhouse>
    if (res.code === 200) {
      farmhouse.value = res.data
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function loadPackages() {
  try {
    const res = await getPackages(farmhouseId) as unknown as ApiResult<FarmhousePackage[]>
    if (res.code === 200) {
      packages.value = Array.isArray(res.data) ? res.data : []
    }
  } catch {
    // ignore
  }
}

async function loadReviews() {
  try {
    const res = await getReviews(farmhouseId, reviewPage.value, 10) as unknown as ApiResult<PageResult<Review>>
    if (res.code === 200 && res.data) {
      reviews.value = res.data.records || []
      reviewTotal.value = res.data.total || 0
    }
  } catch {
    // ignore
  }
}

async function loadFavoriteStatus() {
  if (!userStore.isLoggedIn) return
  try {
    const res = await checkFavorite(farmhouseId) as unknown as ApiResult<boolean>
    if (res.code === 200) {
      isFavorited.value = !!res.data
    }
  } catch {
    // ignore
  }
}

async function toggleFavorite() {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    if (isFavorited.value) {
      const res = await removeFavorite(farmhouseId) as unknown as ApiResult
      if (res.code === 200) {
        isFavorited.value = false
        message.success('已取消收藏')
      }
    } else {
      const res = await addFavorite(farmhouseId) as unknown as ApiResult
      if (res.code === 200) {
        isFavorited.value = true
        message.success('已收藏')
      }
    }
  } catch {
    message.error('操作失败')
  }
}

function goBooking(pkg: FarmhousePackage) {
  router.push(`/farmhouse/${farmhouseId}/package/${pkg.id}`)
}

function handleReviewPageChange(page: number) {
  reviewPage.value = page
  loadReviews()
}

onMounted(() => {
  loadFarmhouse()
  loadPackages()
  loadReviews()
  loadFavoriteStatus()
})
</script>

<template>
  <div class="detail-page">
    <NSpin :show="loading">
      <template v-if="farmhouse">
        <!-- Cover Section -->
        <section class="cover-section">
          <div class="cover-main">
            <img
              v-if="farmhouse.coverImage"
              :src="farmhouse.coverImage"
              :alt="farmhouse.name"
              class="cover-image"
            />
            <div v-else class="cover-placeholder">
              <span>{{ farmhouse.name?.charAt(0) || '农' }}</span>
            </div>
          </div>
          <div v-if="imageList.length > 1" class="cover-thumbs">
            <img
              v-for="(img, idx) in imageList.slice(0, 4)"
              :key="idx"
              :src="img"
              class="thumb-img"
            />
          </div>
        </section>

        <div class="container detail-content">
          <!-- Info Section -->
          <section class="info-section">
            <div class="info-header">
              <div>
                <h1 class="farm-name">{{ farmhouse.name }}</h1>
                <div class="farm-rating">
                  <NRate :value="farmhouse.rating" readonly allow-half :size="18" />
                  <span class="rating-text">{{ farmhouse.rating?.toFixed(1) || '暂无' }}</span>
                  <span class="review-count">{{ farmhouse.reviewCount || 0 }}条评价</span>
                </div>
              </div>
              <NButton
                :type="isFavorited ? 'error' : 'default'"
                :ghost="!isFavorited"
                round
                @click="toggleFavorite"
              >
                <template #icon>
                  <NIcon>
                    <Heart v-if="isFavorited" />
                    <HeartOutline v-else />
                  </NIcon>
                </template>
                {{ isFavorited ? '已收藏' : '收藏' }}
              </NButton>
            </div>

            <div class="farm-meta">
              <div class="meta-item">
                <NIcon :size="16" color="#6B7D6B"><LocationOutline /></NIcon>
                <span>{{ farmhouse.address || '暂无地址' }}</span>
              </div>
              <div class="meta-item">
                <NIcon :size="16" color="#6B7D6B"><CallOutline /></NIcon>
                <span>{{ farmhouse.phone || '暂无电话' }}</span>
              </div>
              <div class="meta-item" v-if="farmhouse.businessHours">
                <NIcon :size="16" color="#6B7D6B"><TimeOutline /></NIcon>
                <span>{{ farmhouse.businessHours }}</span>
              </div>
            </div>

            <div class="farm-tags" v-if="tags.length">
              <NTag
                v-for="tag in tags"
                :key="tag"
                size="medium"
                :bordered="false"
                type="success"
                round
              >
                {{ tag }}
              </NTag>
            </div>

            <div class="farm-desc">
              <h3>简介</h3>
              <p>{{ farmhouse.description || '暂无介绍' }}</p>
            </div>

            <div class="farm-features" v-if="features.length">
              <h3>特色服务</h3>
              <div class="features-grid">
                <div v-for="feature in features" :key="feature" class="feature-item">
                  <NIcon :size="16" color="#5B8C5A"><StarOutline /></NIcon>
                  <span>{{ feature }}</span>
                </div>
              </div>
            </div>
          </section>

          <NDivider />

          <!-- Packages Section -->
          <section class="packages-section">
            <h2 class="section-title">套餐项目</h2>
            <div v-if="packages.length" class="packages-grid">
              <NCard
                v-for="pkg in packages"
                :key="pkg.id"
                class="package-card"
                hoverable
              >
                <div class="package-cover">
                  <img v-if="pkg.coverImage" :src="pkg.coverImage" :alt="pkg.name" />
                  <div v-else class="pkg-placeholder">{{ pkg.name?.charAt(0) || '套' }}</div>
                </div>
                <div class="package-info">
                  <h4>{{ pkg.name }}</h4>
                  <p class="pkg-desc">{{ pkg.description }}</p>
                  <div class="pkg-meta">
                    <span v-if="pkg.capacity">容纳 {{ pkg.capacity }} 人</span>
                    <span v-if="pkg.duration">时长 {{ pkg.duration }}</span>
                  </div>
                  <div class="pkg-bottom">
                    <div class="pkg-price">
                      <span class="current-price">¥{{ pkg.price }}</span>
                      <span v-if="pkg.originalPrice && pkg.originalPrice > pkg.price" class="original-price">
                        ¥{{ pkg.originalPrice }}
                      </span>
                    </div>
                    <NButton type="primary" size="small" @click="goBooking(pkg)">
                      立即预约
                    </NButton>
                  </div>
                </div>
              </NCard>
            </div>
            <NEmpty v-else description="暂无套餐" />
          </section>

          <NDivider />

          <!-- Reviews Section -->
          <section class="reviews-section">
            <h2 class="section-title">用户评价 ({{ reviewTotal }})</h2>
            <div v-if="reviews.length" class="reviews-list">
              <div v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <NAvatar :size="36" round>
                    <template #fallback>用户</template>
                  </NAvatar>
                  <div class="review-user">
                    <span class="review-username">用户{{ review.userId }}</span>
                    <NRate :value="review.rating" readonly allow-half :size="12" />
                  </div>
                  <span class="review-time">{{ review.createTime }}</span>
                </div>
                <p class="review-content">{{ review.content }}</p>
                <div v-if="review.adminReply" class="review-reply">
                  <span class="reply-label">商家回复：</span>
                  {{ review.adminReply }}
                </div>
              </div>
            </div>
            <NEmpty v-else description="暂无评价" />
            <div v-if="reviewTotal > 10" class="reviews-pagination">
              <NPagination
                :page="reviewPage"
                :page-size="10"
                :item-count="reviewTotal"
                @update:page="handleReviewPageChange"
              />
            </div>
          </section>
        </div>
      </template>
      <NEmpty v-else-if="!loading" description="农家乐不存在" style="padding: 120px 0" />
    </NSpin>
  </div>
</template>

<style scoped>
.detail-page {
  min-height: calc(100vh - var(--nav-height));
}

/* Cover */
.cover-section {
  max-width: var(--max-width);
  margin: 0 auto;
  padding: 24px 24px 0;
}

.cover-main {
  width: 100%;
  height: 400px;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8e2d8 0%, #d4c5a9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80px;
  font-weight: 700;
  color: #fff;
}

.cover-thumbs {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.thumb-img {
  width: 120px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  cursor: pointer;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.thumb-img:hover {
  opacity: 1;
}

/* Info */
.detail-content {
  padding-top: 32px;
  padding-bottom: 60px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.farm-name {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 8px;
}

.farm-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rating-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-accent);
}

.review-count {
  font-size: 13px;
  color: var(--color-text-light);
}

.farm-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 16px 0;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.farm-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.farm-desc h3,
.farm-features h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 8px;
}

.farm-desc p {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.8;
  margin-bottom: 20px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* Packages */
.section-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 20px;
}

.packages-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.package-card :deep(.n-card__content) {
  padding: 0;
  display: flex;
}

.package-cover {
  width: 180px;
  min-height: 160px;
  flex-shrink: 0;
  overflow: hidden;
}

.package-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pkg-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8e2d8 0%, #d4c5a9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: #fff;
  font-weight: 700;
}

.package-info {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.package-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 6px;
}

.pkg-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
  line-height: 1.5;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.pkg-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--color-text-light);
  margin: 8px 0;
}

.pkg-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pkg-price {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.current-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-accent);
}

.original-price {
  font-size: 13px;
  color: var(--color-text-light);
  text-decoration: line-through;
}

/* Reviews */
.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  background: var(--color-bg-card);
  border-radius: var(--radius-md);
  padding: 16px;
  border: 1px solid var(--color-border-light);
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.review-user {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.review-username {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-primary);
}

.review-time {
  font-size: 12px;
  color: var(--color-text-light);
}

.review-content {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.7;
}

.review-reply {
  margin-top: 10px;
  padding: 10px 12px;
  background: #f9f8f6;
  border-radius: var(--radius-sm);
  font-size: 13px;
  color: var(--color-text-secondary);
}

.reply-label {
  color: var(--color-primary);
  font-weight: 500;
}

.reviews-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .cover-main {
    height: 240px;
  }

  .packages-grid {
    grid-template-columns: 1fr;
  }

  .package-card :deep(.n-card__content) {
    flex-direction: column;
  }

  .package-cover {
    width: 100%;
    min-height: 120px;
  }
}
</style>
