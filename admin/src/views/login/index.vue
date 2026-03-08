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
    <!-- 半透明遮罩，增加文字可读性 -->
    <div class="farmstay-login__overlay"></div>

    <!-- 登录卡片 -->
    <div class="farmstay-login__card">
      <!-- Logo & 标题 -->
      <div class="farmstay-login__header">
        <div class="farmstay-login__icon">
          <svg viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="50" cy="50" r="48" fill="#2d5016" opacity="0.1"/>
            <path d="M50 15 L20 45 L30 45 L30 75 L70 75 L70 45 L80 45 Z" fill="#2d5016" opacity="0.8"/>
            <rect x="42" y="55" width="16" height="20" rx="2" fill="#8B4513" opacity="0.9"/>
            <rect x="36" y="35" width="10" height="10" rx="1" fill="#87CEEB" opacity="0.7"/>
            <rect x="54" y="35" width="10" height="10" rx="1" fill="#87CEEB" opacity="0.7"/>
            <ellipse cx="25" cy="72" rx="12" ry="8" fill="#228B22" opacity="0.5"/>
            <ellipse cx="75" cy="72" rx="12" ry="8" fill="#228B22" opacity="0.5"/>
            <ellipse cx="20" cy="68" rx="8" ry="6" fill="#32CD32" opacity="0.4"/>
            <ellipse cx="80" cy="68" rx="8" ry="6" fill="#32CD32" opacity="0.4"/>
          </svg>
        </div>
        <h1 class="farmstay-login__title">农家乐预约管理系统</h1>
        <p class="farmstay-login__subtitle">管理后台</p>
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

      <!-- 底部装饰文字 -->
      <p class="farmstay-login__footer">
        发现河南最美农家乐 · 享受田园慢生活
      </p>
    </div>

    <!-- 页脚 -->
    <div class="farmstay-login__copyright">
      © 2026 农家乐线上预约管理系统
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
    rgba(0, 0, 0, 0.35) 0%,
    rgba(0, 0, 0, 0.15) 50%,
    rgba(0, 0, 0, 0.4) 100%
  );
  z-index: 1;
}

/* 登录卡片 - 玻璃拟态 */
.farmstay-login__card {
  position: relative;
  z-index: 2;
  width: 420px;
  padding: 48px 40px 36px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(24px) saturate(1.6);
  -webkit-backdrop-filter: blur(24px) saturate(1.6);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.15),
    0 8px 20px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
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

/* Logo图标 */
.farmstay-login__icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #2d5016 0%, #4a8c2a 100%);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(45, 80, 22, 0.3);
}

.farmstay-login__icon svg {
  width: 44px;
  height: 44px;
}

.farmstay-login__icon svg path,
.farmstay-login__icon svg rect,
.farmstay-login__icon svg ellipse,
.farmstay-login__icon svg circle {
  fill: white;
  opacity: 0.9;
}

/* 标题 */
.farmstay-login__header {
  text-align: center;
  margin-bottom: 8px;
}

.farmstay-login__title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 4px;
  letter-spacing: 2px;
}

.farmstay-login__subtitle {
  font-size: 14px;
  color: #888;
  margin: 0;
  font-weight: 400;
  letter-spacing: 4px;
}

/* 分割线 */
.farmstay-login__divider {
  height: 2px;
  margin: 24px 0 28px;
  background: linear-gradient(
    90deg,
    transparent 0%,
    #2d5016 20%,
    #4a8c2a 50%,
    #2d5016 80%,
    transparent 100%
  );
  opacity: 0.3;
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

/* 底部装饰文字 */
.farmstay-login__footer {
  text-align: center;
  margin-top: 20px;
  font-size: 12px;
  color: #999;
  letter-spacing: 1px;
}

/* 版权 */
.farmstay-login__copyright {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

/* 响应式 */
@media screen and (max-width: 480px) {
  .farmstay-login__card {
    width: 92vw;
    padding: 36px 24px 28px;
    border-radius: 16px;
  }

  .farmstay-login__title {
    font-size: 20px;
  }
}
</style>
