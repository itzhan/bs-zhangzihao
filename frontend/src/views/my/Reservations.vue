<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  NTabs, NTabPane, NCard, NButton, NTag, NEmpty,
  NSpin, NPagination, NIcon, NModal, NInput, useMessage, useDialog
} from 'naive-ui'
import { CalendarOutline, PersonOutline, CallOutline } from '@vicons/ionicons5'
import { getMyList, cancel as cancelReservation } from '@/api/reservation'
import { create as createOrder } from '@/api/order'
import type { Reservation, ApiResult, PageResult } from '@/types'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const reservations = ref<Reservation[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const activeTab = ref('all')

const showCancelModal = ref(false)
const cancellingId = ref<number | null>(null)
const cancelReason = ref('')

const statusMap: Record<number, { label: string; type: 'default' | 'info' | 'success' | 'warning' | 'error' }> = {
  0: { label: '待确认', type: 'info' },
  1: { label: '已确认', type: 'success' },
  2: { label: '已取消', type: 'error' },
  3: { label: '已完成', type: 'default' }
}

const tabs = [
  { name: 'all', label: '全部' },
  { name: '0', label: '待确认' },
  { name: '1', label: '已确认' },
  { name: '2', label: '已取消' },
  { name: '3', label: '已完成' }
]

function getStatus(status: number) {
  return statusMap[status] || statusMap[0]
}

async function loadReservations() {
  loading.value = true
  try {
    const status = activeTab.value === 'all' ? undefined : Number(activeTab.value)
    const res = await getMyList(page.value, 10, status) as unknown as ApiResult<PageResult<Reservation>>
    if (res.code === 200 && res.data) {
      reservations.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  page.value = 1
  loadReservations()
}

function handlePageChange(p: number) {
  page.value = p
  loadReservations()
}

function openCancelModal(id: number) {
  cancellingId.value = id
  cancelReason.value = ''
  showCancelModal.value = true
}

async function confirmCancel() {
  if (!cancellingId.value) return
  try {
    const res = await cancelReservation(cancellingId.value, cancelReason.value) as unknown as ApiResult
    if (res.code === 200) {
      message.success('已取消预约')
      showCancelModal.value = false
      loadReservations()
    } else {
      message.error(res.message || '取消失败')
    }
  } catch {
    message.error('操作失败')
  }
}

function handlePay(reservation: Reservation) {
  dialog.info({
    title: '创建订单',
    content: '确认为该预约创建订单并前往支付？',
    positiveText: '确认',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const res = await createOrder({
          reservationId: reservation.id,
          farmhouseId: reservation.farmhouseId,
          packageId: reservation.packageId,
          personCount: reservation.personCount,
          unitPrice: 0,
          totalAmount: 0
        }) as unknown as ApiResult
        if (res.code === 200) {
          message.success('订单已创建')
          router.push('/my/orders')
        } else {
          message.error(res.message || '创建订单失败')
        }
      } catch {
        message.error('操作失败')
      }
    }
  })
}

onMounted(() => {
  loadReservations()
})
</script>

<template>
  <div class="reservations-page">
    <div class="container">
      <h1 class="page-title">我的预约</h1>

      <NTabs v-model:value="activeTab" type="line" @update:value="handleTabChange">
        <NTabPane v-for="tab in tabs" :key="tab.name" :name="tab.name" :tab="tab.label" />
      </NTabs>

      <NSpin :show="loading">
        <div v-if="reservations.length" class="reservation-list">
          <NCard v-for="item in reservations" :key="item.id" class="reservation-card">
            <div class="reservation-header">
              <span class="reservation-no">预约号：{{ item.reservationNo }}</span>
              <NTag :type="getStatus(item.status).type" size="small" round>
                {{ getStatus(item.status).label }}
              </NTag>
            </div>
            <div class="reservation-body">
              <div class="reservation-info">
                <div class="info-row">
                  <NIcon :size="14"><CalendarOutline /></NIcon>
                  <span>预约日期：{{ item.reserveDate }}</span>
                </div>
                <div class="info-row">
                  <NIcon :size="14"><PersonOutline /></NIcon>
                  <span>{{ item.contactName }} · {{ item.personCount }}人</span>
                </div>
                <div class="info-row">
                  <NIcon :size="14"><CallOutline /></NIcon>
                  <span>{{ item.contactPhone }}</span>
                </div>
                <div v-if="item.remark" class="info-row remark">
                  备注：{{ item.remark }}
                </div>
              </div>
              <div class="reservation-actions">
                <NButton
                  v-if="item.status === 0"
                  size="small"
                  @click="openCancelModal(item.id)"
                >
                  取消预约
                </NButton>
                <NButton
                  v-if="item.status === 1"
                  type="primary"
                  size="small"
                  @click="handlePay(item)"
                >
                  去支付
                </NButton>
              </div>
            </div>
            <div class="reservation-footer">
              <span class="create-time">提交时间：{{ item.createTime }}</span>
            </div>
          </NCard>
        </div>
        <NEmpty v-else-if="!loading" description="暂无预约记录" style="padding: 80px 0" />
      </NSpin>

      <div v-if="total > 10" class="pagination-wrap">
        <NPagination
          :page="page"
          :page-size="10"
          :item-count="total"
          @update:page="handlePageChange"
        />
      </div>

      <!-- Cancel Modal -->
      <NModal v-model:show="showCancelModal" preset="dialog" title="取消预约" positive-text="确认取消" negative-text="返回" @positive-click="confirmCancel">
        <NInput
          v-model:value="cancelReason"
          type="textarea"
          placeholder="请填写取消原因（选填）"
          :rows="3"
        />
      </NModal>
    </div>
  </div>
</template>

<style scoped>
.reservations-page {
  padding: 32px 0 60px;
  min-height: calc(100vh - var(--nav-height));
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 20px;
}

.reservation-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}

.reservation-card {
  border-radius: var(--radius-md);
}

.reservation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.reservation-no {
  font-size: 13px;
  color: var(--color-text-light);
  font-family: monospace;
}

.reservation-body {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
}

.reservation-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.info-row.remark {
  font-size: 13px;
  color: var(--color-text-light);
}

.reservation-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.reservation-footer {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid var(--color-border-light);
}

.create-time {
  font-size: 12px;
  color: var(--color-text-light);
}

.pagination-wrap {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
