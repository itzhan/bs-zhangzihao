<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getAllSettings, batchUpdateSettings } from "@/api/settings";

defineOptions({ name: "SystemSettings" });

const loading = ref(false);
const saving = ref(false);
const settings = ref<{ key: string; value: string; description: string }[]>([]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getAllSettings();
    // data 可能是数组或对象
    if (Array.isArray(res.data)) {
      settings.value = res.data;
    } else if (res.data && typeof res.data === "object") {
      settings.value = Object.entries(res.data).map(([key, val]: any) => ({
        key,
        value: typeof val === "object" ? val.value ?? "" : String(val),
        description: typeof val === "object" ? val.description ?? key : key
      }));
    }
  } finally {
    loading.value = false;
  }
}

async function handleSave() {
  saving.value = true;
  try {
    const data: Record<string, string> = {};
    settings.value.forEach(s => {
      data[s.key] = s.value;
    });
    await batchUpdateSettings(data);
    ElMessage.success("保存成功");
  } finally {
    saving.value = false;
  }
}

onMounted(() => fetchData());
</script>

<template>
  <div class="main">
    <el-card shadow="never" v-loading="loading">
      <template #header>
        <div class="flex items-center justify-between">
          <span class="text-base font-bold">系统设置</span>
          <el-button type="primary" :loading="saving" @click="handleSave">保存设置</el-button>
        </div>
      </template>

      <el-form label-width="200px" v-if="settings.length > 0">
        <el-form-item
          v-for="item in settings"
          :key="item.key"
          :label="item.description || item.key"
        >
          <el-input v-model="item.value" style="max-width: 400px" />
        </el-form-item>
      </el-form>

      <el-empty v-else description="暂无配置项" />
    </el-card>
  </div>
</template>

<style scoped>
.main {
  margin: 16px;
}
</style>
