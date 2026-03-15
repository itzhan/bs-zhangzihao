<script setup lang="ts">
import { ref, computed, onMounted, nextTick, onBeforeUnmount } from "vue";
import {
  getStatistics,
  getReservationTrend,
  getRevenueTrend,
  getTopFarmhouses,
  getRatingDistribution
} from "@/api/dashboard";
import * as echarts from "echarts";
import { useRouter } from "vue-router";

// Lucide icons via unplugin-icons
import IconUsers from "~icons/lucide/users";
import IconHome from "~icons/lucide/home";
import IconCalendar from "~icons/lucide/calendar-days";
import IconCoins from "~icons/lucide/coins";
import IconBanknote from "~icons/lucide/banknote";
import IconUserPlus from "~icons/lucide/user-plus";
import IconShoppingBag from "~icons/lucide/shopping-bag";
import IconCalendarPlus from "~icons/lucide/calendar-plus";
import IconArrowUpRight from "~icons/lucide/arrow-up-right";
import IconTrendingUp from "~icons/lucide/trending-up";

// Quick action icons
import IconStore from "~icons/lucide/store";
import IconPackage from "~icons/lucide/package";
import IconCalendarRange from "~icons/lucide/calendar-range";
import IconClipboardList from "~icons/lucide/clipboard-list";
import IconFileText from "~icons/lucide/file-text";
import IconStar from "~icons/lucide/star";
import IconUserCog from "~icons/lucide/user-cog";
import IconSettings from "~icons/lucide/settings";

defineOptions({ name: "Dashboard" });

const router = useRouter();
const stats = ref<any>({});
const reservationChartRef = ref<HTMLDivElement>();
const revenueChartRef = ref<HTMLDivElement>();
const topChartRef = ref<HTMLDivElement>();
const ratingChartRef = ref<HTMLDivElement>();

let charts: echarts.ECharts[] = [];

const statCards = [
  { key: "totalUsers", label: "总用户数", color: "#4a8c2a", bgColor: "#eef5e8", icon: IconUsers },
  { key: "totalFarmhouses", label: "总农家乐", color: "#2d5016", bgColor: "#e8f0e0", icon: IconHome },
  { key: "totalReservations", label: "总预约数", color: "#8B4513", bgColor: "#f5ede4", icon: IconCalendar },
  { key: "totalOrders", label: "总订单数", color: "#d4a017", bgColor: "#faf3e0", icon: IconCoins },
  { key: "totalRevenue", label: "总收入", color: "#e8703a", bgColor: "#fdf0e8", prefix: "¥", icon: IconBanknote },
  { key: "todayNewUsers", label: "今日新用户", color: "#5ba832", bgColor: "#edf7e5", icon: IconUserPlus },
  { key: "todayNewOrders", label: "今日新订单", color: "#c0392b", bgColor: "#fce8e6", icon: IconShoppingBag },
  { key: "todayNewReservations", label: "今日新预约", color: "#3d7a1c", bgColor: "#e6f2dc", icon: IconCalendarPlus }
];

const quickActions = [
  { label: "农家乐管理", icon: IconStore, path: "/farmhouse/list", color: "#2d5016" },
  { label: "套餐管理", icon: IconPackage, path: "/farmhouse/packages", color: "#4a8c2a" },
  { label: "档期管理", icon: IconCalendarRange, path: "/farmhouse/schedules", color: "#8B4513" },
  { label: "预约管理", icon: IconClipboardList, path: "/business/reservations", color: "#d4a017" },
  { label: "订单管理", icon: IconFileText, path: "/business/orders", color: "#e8703a" },
  { label: "评价管理", icon: IconStar, path: "/business/reviews", color: "#c0392b" },
  { label: "用户管理", icon: IconUserCog, path: "/content/users", color: "#5ba832" },
  { label: "系统设置", icon: IconSettings, path: "/system-manage/settings", color: "#3d7a1c" }
];

const greeting = computed(() => {
  const h = new Date().getHours();
  if (h < 6) return "夜深了";
  if (h < 12) return "上午好";
  if (h < 14) return "中午好";
  if (h < 18) return "下午好";
  return "晚上好";
});

