<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getAnnouncementList,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement
} from "@/api/announcement";

defineOptions({ name: "AnnouncementList" });

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const queryParams = reactive({
  page: 1,
  size: 10,
  type: null as number | null,
  status: null as number | null
});
const dialogVisible = ref(false);
const dialogTitle = ref("新增公告");
const formRef = ref();
const form = reactive({
  id: null as number | null,
  title: "",
  content: "",
  type: 1,
  status: 1,
  sortOrder: 0
});

const typeOptions = [
  { label: "公告", value: 1 },
  { label: "活动", value: 2 }
];

const rules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入内容", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }]
};

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getAnnouncementList(queryParams);
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
  form.title = "";
  form.content = "";
  form.type = 1;
  form.status = 1;
  form.sortOrder = 0;
}

function handleAdd() {
  resetForm();
  dialogTitle.value = "新增公告";
  dialogVisible.value = true;
}

function handleEdit(row: any) {
  resetForm();
  Object.assign(form, row);
  dialogTitle.value = "编辑公告";
  dialogVisible.value = true;
}

async function handleSubmit() {
  await formRef.value?.validate();
  const data = { ...form };
  if (form.id) {
    await updateAnnouncement(form.id, data);
    ElMessage.success("更新成功");
  } else {
    await createAnnouncement(data);
    ElMessage.success("创建成功");
  }
  dialogVisible.value = false;
  fetchData();
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm("确认删除该公告？", "提示", { type: "warning" });
  await deleteAnnouncement(row.id);
  ElMessage.success("删除成功");
  fetchData();
}

function getTypeName(type: number) {
  return typeOptions.find(t => t.value === type)?.label ?? "未知";
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
          <el-select
            v-model="queryParams.type"
            placeholder="类型"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option
              v-for="t in typeOptions"
              :key="t.value"
              :label="t.label"
              :value="t.value"
            />
          </el-select>
          <el-select
            v-model="queryParams.status"
            placeholder="状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="primary" @click="handleAdd">新增公告</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="type" label="类型" width="90">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" />
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
        <el-form-item label="状态">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
        <el-form-item label="排序">
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
