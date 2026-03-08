import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import { getToken, setToken, removeToken, getUser, setUser, removeUser, clearAuth } from '@/utils/auth'
import type { User, ApiResult } from '@/types'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(getToken())
  const user = ref<User | null>(getUser() as User | null)

  const isLoggedIn = computed(() => !!token.value)

  async function login(username: string, password: string) {
    const res = await loginApi(username, password) as unknown as ApiResult<{ token: string; user: User }>
    if (res.code === 200) {
      token.value = res.data.token
      user.value = res.data.user
      setToken(res.data.token)
      setUser(res.data.user as unknown as Record<string, unknown>)
      return true
    }
    throw new Error(res.message || '登录失败')
  }

  async function register(data: { username: string; password: string; nickname: string; phone: string }) {
    const res = await registerApi(data) as unknown as ApiResult
    return res.code === 200
  }

  async function fetchUserInfo() {
    const res = await getUserInfoApi() as unknown as ApiResult<User>
    if (res.code === 200) {
      user.value = res.data
      setUser(res.data as unknown as Record<string, unknown>)
    }
  }

  function logout() {
    token.value = null
    user.value = null
    clearAuth()
    router.push('/login')
  }

  return {
    token,
    user,
    isLoggedIn,
    login,
    register,
    fetchUserInfo,
    logout
  }
})
