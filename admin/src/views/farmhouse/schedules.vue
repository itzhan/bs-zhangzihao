<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getScheduleList,
  createSchedule,
  updateSchedule,
  deleteSchedule
} from "@/api/schedule";
import { getFarmhouseList } from "@/api/farmhouse";
import { getPackageList } from "@/api/package";

defineOptions({ name: "ScheduleList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const farmhouseOptions = ref<any[]>([]);
const packageOptions = ref<any[]>([]);
const queryParams = reactive({
  page: 1,
  size: 10,
  farmhouseId: null as number | null,
  packageId: null as number | null
});
const dialogVisible = ref(false);
const dialogTitle = ref("新增档期");
const formRef = ref();
const form = reactive({
  id: null as number | null,
  packageId: null as number | null,
  farmhouseId: null as number | null,
  scheduleDate: "",
  totalQuota: 10,
  remainingQuota: 10,
  priceOverride: null as number | null,
  status: 1
});

const rules = {
  packageId: [{ required: true, message: "请选择套餐", trigger: "change" }],
  farmhouseId: [{ required: true, message: "请选择农家乐", trigger: "change" }],
  scheduleDate: [{ required: true, message: "请选择日期", trigger: "change" }],
  totalQuota: [{ required: true, message: "请输入总名额", trigger: "blur" }]
};

function getPackageName(packageId: number) {
  return packageOptions.value.find((p: any) => p.id === packageId)?.name ?? "-";
}

function getFarmhouseName(farmhouseId: number) {
  return farmhouseOptions.value.find((f: any) => f.id === farmhouseId)?.name ?? "-";
}

async function loadFarmhouses() {
  const res: any = await getFarmhouseList({ page: 1, size: 999 });
  farmhouseOptions.value = res.data?.records ?? [];
}

async function loadPackages() {
  const params: any = { page: 1, size: 999 };
  if (queryParams.farmhouseId) params.farmhouseId = queryParams.farmhouseId;
  const res: any = await getPackageList(params);
  packageOptions.value = res.data?.records ?? [];
}

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getScheduleList(queryParams);
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

function handleFarmhouseChange() {
  loadPackages();
  handleSearch();
}

function resetForm() {
  form.id = null;
  form.packageId = null;
  form.farmhouseId = null;
  form.scheduleDate = "";
  form.totalQuota = 10;
  form.remainingQuota = 10;
  form.priceOverride = null;
  form.status = 1;
}

function handleAdd() {
  resetForm();
  dialogTitle.value = "新增档期";
  dialogVisible.value = true;
}

function handleEdit(row: any) {
  resetForm();
  Object.assign(form, row);
  dialogTitle.value = "编辑档期";
  dialogVisible.value = true;
}

async function handleSubmit() {
  await formRef.value?.validate();
  const data = { ...form };
  if (form.id) {
    await updateSchedule(form.id, data);
    ElMessage.success("更新成功");
  } else {
    await createSchedule(data);
    ElMessage.success("创建成功");
  }
  dialogVisible.value = false;
  fetchData();
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm("确认删除该档期？", "提示", { type: "warning" });
  await deleteSchedule(row.id);
  ElMessage.success("删除成功");
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
  fetchData();
});
</script>

<template>
  <div class="main">
    <el-card shadow="never">
      <div class="mb-4 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <el-select
            v-model="queryParams.farmhouseId"
            placeholder="选择农家乐"
            clearable
            style="width: 200px"
            @change="handleFarmhouseChange"
          >
            <el-option
              v-for="f in farmhouseOptions"
              :key="f.id"
              :label="f.name"
              :value="f.id"
            />
          </el-select>
          <el-select
            v-model="queryParams.packageId"
            placeholder="选择套餐"
            clearable
            style="width: 200px"
            @change="handleSearch"
          >
            <el-option
              v-for="p in packageOptions"
              :key="p.id"
              :label="p.name"
              :value="p.id"
            />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="primary" @click="handleAdd">新增档期</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="农家乐" min-width="120">
          <template #default="{ row }">
            {{ getFarmhouseName(row.farmhouseId) }}
          </template>
        </el-table-column>
        <el-table-column label="套餐" min-width="120">
          <template #default="{ row }">
            {{ getPackageName(row.packageId) }}
          </template>
        </el-table-column>
        <el-table-column prop="scheduleDate" label="日期" width="120" />
        <el-table-column prop="totalQuota" label="总名额" width="80" />
        <el-table-column prop="remainingQuota" label="剩余名额" width="90" />
        <el-table-column label="特价" width="100">
          <template #default="{ row }">
            {{ row.priceOverride != null ? `¥${row.priceOverride}` : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "可用" : "不可用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="农家乐" prop="farmhouseId">
          <el-select v-model="form.farmhouseId" placeholder="选择农家乐" style="width: 100%">
            <el-option
              v-for="f in farmhouseOptions"
              :key="f.id"
              :label="f.name"
              :value="f.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="套餐" prop="packageId">
          <el-select v-model="form.packageId" placeholder="选择套餐" style="width: 100%">
            <el-option
              v-for="p in packageOptions"
              :key="p.id"
              :label="p.name"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="scheduleDate">
          <el-date-picker
            v-model="form.scheduleDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="总名额" prop="totalQuota">
          <el-input-number v-model="form.totalQuota" :min="1" />
        </el-form-item>
        <el-form-item label="剩余名额">
          <el-input-number v-model="form.remainingQuota" :min="0" />
        </el-form-item>
        <el-form-item label="特价">
          <el-input-number v-model="form.priceOverride" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.main {
  margin: 16px;
}
</style>
