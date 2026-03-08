<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getUserList, toggleUserStatus } from "@/api/admin-user";

defineOptions({ name: "UserList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({ page: 1, size: 10, keyword: "" });

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getUserList(queryParams);
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

async function handleToggleStatus(row: any) {
  const action = row.status === 1 ? "禁用" : "启用";
  await ElMessageBox.confirm(`确认${action}该用户？`, "提示", { type: "warning" });
  await toggleUserStatus(row.id);
  ElMessage.success(`${action}成功`);
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
          v-model="queryParams.keyword"
          placeholder="搜索用户名/昵称/手机"
          clearable
          style="width: 260px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="role" label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'">
              {{ row.role === "ADMIN" ? "管理员" : "用户" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "正常" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? "禁用" : "启用" }}
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
