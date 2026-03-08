<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  NButton, NCard, NInputNumber, NInput, NForm, NFormItem,
  NIcon, NSpin, NEmpty, NTag, useMessage
} from 'naive-ui'
import { CalendarOutline, PeopleOutline, TimeOutline, CheckmarkCircleOutline } from '@vicons/ionicons5'
import { getDetail as getPackageDetail } from '@/api/package'
import { getByPackage } from '@/api/schedule'
import { create as createReservation } from '@/api/reservation'
import { useUserStore } from '@/stores/user'
import type { FarmhousePackage, Schedule, ApiResult } from '@/types'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const farmhouseId = Number(route.params.id)
const packageId = Number(route.params.packageId)

const pkg = ref<FarmhousePackage | null>(null)
const schedules = ref<Schedule[]>([])
const loading = ref(true)
const submitting = ref(false)

const selectedSchedule = ref<Schedule | null>(null)

const formData = ref({
  personCount: 1,
  contactName: '',
  contactPhone: '',
  remark: ''
})

const includes = computed(() => {
  if (!pkg.value?.includes) return []
  return pkg.value.includes.split(',').filter(Boolean)
})

const availableSchedules = computed(() => {
  return schedules.value.filter(s => s.status === 1 && s.remainingQuota > 0)
})

function selectSchedule(schedule: Schedule) {
  selectedSchedule.value = schedule
}

function isSelected(schedule: Schedule) {
  return selectedSchedule.value?.id === schedule.id
}

async function loadPackage() {
  try {
    const res = await getPackageDetail(packageId) as unknown as ApiResult<FarmhousePackage>
    if (res.code === 200) {
      pkg.value = res.data
    }
  } catch {
    // ignore
  }
}

