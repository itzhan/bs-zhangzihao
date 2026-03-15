<script setup lang="ts">
import { useRouter } from "vue-router";
import { message } from "@/utils/message";
import { useUserStoreHook } from "@/store/modules/user";
import { initRouter, getTopMenu } from "@/router/utils";
import { useEventListener } from "@vueuse/core";
import type { FormInstance } from "element-plus";
import { ref, reactive } from "vue";
import { debounce } from "@pureadmin/utils";

import Lock from "~icons/ri/lock-fill";
import User from "~icons/ri/user-3-fill";
import IconLeaf from "~icons/lucide/leaf";
import IconMapPin from "~icons/lucide/map-pin";
import IconStar from "~icons/lucide/star";
import IconShield from "~icons/lucide/shield-check";
import { useRenderIcon } from "@/components/ReIcon/src/hooks";

defineOptions({
  name: "Login"
});

const router = useRouter();
const loading = ref(false);
const disabled = ref(false);
const ruleFormRef = ref<FormInstance>();

const ruleForm = reactive({
  username: "admin",
  password: "admin123"
});

const loginRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" }
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" }
  ]
};

const features = [
  { icon: IconMapPin, text: "覆盖河南10+优质农家乐" },
  { icon: IconStar, text: "在线预约 · 轻松管理" },
  { icon: IconShield, text: "安全可靠 · 数据保障" }
];

const onLogin = async (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  await formEl.validate(valid => {
    if (valid) {
      loading.value = true;
      useUserStoreHook()
        .loginByUsername({
          username: ruleForm.username,
          password: ruleForm.password
        })
        .then(async () => {
          await initRouter();
          disabled.value = true;
          router.push(getTopMenu(true).path).then(() => {
            message("登录成功", { type: "success" });
          });
        })
        .catch(_err => {
          message("登录失败，请检查用户名或密码", { type: "error" });
        })
        .finally(() => {
          disabled.value = false;
          loading.value = false;
        });
    }
  });
};

const immediateDebounce: any = debounce(
  formRef => onLogin(formRef),
  1000,
  true
);

useEventListener(document, "keydown", ({ code }) => {
  if (
    ["Enter", "NumpadEnter"].includes(code) &&
    !disabled.value &&
    !loading.value
  )
    immediateDebounce(ruleFormRef.value);
});
</script>

<template>
  <div class="farmstay-login">
    <!-- 全屏背景 -->
    <div class="farmstay-login__bg"></div>
    <!-- 半透明遮罩 -->
    <div class="farmstay-login__overlay"></div>

    <!-- 主内容：左右布局 -->
    <div class="farmstay-login__main">
      <!-- 左侧品牌区 -->
      <div class="farmstay-login__brand">
        <div class="brand-content">
          <div class="brand-logo">
            <component :is="IconLeaf" class="brand-logo-icon" />
          </div>
          <h1 class="brand-title">农家乐预约管理系统</h1>
          <p class="brand-subtitle">发现河南最美农家乐 · 享受田园慢生活</p>
          <div class="brand-features">
            <div v-for="(f, i) in features" :key="i" class="brand-feature">
              <component :is="f.icon" class="brand-feature-icon" />
              <span>{{ f.text }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录卡片 -->
      <div class="farmstay-login__card">
        <div class="farmstay-login__header">
          <h2 class="farmstay-login__title">管理后台</h2>
          <p class="farmstay-login__desc">请输入您的管理员账号登录系统</p>
        </div>

        <!-- 分割线 -->
        <div class="farmstay-login__divider"></div>

        <!-- 表单 -->
        <el-form
          ref="ruleFormRef"
          :model="ruleForm"
          :rules="loginRules"
          size="large"
          class="farmstay-login__form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="ruleForm.username"
              clearable
              placeholder="请输入用户名"
              :prefix-icon="useRenderIcon(User)"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="ruleForm.password"
              clearable
              show-password
              placeholder="请输入密码"
              :prefix-icon="useRenderIcon(Lock)"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              class="farmstay-login__btn"
              type="primary"
              size="large"
              :loading="loading"
              :disabled="disabled"
              @click="onLogin(ruleFormRef)"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <p class="farmstay-login__footer">
          © 2026 农家乐线上预约管理系统
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.farmstay-login {
  position: relative;
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}

/* 全屏背景图 */
.farmstay-login__bg {
  position: absolute;
  inset: 0;
  background: url("@/assets/login/farmstay_bg.png") center/cover no-repeat;
  z-index: 0;
  animation: bgZoom 30s ease-in-out infinite alternate;
}

@keyframes bgZoom {
  0% { transform: scale(1); }
  100% { transform: scale(1.08); }
}

/* 半透明遮罩 */
.farmstay-login__overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(0, 0, 0, 0.5) 0%,
    rgba(0, 0, 0, 0.2) 40%,
    rgba(0, 0, 0, 0.45) 100%
  );
  z-index: 1;
}

