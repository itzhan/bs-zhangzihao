<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getFarmhouseList,
  createFarmhouse,
  updateFarmhouse,
  deleteFarmhouse,
  toggleFarmhouseStatus
} from "@/api/farmhouse";

defineOptions({ name: "FarmhouseList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({ page: 1, size: 10, keyword: "" });
const dialogVisible = ref(false);
const dialogTitle = ref("新增农家乐");
const formRef = ref();
const form = reactive({
  id: null as number | null,
  name: "",
  description: "",
  shortDesc: "",
  address: "",
  phone: "",
  coverImage: "",
  ownerName: "",
  tags: "",
  features: "",
  businessHours: "",
  sortOrder: 0
});

const rules = {
  name: [{ required: true, message: "请输入名称", trigger: "blur" }],
  address: [{ required: true, message: "请输入地址", trigger: "blur" }],
  phone: [{ required: true, message: "请输入电话", trigger: "blur" }]
};

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getFarmhouseList(queryParams);
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
  form.name = "";
  form.description = "";
  form.shortDesc = "";
  form.address = "";
  form.phone = "";
  form.coverImage = "";
  form.ownerName = "";
  form.tags = "";
  form.features = "";
  form.businessHours = "";
  form.sortOrder = 0;
}

function handleAdd() {
  resetForm();
  dialogTitle.value = "新增农家乐";
  dialogVisible.value = true;
}

function handleEdit(row: any) {
  resetForm();
  Object.assign(form, row);
  dialogTitle.value = "编辑农家乐";
  dialogVisible.value = true;
}

async function handleSubmit() {
  await formRef.value?.validate();
  const data = { ...form };
  if (form.id) {
    await updateFarmhouse(form.id, data);
    ElMessage.success("更新成功");
  } else {
    await createFarmhouse(data);
    ElMessage.success("创建成功");
  }
  dialogVisible.value = false;
  fetchData();
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm("确认删除该农家乐？", "提示", { type: "warning" });
  await deleteFarmhouse(row.id);
  ElMessage.success("删除成功");
  fetchData();
}

async function handleToggleStatus(row: any) {
  await toggleFarmhouseStatus(row.id);
  ElMessage.success("状态已更新");
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
      <div class="mb-4 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索农家乐名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleSearch"
          />
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="primary" @click="handleAdd">新增农家乐</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" min-width="120" />
        <el-table-column prop="address" label="地址" min-width="160" />
        <el-table-column prop="rating" label="评分" width="80">
          <template #default="{ row }">
            {{ row.rating != null ? Number(row.rating).toFixed(1) : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="reviewCount" label="评价数" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "上架" : "下架" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? "下架" : "上架" }}
            </el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="简介" prop="shortDesc">
          <el-input v-model="form.shortDesc" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <el-input v-model="form.coverImage" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="负责人" prop="ownerName">
          <el-input v-model="form.ownerName" />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-input v-model="form.tags" placeholder="多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="特色" prop="features">
          <el-input v-model="form.features" placeholder="多个用逗号分隔" />
        </el-form-item>
        <el-form-item label="营业时间" prop="businessHours">
          <el-time-picker
            v-model="form.businessHours"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="HH:mm"
            value-format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
