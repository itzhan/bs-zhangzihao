<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NCard, NForm, NFormItem, NInput, NButton, NIcon, useMessage } from 'naive-ui'
import { LeafOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const formData = ref({
  username: '',
  password: ''
})
const loading = ref(false)

async function handleLogin() {
  if (!formData.value.username || !formData.value.password) {
    message.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const success = await userStore.login(formData.value.username, formData.value.password)
    if (success) {
      message.success('登录成功')
      const redirect = route.query.redirect as string
      router.push(redirect || '/farmhouses')
    } else {
      message.error('用户名或密码错误')
    }
  } catch (e: any) {
    const msg = e?.response?.data?.message || e?.message || '登录失败，请稍后重试'
    message.error(msg)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <NCard class="login-card" :bordered="false">
        <div class="login-header">
          <NIcon :size="40" color="#5B8C5A">
            <LeafOutline />
          </NIcon>
          <h2>欢迎回来</h2>
          <p>登录农家乐预约平台</p>
        </div>

        <NForm class="login-form">
          <NFormItem label="用户名">
            <NInput
              v-model:value="formData.username"
              placeholder="请输入用户名"
              size="large"
              @keyup.enter="handleLogin"
            />
          </NFormItem>
          <NFormItem label="密码">
            <NInput
              v-model:value="formData.password"
              type="password"
              show-password-on="click"
              placeholder="请输入密码"
              size="large"
              @keyup.enter="handleLogin"
            />
          </NFormItem>
          <NButton
            type="primary"
            block
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登录
          </NButton>
        </NForm>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
      </NCard>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: calc(100vh - var(--nav-height) - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(160deg, rgba(91, 140, 90, 0.04) 0%, var(--color-bg-body) 40%, rgba(212, 132, 90, 0.03) 100%);
}

.login-container {
  width: 100%;
  max-width: 420px;
}

.login-card {
  padding: 16px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 12px 0 4px;
}

.login-header p {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.login-form {
  margin-bottom: 16px;
}

.login-btn {
  margin-top: 8px;
  height: 44px;
  font-size: 16px;
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.login-footer .link {
  color: var(--color-primary);
  font-weight: 500;
  margin-left: 4px;
}

.login-footer .link:hover {
  color: var(--color-primary-hover);
}
</style>
