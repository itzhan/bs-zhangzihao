<script setup lang="ts">
import { ref, onMounted, nextTick, onBeforeUnmount } from "vue";
import {
  getStatistics,
  getReservationTrend,
  getRevenueTrend,
  getTopFarmhouses,
  getRatingDistribution
} from "@/api/dashboard";
import * as echarts from "echarts";

defineOptions({ name: "Dashboard" });

const stats = ref<any>({});
const reservationChartRef = ref<HTMLDivElement>();
const revenueChartRef = ref<HTMLDivElement>();
const topChartRef = ref<HTMLDivElement>();
const ratingChartRef = ref<HTMLDivElement>();

let charts: echarts.ECharts[] = [];

const statCards = [
  { key: "totalUsers", label: "总用户数", color: "#4a8c2a", icon: "👥" },
  { key: "totalFarmhouses", label: "总农家乐", color: "#2d5016", icon: "🏡" },
  { key: "totalReservations", label: "总预约数", color: "#8B4513", icon: "📅" },
  { key: "totalOrders", label: "总订单数", color: "#d4a017", icon: "💰" },
  { key: "totalRevenue", label: "总收入", color: "#e8703a", prefix: "¥", icon: "💵" },
  { key: "todayNewUsers", label: "今日新用户", color: "#5ba832", icon: "🌟" },
  { key: "todayNewOrders", label: "今日新订单", color: "#c0392b", icon: "📦" },
  { key: "todayNewReservations", label: "今日新预约", color: "#3d7a1c", icon: "✨" }
];

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
      title: { text: "近30天预约趋势", left: "center", textStyle: { fontSize: 14 } },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: data.map((d: any) => d.date) },
      yAxis: { type: "value" },
      series: [{ data: data.map((d: any) => d.count), type: "line", smooth: true, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(74,140,42,0.4)' }, { offset: 1, color: 'rgba(74,140,42,0.05)' }]) }, itemStyle: { color: "#4a8c2a" }, lineStyle: { color: "#4a8c2a" } }],
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
      title: { text: "近30天收入趋势", left: "center", textStyle: { fontSize: 14 } },
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: data.map((d: any) => d.date) },
      yAxis: { type: "value" },
      series: [{ data: data.map((d: any) => d.amount), type: "line", smooth: true, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(139,69,19,0.4)' }, { offset: 1, color: 'rgba(139,69,19,0.05)' }]) }, itemStyle: { color: "#8B4513" }, lineStyle: { color: "#8B4513" } }],
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
      title: { text: "热门农家乐TOP10", left: "center", textStyle: { fontSize: 14 } },
      tooltip: { trigger: "axis", axisPointer: { type: "shadow" } },
      xAxis: { type: "value" },
      yAxis: { type: "category", data: data.map((d: any) => d.name).reverse() },
      series: [{ data: data.map((d: any) => d.reservationCount).reverse(), type: "bar", itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [{ offset: 0, color: '#2d5016' }, { offset: 1, color: '#4a8c2a' }]) } }],
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
    chart.setOption({
      title: { text: "评分分布", left: "center", textStyle: { fontSize: 14 } },
      tooltip: { trigger: "item" },
      legend: { bottom: 0 },
      series: [{
        type: "pie",
        radius: ["35%", "60%"],
        data: data.map((d: any) => ({ name: `${d.rating}星`, value: d.count })),
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: "rgba(0,0,0,0.5)" } }
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
  <div class="main">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-4">
      <el-col v-for="card in statCards" :key="card.key" :xs="12" :sm="6" :md="6" :lg="3">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value" :style="{ color: card.color }">
            {{ card.prefix ?? "" }}{{ stats[card.key] ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div ref="reservationChartRef" style="height: 350px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div ref="revenueChartRef" style="height: 350px" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门 & 评分 -->
    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div ref="topChartRef" style="height: 350px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never">
          <div ref="ratingChartRef" style="height: 350px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.main {
  margin: 16px;
}
.stat-card {
  text-align: center;
  margin-bottom: 8px;
  border-radius: 12px;
  transition: all 0.3s ease;
}
.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(45, 80, 22, 0.15);
}
.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 24px;
  font-weight: bold;
}
</style>
