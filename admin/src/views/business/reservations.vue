<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getReservationList,
  confirmReservation,
  completeReservation
} from "@/api/reservation";
import { getFarmhouseList } from "@/api/farmhouse";
import { getPackageList } from "@/api/package";
import { getUserList } from "@/api/admin-user";

defineOptions({ name: "ReservationList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const farmhouseOptions = ref<any[]>([]);
const packageOptions = ref<any[]>([]);
const userOptions = ref<any[]>([]);
const queryParams = reactive({
  page: 1,
  size: 10,
  status: null as number | null,
  farmhouseId: null as number | null
});

const statusOptions = [
  { label: "待确认", value: 0 },
  { label: "已确认", value: 1 },
  { label: "已取消", value: 2 },
  { label: "已完成", value: 3 }
];

function getStatusType(status: number) {
  const map: Record<number, string> = { 0: "warning", 1: "primary", 2: "info", 3: "success" };
  return map[status] ?? "info";
}

function getStatusName(status: number) {
  return statusOptions.find(s => s.value === status)?.label ?? "未知";
}

function getFarmhouseName(id: number) {
  return farmhouseOptions.value.find((f: any) => f.id === id)?.name ?? "-";
}
function getPackageName(id: number) {
  return packageOptions.value.find((p: any) => p.id === id)?.name ?? "-";
}
function getUserName(id: number) {
  return userOptions.value.find((u: any) => u.id === id)?.username ?? "-";
}

async function loadFarmhouses() {
  const res: any = await getFarmhouseList({ page: 1, size: 999 });
  farmhouseOptions.value = res.data?.records ?? [];
}
async function loadPackages() {
  const res: any = await getPackageList({ page: 1, size: 999 });
  packageOptions.value = res.data?.records ?? [];
}
async function loadUsers() {
  const res: any = await getUserList({ page: 1, size: 999 });
  userOptions.value = res.data?.records ?? [];
}

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getReservationList(queryParams);
    tableData.value = res.data?.records ?? [];
    total.value = res.data?.total ?? 0;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  queryParams.page = 1;
  fetchData();
}

async function handleConfirm(row: any) {
  await ElMessageBox.confirm("确认该预约？", "提示", { type: "info" });
  await confirmReservation(row.id);
  ElMessage.success("已确认");
  fetchData();
}

async function handleComplete(row: any) {
  await ElMessageBox.confirm("确认完成该预约？", "提示", { type: "info" });
  await completeReservation(row.id);
  ElMessage.success("已完成");
  fetchData();
}

function handleSizeChange(val: number) {
  queryParams.size = val;
  fetchData();
}

function handleCurrentChange(val: number) {
  queryParams.page = val;
  fetchData();
}

onMounted(() => {
  loadFarmhouses();
  loadPackages();
  loadUsers();
  fetchData();
});
</script>

<template>
  <div class="main">
    <el-card shadow="never">
      <div class="mb-4 flex items-center gap-2">
        <el-select
          v-model="queryParams.status"
          placeholder="预约状态"
          clearable
          style="width: 140px"
          @change="handleSearch"
        >
          <el-option
            v-for="s in statusOptions"
            :key="s.value"
            :label="s.label"
            :value="s.value"
          />
        </el-select>
        <el-select
          v-model="queryParams.farmhouseId"
          placeholder="选择农家乐"
          clearable
          style="width: 200px"
          @change="handleSearch"
        >
          <el-option
            v-for="f in farmhouseOptions"
            :key="f.id"
            :label="f.name"
            :value="f.id"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="reservationNo" label="预约编号" width="160" />
        <el-table-column label="用户" width="100">
          <template #default="{ row }">{{ getUserName(row.userId) }}</template>
        </el-table-column>
        <el-table-column label="农家乐" min-width="120">
          <template #default="{ row }">{{ getFarmhouseName(row.farmhouseId) }}</template>
        </el-table-column>
        <el-table-column label="套餐" min-width="100">
          <template #default="{ row }">{{ getPackageName(row.packageId) }}</template>
        </el-table-column>
        <el-table-column prop="reserveDate" label="日期" width="110" />
        <el-table-column prop="personCount" label="人数" width="70" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              size="small"
              type="primary"
              link
              @click="handleConfirm(row)"
            >
              确认
            </el-button>
            <el-button
              v-if="row.status === 1"
              size="small"
              type="success"
              link
              @click="handleComplete(row)"
            >
              完成
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.main {
  margin: 16px;
}
</style>
