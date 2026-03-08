<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { NCard, NForm, NFormItem, NInput, NButton, NIcon, useMessage } from 'naive-ui'
import { LeafOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()

const formData = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: ''
})
const loading = ref(false)

async function handleRegister() {
  if (!formData.value.username || !formData.value.password) {
    message.warning('请填写用户名和密码')
    return
  }
  if (formData.value.password !== formData.value.confirmPassword) {
    message.warning('两次密码输入不一致')
    return
  }
  if (!formData.value.nickname) {
    message.warning('请填写昵称')
    return
  }
  if (!formData.value.phone) {
    message.warning('请填写手机号')
    return
  }

  loading.value = true
  try {
    const success = await userStore.register({
      username: formData.value.username,
      password: formData.value.password,
      nickname: formData.value.nickname,
      phone: formData.value.phone
    })
    if (success) {
      message.success('注册成功，请登录')
      router.push('/login')
    } else {
      message.error('注册失败，用户名可能已存在')
    }
  } catch {
    message.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-container">
      <NCard class="register-card" :bordered="false">
        <div class="register-header">
          <NIcon :size="40" color="#5B8C5A">
            <LeafOutline />
          </NIcon>
          <h2>创建账号</h2>
          <p>加入农家乐预约平台</p>
        </div>

        <NForm class="register-form">
          <NFormItem label="用户名">
            <NInput
              v-model:value="formData.username"
              placeholder="请输入用户名"
              size="large"
            />
          </NFormItem>
          <NFormItem label="密码">
            <NInput
              v-model:value="formData.password"
              type="password"
              show-password-on="click"
              placeholder="请输入密码"
              size="large"
            />
          </NFormItem>
          <NFormItem label="确认密码">
            <NInput
              v-model:value="formData.confirmPassword"
              type="password"
              show-password-on="click"
              placeholder="请再次输入密码"
              size="large"
            />
          </NFormItem>
          <NFormItem label="昵称">
            <NInput
              v-model:value="formData.nickname"
              placeholder="请输入昵称"
              size="large"
            />
          </NFormItem>
          <NFormItem label="手机号">
            <NInput
              v-model:value="formData.phone"
              placeholder="请输入手机号"
              size="large"
            />
          </NFormItem>
          <NButton
            type="primary"
            block
            size="large"
            :loading="loading"
            @click="handleRegister"
            class="register-btn"
          >
            注册
          </NButton>
        </NForm>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="link">立即登录</router-link>
        </div>
      </NCard>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  min-height: calc(100vh - var(--nav-height) - 200px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(160deg, rgba(91, 140, 90, 0.04) 0%, var(--color-bg-body) 40%, rgba(212, 132, 90, 0.03) 100%);
}

.register-container {
  width: 100%;
  max-width: 420px;
}

.register-card {
  padding: 16px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
}

.register-header {
  text-align: center;
  margin-bottom: 24px;
}

.register-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 12px 0 4px;
}

.register-header p {
  font-size: 14px;
  color: var(--color-text-secondary);
}

.register-form {
  margin-bottom: 16px;
}

.register-btn {
  margin-top: 8px;
  height: 44px;
  font-size: 16px;
}

.register-footer {
  text-align: center;
  font-size: 14px;
  color: var(--color-text-secondary);
}

.register-footer .link {
  color: var(--color-primary);
  font-weight: 500;
  margin-left: 4px;
}

.register-footer .link:hover {
  color: var(--color-primary-hover);
}
</style>
