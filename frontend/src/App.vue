<template>
  <n-config-provider :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <router-view />
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { NConfigProvider, NMessageProvider, NDialogProvider, NNotificationProvider } from 'naive-ui'

const themeOverrides = {
  common: {
    primaryColor: '#10b981', // 更清新现代的绿色 (Emerald 500)
    primaryColorHover: '#34d399',
    primaryColorPressed: '#059669',
    primaryColorSuppl: '#34d399',
    borderRadius: '8px', // 全局圆角
    fontFamily: '"Inter", "Roboto", -apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Arial, sans-serif'
  },
  Button: {
    borderRadiusMedium: '8px',
    fontWeight: '500' // 按钮字体加粗一点
  },
  Card: {
    borderRadius: '16px', // 卡片圆角更大
    boxShadow: '0 4px 20px 0 rgba(0, 0, 0, 0.03)' // 极柔和的阴影
  },
  Input: {
    borderRadius: '8px'
  }
}
</script>

<style>
/* 引入 Inter 字体 (如果项目中没有本地字体，这里只是备选方案，主要依赖系统字体) */
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f8fafc; /* 更干净的背景色 (Slate 50) */
  color: #1e293b; /* Slate 800 */
}

#app {
  width: 100%;
  height: 100vh;
}

/* 滚动条美化 (Mac OS风格) */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
::-webkit-scrollbar-track {
  background: transparent;
}
</style>