/* 主内容 */
.farmstay-login__main {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: stretch;
  max-width: 880px;
  width: 90vw;
  min-height: 520px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.3);
  animation: cardIn 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
}

@keyframes cardIn {
  0% {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 左侧品牌区 */
.farmstay-login__brand {
  width: 400px;
  flex-shrink: 0;
  background: linear-gradient(160deg, #1a3a0a 0%, #2d5016 40%, #4a8c2a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
  position: relative;
  overflow: hidden;
}

.farmstay-login__brand::before {
  content: '';
  position: absolute;
  top: -60px;
  right: -60px;
  width: 220px;
  height: 220px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 50%;
}

.farmstay-login__brand::after {
  content: '';
  position: absolute;
  bottom: -40px;
  left: -40px;
  width: 160px;
  height: 160px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 50%;
}

.brand-content {
  position: relative;
  z-index: 1;
  color: #fff;
}

.brand-logo {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.brand-logo-icon {
  width: 32px;
  height: 32px;
  color: #90ee90;
}

.brand-title {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 8px;
  line-height: 1.3;
}

.brand-subtitle {
  font-size: 14px;
  margin: 0 0 36px;
  opacity: 0.7;
  line-height: 1.5;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.brand-feature {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  opacity: 0.85;
}

.brand-feature-icon {
  width: 18px;
  height: 18px;
  opacity: 0.9;
  flex-shrink: 0;
}

/* 右侧登录卡片 */
.farmstay-login__card {
  flex: 1;
  padding: 48px 40px 36px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(24px);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 标题 */
.farmstay-login__header {
  margin-bottom: 8px;
}

.farmstay-login__title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 8px;
}

.farmstay-login__desc {
  font-size: 14px;
  color: #888;
  margin: 0;
}

/* 分割线 */
.farmstay-login__divider {
  height: 2px;
  margin: 24px 0 28px;
  background: linear-gradient(
    90deg,
    #2d5016 0%,
    #4a8c2a 30%,
    transparent 100%
  );
  opacity: 0.2;
  border-radius: 1px;
}

/* 表单 */
.farmstay-login__form {
  width: 100%;
}

.farmstay-login__form :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 4px 16px;
  background: rgba(245, 247, 240, 0.8);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04) !important;
  border: 1px solid rgba(45, 80, 22, 0.12);
  transition: all 0.3s ease;
}

.farmstay-login__form :deep(.el-input__wrapper:hover) {
  border-color: rgba(45, 80, 22, 0.3);
  box-shadow: 0 4px 12px rgba(45, 80, 22, 0.08) !important;
}

.farmstay-login__form :deep(.el-input__wrapper.is-focus) {
  border-color: #4a8c2a;
  box-shadow: 0 0 0 3px rgba(74, 140, 42, 0.12) !important;
}

.farmstay-login__form :deep(.el-input__prefix .el-icon) {
  color: #4a8c2a;
}

/* 登录按钮 */
.farmstay-login__btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 8px;
  background: linear-gradient(135deg, #2d5016 0%, #4a8c2a 50%, #3d7a1c 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(45, 80, 22, 0.35);
  transition: all 0.3s ease;
}

.farmstay-login__btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 28px rgba(45, 80, 22, 0.45);
  background: linear-gradient(135deg, #3d6a20 0%, #5a9e38 50%, #4a8c2a 100%);
}

.farmstay-login__btn:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(45, 80, 22, 0.3);
}

/* 底部文字 */
.farmstay-login__footer {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: #bbb;
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .farmstay-login__brand {
    display: none;
  }

  .farmstay-login__main {
    max-width: 420px;
  }

  .farmstay-login__card {
    border-radius: 24px;
    padding: 36px 24px 28px;
  }
}
</style>