async function loadSchedules() {
  try {
    const res = await getByPackage(packageId) as unknown as ApiResult<Schedule[]>
    if (res.code === 200) {
      schedules.value = Array.isArray(res.data) ? res.data : []
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  if (!userStore.isLoggedIn) {
    message.warning('请先登录')
    router.push(`/login?redirect=${route.fullPath}`)
    return
  }

  if (!selectedSchedule.value) {
    message.warning('请选择预约日期')
    return
  }

  if (!formData.value.contactName) {
    message.warning('请填写联系人姓名')
    return
  }

  if (!formData.value.contactPhone) {
    message.warning('请填写联系电话')
    return
  }

  if (formData.value.personCount > selectedSchedule.value.remainingQuota) {
    message.warning('人数超出剩余名额')
    return
  }

  submitting.value = true
  try {
    const res = await createReservation({
      farmhouseId,
      packageId,
      scheduleId: selectedSchedule.value.id,
      reserveDate: selectedSchedule.value.scheduleDate,
      personCount: formData.value.personCount,
      contactName: formData.value.contactName,
      contactPhone: formData.value.contactPhone,
      remark: formData.value.remark || undefined
    }) as unknown as ApiResult

    if (res.code === 200) {
      message.success('预约成功！')
      router.push('/my/reservations')
    } else {
      message.error(res.message || '预约失败')
    }
  } catch {
    message.error('预约失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPackage()
  loadSchedules()
})
</script>

<template>
  <div class="package-page">
    <div class="container">
      <NSpin :show="loading">
        <template v-if="pkg">
          <div class="package-layout">
            <!-- Left: Package Info -->
            <div class="package-left">
              <div class="pkg-cover">
                <img v-if="pkg.coverImage" :src="pkg.coverImage" :alt="pkg.name" />
                <div v-else class="pkg-cover-placeholder">{{ pkg.name?.charAt(0) || '套' }}</div>
              </div>

              <h1 class="pkg-name">{{ pkg.name }}</h1>

              <div class="pkg-price-section">
                <span class="pkg-current-price">¥{{ pkg.price }}</span>
                <span v-if="pkg.originalPrice && pkg.originalPrice > pkg.price" class="pkg-original-price">
                  ¥{{ pkg.originalPrice }}
                </span>
                <NTag v-if="pkg.originalPrice && pkg.originalPrice > pkg.price" type="warning" size="small" :bordered="false">
                  优惠
                </NTag>
              </div>

              <div class="pkg-meta-list">
                <div class="pkg-meta-item" v-if="pkg.capacity">
                  <NIcon :size="18" color="#6B7D6B"><PeopleOutline /></NIcon>
                  <span>可容纳 {{ pkg.capacity }} 人</span>
                </div>
                <div class="pkg-meta-item" v-if="pkg.duration">
                  <NIcon :size="18" color="#6B7D6B"><TimeOutline /></NIcon>
                  <span>时长 {{ pkg.duration }}</span>
                </div>
              </div>

              <div class="pkg-description" v-if="pkg.description">
                <h3>套餐介绍</h3>
                <p>{{ pkg.description }}</p>
              </div>

              <div class="pkg-includes" v-if="includes.length">
                <h3>套餐包含</h3>
                <div class="includes-list">
                  <div v-for="item in includes" :key="item" class="include-item">
                    <NIcon :size="16" color="#5B8C5A"><CheckmarkCircleOutline /></NIcon>
                    <span>{{ item }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right: Booking Form -->
            <div class="package-right">
              <NCard title="预约信息" class="booking-card">
                <!-- Schedule Calendar -->
                <div class="schedule-section">
                  <h4>
                    <NIcon :size="16"><CalendarOutline /></NIcon>
                    选择预约日期
                  </h4>
                  <div v-if="availableSchedules.length" class="schedule-grid">
                    <div
                      v-for="schedule in availableSchedules"
                      :key="schedule.id"
                      :class="['schedule-item', { selected: isSelected(schedule) }]"
                      @click="selectSchedule(schedule)"
                    >
                      <span class="schedule-date">{{ schedule.scheduleDate }}</span>
                      <span class="schedule-quota">余{{ schedule.remainingQuota }}位</span>
                      <span v-if="schedule.priceOverride" class="schedule-price">¥{{ schedule.priceOverride }}</span>
                    </div>
                  </div>
                  <NEmpty v-else size="small" description="暂无可预约日期" />
                </div>

                <!-- Booking Form -->
                <NForm class="booking-form" label-placement="top">
                  <NFormItem label="预约人数">
                    <NInputNumber
                      v-model:value="formData.personCount"
                      :min="1"
                      :max="selectedSchedule?.remainingQuota || 99"
                      style="width: 100%"
                    />
                  </NFormItem>
                  <NFormItem label="联系人">
                    <NInput v-model:value="formData.contactName" placeholder="请输入联系人姓名" />
                  </NFormItem>
                  <NFormItem label="联系电话">
                    <NInput v-model:value="formData.contactPhone" placeholder="请输入联系电话" />
                  </NFormItem>
                  <NFormItem label="备注">
                    <NInput
                      v-model:value="formData.remark"
                      type="textarea"
                      placeholder="有什么特殊要求可以备注（选填）"
                      :rows="3"
                    />
                  </NFormItem>
                </NForm>

                <!-- Total & Submit -->
                <div class="booking-total">
                  <span class="total-label">合计：</span>
                  <span class="total-price">
                    ¥{{ ((selectedSchedule?.priceOverride || pkg.price) * formData.personCount).toFixed(2) }}
                  </span>
                </div>

                <NButton
                  type="primary"
                  block
                  size="large"
                  :loading="submitting"
                  :disabled="!selectedSchedule"
                  @click="handleSubmit"
                  class="submit-btn"
                >
                  立即预约
                </NButton>
              </NCard>
            </div>
          </div>
        </template>
        <NEmpty v-else-if="!loading" description="套餐不存在" style="padding: 120px 0" />
      </NSpin>
    </div>
  </div>
</template>

<style scoped>
.package-page {
  padding: 32px 0 60px;
  min-height: calc(100vh - var(--nav-height));
}

.package-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 32px;
  align-items: start;
}

/* Left */
.pkg-cover {
  width: 100%;
  height: 320px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: 24px;
}

.pkg-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pkg-cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8e2d8 0%, #d4c5a9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64px;
  font-weight: 700;
  color: #fff;
}

.pkg-name {
  font-size: 26px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 12px;
}

.pkg-price-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 16px;
}

.pkg-current-price {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-accent);
}

.pkg-original-price {
  font-size: 16px;
  color: var(--color-text-light);
  text-decoration: line-through;
}

.pkg-meta-list {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.pkg-meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.pkg-description h3,
.pkg-includes h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 10px;
}

.pkg-description p {
  font-size: 14px;
  color: var(--color-text-secondary);
  line-height: 1.8;
  margin-bottom: 24px;
}

.includes-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.include-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

/* Right */
.booking-card {
  position: sticky;
  top: calc(var(--nav-height) + 20px);
}

.schedule-section {
  margin-bottom: 20px;
}

.schedule-section h4 {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 12px;
}

.schedule-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.schedule-item {
  padding: 10px 8px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.schedule-item:hover {
  border-color: var(--color-primary);
}

.schedule-item.selected {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.schedule-date {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.schedule-quota {
  font-size: 11px;
  color: var(--color-text-light);
}

.schedule-price {
  font-size: 12px;
  color: var(--color-accent);
  font-weight: 600;
}

.booking-form {
  margin-top: 16px;
}

.booking-total {
  display: flex;
  justify-content: flex-end;
  align-items: baseline;
  margin-bottom: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--color-border-light);
}

.total-label {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-accent);
}

.submit-btn {
  height: 46px;
  font-size: 16px;
}

@media (max-width: 768px) {
  .package-layout {
    grid-template-columns: 1fr;
  }

  .booking-card {
    position: static;
  }

  .pkg-cover {
    height: 200px;
  }
}
</style>