function goTo(path: string) {
  router.push(path);
}

async function loadStats() {
  try {
    const res: any = await getStatistics();
    stats.value = res.data ?? {};
  } catch {
    stats.value = {};
  }
}

async function loadReservationTrend() {
  try {
    const res: any = await getReservationTrend({ days: 30 });
    const data = res.data ?? [];
    if (!reservationChartRef.value) return;
    const chart = echarts.init(reservationChartRef.value);
    charts.push(chart);
    chart.setOption({
      title: { text: "近30天预约趋势", left: "center", textStyle: { fontSize: 14, fontWeight: 600 } },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: data.map((d: any) => d.date), axisLabel: { fontSize: 11 } },
      yAxis: { type: "value" },
      series: [{ data: data.map((d: any) => d.count), type: "line", smooth: true, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(74,140,42,0.4)' }, { offset: 1, color: 'rgba(74,140,42,0.05)' }]) }, itemStyle: { color: "#4a8c2a" }, lineStyle: { color: "#4a8c2a", width: 2.5 } }],
      grid: { left: 50, right: 20, bottom: 30, top: 50 }
    });
  } catch {
    /* empty */
  }
}

async function loadRevenueTrend() {
  try {
    const res: any = await getRevenueTrend({ days: 30 });
    const data = res.data ?? [];
    if (!revenueChartRef.value) return;
    const chart = echarts.init(revenueChartRef.value);
    charts.push(chart);
    chart.setOption({
      title: { text: "近30天收入趋势", left: "center", textStyle: { fontSize: 14, fontWeight: 600 } },
      tooltip: { trigger: "axis", valueFormatter: (v: any) => `¥${v}` },
      xAxis: { type: "category", data: data.map((d: any) => d.date), axisLabel: { fontSize: 11 } },
      yAxis: { type: "value" },
      series: [{ data: data.map((d: any) => d.revenue), type: "line", smooth: true, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(139,69,19,0.4)' }, { offset: 1, color: 'rgba(139,69,19,0.05)' }]) }, itemStyle: { color: "#8B4513" }, lineStyle: { color: "#8B4513", width: 2.5 } }],
      grid: { left: 60, right: 20, bottom: 30, top: 50 }
    });
  } catch {
    /* empty */
  }
}

async function loadTopFarmhouses() {
  try {
    const res: any = await getTopFarmhouses({ limit: 10 });
    const data = res.data ?? [];
    if (!topChartRef.value) return;
    const chart = echarts.init(topChartRef.value);
    charts.push(chart);
    chart.setOption({
      title: { text: "热门农家乐TOP10", left: "center", textStyle: { fontSize: 14, fontWeight: 600 } },
      tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
      xAxis: { type: "value" },
      yAxis: { type: "category", data: data.map((d: any) => d.farmhouseName).reverse(), axisLabel: { fontSize: 11 } },
      series: [{ data: data.map((d: any) => d.orderCount).reverse(), type: "bar", barWidth: 18, itemStyle: { borderRadius: [0, 6, 6, 0], color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#2d5016' }, { offset: 1, color: '#4a8c2a' }]) } }],
      grid: { left: 120, right: 30, bottom: 30, top: 50 }
    });
  } catch {
    /* empty */
  }
}

async function loadRatingDistribution() {
  try {
    const res: any = await getRatingDistribution();
    const data = res.data ?? [];
    if (!ratingChartRef.value) return;
    const chart = echarts.init(ratingChartRef.value);
    charts.push(chart);
    const colors = ['#c0392b', '#e8703a', '#d4a017', '#5ba832', '#2d5016'];
    chart.setOption({
      title: { text: "评分分布", left: "center", textStyle: { fontSize: 14, fontWeight: 600 } },
      tooltip: { trigger: "item" },
      legend: { bottom: 0 },
      series: [{
        type: "pie",
        radius: ["35%", "60%"],
        data: data.map((d: any, i: number) => ({ name: `${d.rating}星`, value: d.count, itemStyle: { color: colors[i % colors.length] } })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: "rgba(0,0,0,0.5)" } },
        label: { formatter: '{b}: {c}条' }
      }]
    });
  } catch {
    /* empty */
  }
}

function handleResize() {
  charts.forEach(c => c.resize());
}

onMounted(async () => {
  await loadStats();
  await nextTick();
  loadReservationTrend();
  loadRevenueTrend();
  loadTopFarmhouses();
  loadRatingDistribution();
  window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", handleResize);
  charts.forEach(c => c.dispose());
  charts = [];
});
</script>

<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-left">
        <h2 class="welcome-title">{{ greeting }}，管理员 👋</h2>
        <p class="welcome-desc">欢迎回到农家乐预约管理系统控制台，这是今日的运营快报</p>
      </div>
      <div class="welcome-right">
        <component :is="IconTrendingUp" class="welcome-icon" />
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-4">
      <el-col v-for="card in statCards" :key="card.key" :xs="12" :sm="6" :md="6" :lg="6">
        <div class="stat-card" @click="goTo('/business/order')">
          <div class="stat-icon-box" :style="{ background: card.bgColor }">
            <component :is="card.icon" class="stat-icon" :style="{ color: card.color }" />
          </div>
          <div class="stat-info">
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-value" :style="{ color: card.color }">
              {{ card.prefix ?? "" }}{{ stats[card.key] ?? 0 }}
            </div>
          </div>
          <component :is="IconArrowUpRight" class="stat-arrow" />
        </div>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <div class="quick-actions-section">
      <h3 class="section-title">快捷操作</h3>
      <div class="quick-actions-grid">
        <div
          v-for="action in quickActions"
          :key="action.label"
          class="quick-action-item"
          @click="goTo(action.path)"
        >
          <div class="quick-action-icon" :style="{ background: action.color + '14', color: action.color }">
            <component :is="action.icon" />
          </div>
          <span class="quick-action-label">{{ action.label }}</span>
        </div>
      </div>
    </div>

    <!-- 趋势图 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div ref="reservationChartRef" style="height: 350px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div ref="revenueChartRef" style="height: 350px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门 & 评分 -->
    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div ref="topChartRef" style="height: 350px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="chart-card">
          <div ref="ratingChartRef" style="height: 350px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px;
}

/* 欢迎横幅 */
.welcome-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28px 32px;
  margin-bottom: 20px;
  border-radius: 16px;
  background: linear-gradient(135deg, #2d5016 0%, #4a8c2a 50%, #3d7a1c 100%);
  color: #fff;
  overflow: hidden;
  position: relative;
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: -30px;
  right: -30px;
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 50%;
}

.welcome-banner::after {
  content: '';
  position: absolute;
  bottom: -40px;
  right: 60px;
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 50%;
}

.welcome-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0 0 6px;
}

.welcome-desc {
  font-size: 14px;
  margin: 0;
  opacity: 0.85;
}

.welcome-icon {
  width: 56px;
  height: 56px;
  opacity: 0.3;
  position: relative;
  z-index: 1;
}

/* 统计卡片 */
.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(45, 80, 22, 0.12);
  border-color: rgba(74, 140, 42, 0.2);
}

.stat-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon {
  width: 24px;
  height: 24px;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-arrow {
  width: 16px;
  height: 16px;
  color: #c0c4cc;
  flex-shrink: 0;
  transition: color 0.3s;
}

.stat-card:hover .stat-arrow {
  color: #4a8c2a;
}

/* 快捷操作 */
.quick-actions-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 14px;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 12px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 8px;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-action-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.06);
  border-color: rgba(74, 140, 42, 0.25);
}

.quick-action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-action-icon svg {
  width: 20px;
  height: 20px;
}

.quick-action-label {
  font-size: 12px;
  color: #606266;
  text-align: center;
}

/* 图表卡片 */
.chart-card {
  border-radius: 14px;
  margin-bottom: 12px;
}

.mb-4 {
  margin-bottom: 16px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .quick-actions-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .welcome-banner {
    padding: 20px;
  }

  .welcome-title {
    font-size: 18px;
  }

  .welcome-icon {
    display: none;
  }

  .quick-actions-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>
