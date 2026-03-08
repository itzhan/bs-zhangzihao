<script setup lang="ts">
import { NConfigProvider, NMessageProvider, NDialogProvider, NNotificationProvider } from 'naive-ui'
import type { GlobalThemeOverrides } from 'naive-ui'
import NavBar from '@/components/NavBar.vue'
import FooterBar from '@/components/FooterBar.vue'

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: '#5B8C5A',
    primaryColorHover: '#4A7A49',
    primaryColorPressed: '#3D6B3E',
    primaryColorSuppl: '#5B8C5A',
    borderRadius: '8px',
    fontFamily: "system-ui, 'PingFang SC', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif"
  },
  Button: {
    borderRadiusMedium: '8px',
    borderRadiusSmall: '6px'
  },
  Card: {
    borderRadius: '10px'
  },
  Input: {
    borderRadius: '8px'
  }
}
</script>

<template>
  <NConfigProvider :theme-overrides="themeOverrides">
    <NMessageProvider>
      <NDialogProvider>
        <NNotificationProvider>
          <div class="app-layout">
            <NavBar />
            <main class="app-main">
              <router-view v-slot="{ Component }">
                <transition name="fade" mode="out-in">
                  <component :is="Component" />
                </transition>
              </router-view>
            </main>
            <FooterBar />
          </div>
        </NNotificationProvider>
      </NDialogProvider>
    </NMessageProvider>
  </NConfigProvider>
</template>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-main {
  flex: 1;
}
</style>
