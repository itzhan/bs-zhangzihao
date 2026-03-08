<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  NTabs, NTabPane, NCard, NButton, NTag, NEmpty,
  NSpin, NPagination, NModal, NInput, NRadioGroup, NRadio, useMessage
} from 'naive-ui'
import { getMyList, pay as payOrder, cancel as cancelOrder, requestRefund } from '@/api/order'
import type { Order, ApiResult, PageResult } from '@/types'

const message = useMessage()

const orders = ref<Order[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const activeTab = ref('all')

// Pay modal
const showPayModal = ref(false)
const payingOrderId = ref<number | null>(null)
const paymentMethod = ref('wechat')

// Cancel/Refund modal
const showCancelModal = ref(false)
const cancellingOrderId = ref<number | null>(null)
const cancelReason = ref('')
const cancelType = ref<'cancel' | 'refund'>('cancel')

const statusMap: Record<number, { label: string; type: 'default' | 'info' | 'success' | 'warning' | 'error' }> = {
  0: { label: '待支付', type: 'warning' },
  1: { label: '已支付', type: 'success' },
  2: { label: '已取消', type: 'error' },
  3: { label: '已退款', type: 'default' },
  4: { label: '已完成', type: 'info' }
}

const tabs = [
  { name: 'all', label: '全部' },
  { name: '0', label: '待支付' },
  { name: '1', label: '已支付' },
  { name: '2', label: '已取消' },
  { name: '4', label: '已完成' }
]

function getStatus(status: number) {
  return statusMap[status] || statusMap[0]
}

async function loadOrders() {
  loading.value = true
  try {
    const status = activeTab.value === 'all' ? undefined : Number(activeTab.value)
    const res = await getMyList(page.value, 10, status) as unknown as ApiResult<PageResult<Order>>
    if (res.code === 200 && res.data) {
      orders.value = res.data.records || []
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
  loadOrders()
}

function handlePageChange(p: number) {
  page.value = p
  loadOrders()
}

// Pay
function openPayModal(id: number) {
  payingOrderId.value = id
  paymentMethod.value = 'wechat'
  showPayModal.value = true
}

async function confirmPay() {
  if (!payingOrderId.value) return
  try {
    const res = await payOrder(payingOrderId.value, paymentMethod.value) as unknown as ApiResult
    if (res.code === 200) {
      message.success('支付成功')
      showPayModal.value = false
      loadOrders()
    } else {
      message.error(res.message || '支付失败')
    }
  } catch {
    message.error('支付失败')
  }
}

// Cancel/Refund
function openCancelModal(id: number, type: 'cancel' | 'refund') {
  cancellingOrderId.value = id
  cancelReason.value = ''
  cancelType.value = type
  showCancelModal.value = true
}

async function confirmCancelOrRefund() {
  if (!cancellingOrderId.value) return
  try {
    let res: ApiResult
    if (cancelType.value === 'cancel') {
      res = await cancelOrder(cancellingOrderId.value, cancelReason.value) as unknown as ApiResult
    } else {
      res = await requestRefund(cancellingOrderId.value, cancelReason.value) as unknown as ApiResult
    }
    if (res.code === 200) {
      message.success(cancelType.value === 'cancel' ? '已取消订单' : '退款申请已提交')
      showCancelModal.value = false
      loadOrders()
    } else {
      message.error(res.message || '操作失败')
    }
  } catch {
    message.error('操作失败')
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<template>
  <div class="orders-page">
    <div class="container">
      <h1 class="page-title">我的订单</h1>

      <NTabs v-model:value="activeTab" type="line" @update:value="handleTabChange">
        <NTabPane v-for="tab in tabs" :key="tab.name" :name="tab.name" :tab="tab.label" />
      </NTabs>

      <NSpin :show="loading">
        <div v-if="orders.length" class="order-list">
          <NCard v-for="item in orders" :key="item.id" class="order-card">
            <div class="order-header">
              <span class="order-no">订单号：{{ item.orderNo }}</span>
              <NTag :type="getStatus(item.status).type" size="small" round>
                {{ getStatus(item.status).label }}
              </NTag>
            </div>
            <div class="order-body">
              <div class="order-info">
                <h4>{{ item.farmhouseName }}</h4>
                <p class="order-package">{{ item.packageName }}</p>
                <p class="order-detail">{{ item.personCount }}人 · 单价 ¥{{ item.unitPrice }}</p>
              </div>
              <div class="order-right">
                <div class="order-amount">
                  <span class="amount-label">合计</span>
                  <span class="amount-value">¥{{ item.totalAmount }}</span>
                </div>
                <div class="order-actions">
                  <NButton
                    v-if="item.status === 0"
                    type="primary"
                    size="small"
                    @click="openPayModal(item.id)"
                  >
                    去支付
                  </NButton>
                  <NButton
                    v-if="item.status === 0"
                    size="small"
                    @click="openCancelModal(item.id, 'cancel')"
                  >
                    取消
                  </NButton>
                  <NButton
                    v-if="item.status === 1"
                    size="small"
                    @click="openCancelModal(item.id, 'refund')"
                  >
                    申请退款
                  </NButton>
                </div>
              </div>
            </div>
            <div class="order-footer">
              <span>下单时间：{{ item.createTime }}</span>
              <span v-if="item.payTime">支付时间：{{ item.payTime }}</span>
            </div>
          </NCard>
        </div>
        <NEmpty v-else-if="!loading" description="暂无订单" style="padding: 80px 0" />
      </NSpin>

      <div v-if="total > 10" class="pagination-wrap">
        <NPagination
          :page="page"
          :page-size="10"
          :item-count="total"
          @update:page="handlePageChange"
        />
      </div>

      <!-- Pay Modal -->
      <NModal v-model:show="showPayModal" preset="dialog" title="选择支付方式" positive-text="确认支付" negative-text="取消" @positive-click="confirmPay">
        <NRadioGroup v-model:value="paymentMethod" style="margin-top: 12px">
          <NRadio value="wechat">微信支付</NRadio>
          <NRadio value="alipay">支付宝</NRadio>
        </NRadioGroup>
      </NModal>

      <!-- Cancel/Refund Modal -->
      <NModal
        v-model:show="showCancelModal"
        preset="dialog"
        :title="cancelType === 'cancel' ? '取消订单' : '申请退款'"
        positive-text="确认"
        negative-text="返回"
        @positive-click="confirmCancelOrRefund"
      >
        <NInput
          v-model:value="cancelReason"
          type="textarea"
          :placeholder="cancelType === 'cancel' ? '请填写取消原因（选填）' : '请填写退款原因'"
          :rows="3"
          style="margin-top: 12px"
        />
      </NModal>
    </div>
  </div>
</template>

<style scoped>
.orders-page {
  padding: 32px 0 60px;
  min-height: calc(100vh - var(--nav-height));
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 20px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 20px;
}

.order-card {
  border-radius: var(--radius-md);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-size: 13px;
  color: var(--color-text-light);
  font-family: monospace;
}

.order-body {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.order-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.order-package {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 4px;
}

.order-detail {
  font-size: 13px;
  color: var(--color-text-light);
}

.order-right {
  text-align: right;
  flex-shrink: 0;
}

.amount-label {
  font-size: 13px;
  color: var(--color-text-light);
}

.amount-value {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: var(--color-accent);
  margin-bottom: 8px;
}

.order-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.order-footer {
  display: flex;
  gap: 20px;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid var(--color-border-light);
  font-size: 12px;
  color: var(--color-text-light);
}

.pagination-wrap {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}
</style>
