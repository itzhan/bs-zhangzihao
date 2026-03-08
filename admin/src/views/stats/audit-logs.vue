<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { getAuditLogList } from "@/api/audit-log";

defineOptions({ name: "AuditLogList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({
  page: 1,
  size: 10,
  action: "",
  module: ""
});

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getAuditLogList(queryParams);
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

function handleSizeChange(val: number) {
  queryParams.size = val;
  fetchData();
}

function handleCurrentChange(val: number) {
  queryParams.page = val;
  fetchData();
}

onMounted(() => fetchData());
</script>

<template>
  <div class="main">
    <el-card shadow="never">
      <div class="mb-4 flex items-center gap-2">
        <el-input
          v-model="queryParams.action"
          placeholder="操作类型"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="queryParams.module"
          placeholder="模块"
          clearable
          style="width: 160px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="action" label="操作类型" width="120" />
        <el-table-column prop="module" label="模块" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP" width="140" />
        <el-table-column prop="createTime" label="时间" width="170" />
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
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
