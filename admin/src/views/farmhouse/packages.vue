<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getPackageList,
  createPackage,
  updatePackage,
  deletePackage
} from "@/api/package";
import { getFarmhouseList } from "@/api/farmhouse";

defineOptions({ name: "PackageList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const farmhouseOptions = ref<any[]>([]);
const queryParams = reactive({
  page: 1,
  size: 10,
  farmhouseId: null as number | null,
  type: null as number | null
});
const dialogVisible = ref(false);
const dialogTitle = ref("新增套餐");
const formRef = ref();
const form = reactive({
  id: null as number | null,
  farmhouseId: null as number | null,
  name: "",
  description: "",
  price: 0,
  originalPrice: 0,
  type: 1,
  capacity: 1,
  coverImage: "",
  status: 1
});

const typeOptions = [
  { label: "住宿", value: 1 },
  { label: "餐饮", value: 2 },
  { label: "活动体验", value: 3 },
  { label: "综合套餐", value: 4 }
];

const rules = {
  farmhouseId: [{ required: true, message: "请选择农家乐", trigger: "change" }],
  name: [{ required: true, message: "请输入套餐名", trigger: "blur" }],
  price: [{ required: true, message: "请输入价格", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }]
};

async function loadFarmhouses() {
  const res: any = await getFarmhouseList({ page: 1, size: 999 });
  farmhouseOptions.value = res.data?.records ?? [];
}

function getFarmhouseName(farmhouseId: number) {
  return farmhouseOptions.value.find((f: any) => f.id === farmhouseId)?.name ?? "-";
}

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getPackageList(queryParams);
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

function resetForm() {
  form.id = null;
  form.farmhouseId = null;
  form.name = "";
  form.description = "";
  form.price = 0;
  form.originalPrice = 0;
  form.type = 1;
  form.capacity = 1;
  form.coverImage = "";
  form.status = 1;
}

function handleAdd() {
  resetForm();
  dialogTitle.value = "新增套餐";
  dialogVisible.value = true;
}

function handleEdit(row: any) {
  resetForm();
  Object.assign(form, row);
  dialogTitle.value = "编辑套餐";
  dialogVisible.value = true;
}

async function handleSubmit() {
  await formRef.value?.validate();
  const data = { ...form };
  if (form.id) {
    await updatePackage(form.id, data);
    ElMessage.success("更新成功");
  } else {
    await createPackage(data);
    ElMessage.success("创建成功");
  }
  dialogVisible.value = false;
  fetchData();
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm("确认删除该套餐？", "提示", { type: "warning" });
  await deletePackage(row.id);
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

function getTypeName(type: number) {
  return typeOptions.find(t => t.value === type)?.label ?? "未知";
}

onMounted(() => {
  loadFarmhouses();
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
            @change="handleSearch"
          >
            <el-option
              v-for="f in farmhouseOptions"
              :key="f.id"
              :label="f.name"
              :value="f.id"
            />
          </el-select>
          <el-select
            v-model="queryParams.type"
            placeholder="套餐类型"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option
              v-for="t in typeOptions"
              :key="t.value"
              :label="t.label"
              :value="t.value"
            />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="primary" @click="handleAdd">新增套餐</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="农家乐" min-width="120">
          <template #default="{ row }">{{ getFarmhouseName(row.farmhouseId) }}</template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">¥{{ row.originalPrice }}</template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量" width="70" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
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
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width: 100%">
            <el-option
              v-for="t in typeOptions"
              :key="t.value"
              :label="t.label"
              :value="t.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="容量">
          <el-input-number v-model="form.capacity" :min="1" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="图片URL" />
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
