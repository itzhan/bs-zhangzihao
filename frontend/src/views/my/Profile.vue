<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  NCard, NForm, NFormItem, NInput, NButton, NRadioGroup,
  NRadio, NAvatar, NIcon, NDivider, NUpload, useMessage
} from 'naive-ui'
import type { UploadFileInfo } from 'naive-ui'
import { PersonCircleOutline, CameraOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'
import { upload } from '@/api/file'
import request from '@/utils/request'
import type { ApiResult } from '@/types'

const message = useMessage()
const userStore = useUserStore()

const formData = ref({
  nickname: '',
  phone: '',
  email: '',
  gender: 0,
  avatar: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const saving = ref(false)
const changingPassword = ref(false)

function loadProfile() {
  if (userStore.user) {
    formData.value = {
      nickname: userStore.user.nickname || '',
      phone: userStore.user.phone || '',
      email: userStore.user.email || '',
      gender: userStore.user.gender || 0,
      avatar: userStore.user.avatar || ''
    }
  }
}

async function handleSave() {
  if (!formData.value.nickname) {
    message.warning('请填写昵称')
    return
  }
  saving.value = true
  try {
    const res = await request.put('/api/user/profile', formData.value) as unknown as ApiResult
    if (res.code === 200) {
      message.success('保存成功')
      await userStore.fetchUserInfo()
    } else {
      message.error(res.message || '保存失败')
    }
  } catch {
    message.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function handleChangePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    message.warning('请填写完整的密码信息')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    message.warning('两次密码不一致')
    return
  }
  changingPassword.value = true
  try {
    const res = await request.put('/api/user/password', {
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    }) as unknown as ApiResult
    if (res.code === 200) {
      message.success('密码修改成功')
      passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
    } else {
      message.error(res.message || '修改失败')
    }
  } catch {
    message.error('修改失败')
  } finally {
    changingPassword.value = false
  }
}

async function handleAvatarUpload({ file }: { file: UploadFileInfo }) {
  if (!file.file) return
  try {
    const res = await upload(file.file) as unknown as ApiResult<string>
    if (res.code === 200) {
      formData.value.avatar = res.data
      message.success('头像上传成功')
    }
  } catch {
    message.error('上传失败')
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<template>
  <div class="profile-page">
    <div class="container">
      <h1 class="page-title">个人中心</h1>

      <div class="profile-layout">
        <!-- Profile Card -->
        <NCard title="个人信息" class="profile-card">
          <!-- Avatar -->
          <div class="avatar-section">
            <NUpload
              :show-file-list="false"
              :custom-request="() => {}"
              @change="handleAvatarUpload"
              accept="image/*"
            >
              <div class="avatar-wrapper">
                <NAvatar :size="80" round :src="formData.avatar">
                  <template #fallback>
                    <NIcon :size="40"><PersonCircleOutline /></NIcon>
                  </template>
                </NAvatar>
                <div class="avatar-overlay">
                  <NIcon :size="20" color="#fff"><CameraOutline /></NIcon>
                </div>
              </div>
            </NUpload>
            <span class="avatar-tip">点击更换头像</span>
          </div>

          <NForm label-placement="left" label-width="80" class="profile-form">
            <NFormItem label="昵称">
              <NInput v-model:value="formData.nickname" placeholder="请输入昵称" />
            </NFormItem>
            <NFormItem label="手机号">
              <NInput v-model:value="formData.phone" placeholder="请输入手机号" />
            </NFormItem>
            <NFormItem label="邮箱">
              <NInput v-model:value="formData.email" placeholder="请输入邮箱" />
            </NFormItem>
            <NFormItem label="性别">
              <NRadioGroup v-model:value="formData.gender">
                <NRadio :value="0">保密</NRadio>
                <NRadio :value="1">男</NRadio>
                <NRadio :value="2">女</NRadio>
              </NRadioGroup>
            </NFormItem>
            <NFormItem>
              <NButton type="primary" :loading="saving" @click="handleSave">
                保存修改
              </NButton>
            </NFormItem>
          </NForm>
        </NCard>

        <!-- Password Card -->
        <NCard title="修改密码" class="password-card">
          <NForm label-placement="left" label-width="80" class="password-form">
            <NFormItem label="原密码">
              <NInput
                v-model:value="passwordForm.oldPassword"
                type="password"
                show-password-on="click"
                placeholder="请输入原密码"
              />
            </NFormItem>
            <NFormItem label="新密码">
              <NInput
                v-model:value="passwordForm.newPassword"
                type="password"
                show-password-on="click"
                placeholder="请输入新密码"
              />
            </NFormItem>
            <NFormItem label="确认密码">
              <NInput
                v-model:value="passwordForm.confirmPassword"
                type="password"
                show-password-on="click"
                placeholder="请再次输入新密码"
              />
            </NFormItem>
            <NFormItem>
              <NButton type="primary" :loading="changingPassword" @click="handleChangePassword">
                修改密码
              </NButton>
            </NFormItem>
          </NForm>
        </NCard>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-page {
  padding: 32px 0 60px;
  min-height: calc(100vh - var(--nav-height));
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 24px;
}

.profile-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  align-items: start;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
}

.avatar-tip {
  font-size: 12px;
  color: var(--color-text-light);
  margin-top: 8px;
}

.profile-form,
.password-form {
  max-width: 400px;
}

@media (max-width: 768px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}
</style>
