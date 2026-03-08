<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getOrderList, processRefund, completeOrder } from "@/api/order";
import { getFarmhouseList } from "@/api/farmhouse";
import { getUserList } from "@/api/admin-user";

defineOptions({ name: "OrderList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const farmhouseOptions = ref<any[]>([]);
const userOptions = ref<any[]>([]);
const queryParams = reactive({
  page: 1,
  size: 10,
  status: null as number | null,
  farmhouseId: null as number | null
});

const statusOptions = [
  { label: "待支付", value: 0 },
  { label: "已支付", value: 1 },
  { label: "已取消", value: 2 },
  { label: "退款中", value: 3 },
  { label: "已退款", value: 4 },
  { label: "已完成", value: 5 }
];

function getStatusType(status: number) {
  const map: Record<number, string> = {
    0: "warning",
    1: "primary",
    2: "info",
    3: "danger",
    4: "info",
    5: "success"
  };
  return map[status] ?? "info";
}

function getStatusName(status: number) {
  return statusOptions.find(s => s.value === status)?.label ?? "未知";
}

async function loadFarmhouses() {
  const res: any = await getFarmhouseList({ page: 1, size: 999 });
  farmhouseOptions.value = res.data?.records ?? [];
}
async function loadUsers() {
  const res: any = await getUserList({ page: 1, size: 999 });
  userOptions.value = res.data?.records ?? [];
}

function getUserName(id: number) {
  return userOptions.value.find((u: any) => u.id === id)?.username ?? "-";
}

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getOrderList(queryParams);
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

async function handleRefund(row: any, approved: boolean) {
  const action = approved ? "同意退款" : "拒绝退款";
  await ElMessageBox.confirm(`确认${action}？`, "提示", { type: "warning" });
  await processRefund(row.id, { approved });
  ElMessage.success(`${action}成功`);
  fetchData();
}

async function handleComplete(row: any) {
  await ElMessageBox.confirm("确认完成该订单？", "提示", { type: "info" });
  await completeOrder(row.id);
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
          placeholder="订单状态"
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
        <el-table-column prop="orderNo" label="订单编号" width="170" />
        <el-table-column label="用户" width="100">
          <template #default="{ row }">{{ getUserName(row.userId) }}</template>
        </el-table-column>
        <el-table-column prop="farmhouseName" label="农家乐" min-width="110" />
        <el-table-column prop="packageName" label="套餐" min-width="100" />
        <el-table-column prop="personCount" label="人数" width="70" />
        <el-table-column prop="totalAmount" label="金额" width="90">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 3">
              <el-button size="small" type="success" link @click="handleRefund(row, true)">同意退款</el-button>
              <el-button size="small" type="danger" link @click="handleRefund(row, false)">拒绝退款</el-button>
            </template>
            <el-button
              v-if="row.status === 1"
              size="small"
              type="primary"
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
