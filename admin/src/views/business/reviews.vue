<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getReviewList,
  approveReview,
  rejectReview,
  replyReview
} from "@/api/review";
import { getFarmhouseList } from "@/api/farmhouse";
import { getUserList } from "@/api/admin-user";

defineOptions({ name: "ReviewList" });

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
  { label: "待审核", value: 0 },
  { label: "已通过", value: 1 },
  { label: "已拒绝", value: 2 }
];

function getStatusType(status: number) {
  const map: Record<number, string> = { 0: "warning", 1: "success", 2: "danger" };
  return map[status] ?? "info";
}

function getStatusName(status: number) {
  return statusOptions.find(s => s.value === status)?.label ?? "未知";
}

const replyDialogVisible = ref(false);
const replyForm = reactive({ id: 0, content: "" });

async function loadFarmhouses() {
  const res: any = await getFarmhouseList({ page: 1, size: 999 });
  farmhouseOptions.value = res.data?.records ?? [];
}
async function loadUsers() {
  const res: any = await getUserList({ page: 1, size: 999 });
  userOptions.value = res.data?.records ?? [];
}

function getFarmhouseName(id: number) {
  return farmhouseOptions.value.find((f: any) => f.id === id)?.name ?? "-";
}
function getUserName(id: number) {
  return userOptions.value.find((u: any) => u.id === id)?.username ?? "-";
}

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getReviewList(queryParams);
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

async function handleApprove(row: any) {
  await ElMessageBox.confirm("通过该评价？", "提示", { type: "info" });
  await approveReview(row.id);
  ElMessage.success("已通过");
  fetchData();
}

async function handleReject(row: any) {
  await ElMessageBox.confirm("拒绝该评价？", "提示", { type: "warning" });
  await rejectReview(row.id);
  ElMessage.success("已拒绝");
  fetchData();
}

function showReplyDialog(row: any) {
  replyForm.id = row.id;
  replyForm.content = "";
  replyDialogVisible.value = true;
}

async function handleReply() {
  if (!replyForm.content.trim()) {
    ElMessage.warning("请输入回复内容");
    return;
  }
  await replyReview(replyForm.id, { reply: replyForm.content });
  ElMessage.success("回复成功");
  replyDialogVisible.value = false;
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
          placeholder="评价状态"
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
        <el-table-column label="用户" width="100">
          <template #default="{ row }">{{ getUserName(row.userId) }}</template>
        </el-table-column>
        <el-table-column label="农家乐" min-width="120">
          <template #default="{ row }">{{ getFarmhouseName(row.farmhouseId) }}</template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              size="small"
              type="success"
              link
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 0"
              size="small"
              type="danger"
              link
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button
              size="small"
              type="primary"
              link
              @click="showReplyDialog(row)"
            >
              回复
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

    <el-dialog v-model="replyDialogVisible" title="回复评价" width="500px">
      <el-input
        v-model="replyForm.content"
        type="textarea"
        :rows="4"
        placeholder="请输入回复内容"
      />
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReply">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.main {
  margin: 16px;
}
</style>
