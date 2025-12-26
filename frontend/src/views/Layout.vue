<template>
  <n-layout has-sider style="height: 100vh" class="main-layout">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
      class="custom-sider"
    >
      <div class="logo-container" :class="{ collapsed: collapsed }">
        <div class="logo-icon-bg">
          <n-icon :component="SchoolOutline" size="24" color="#fff" />
        </div>
        <transition name="fade">
          <span v-if="!collapsed" class="logo-text">复习题库</span>
        </transition>
      </div>
      
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuSelect"
        class="custom-menu"
      />
    </n-layout-sider>

    <n-layout>
      <n-layout-header class="custom-header">
        <n-space align="center" justify="space-between" style="width: 100%">
          <n-space align="center" :size="16">
            <n-text class="page-title">
              {{ currentRouteName }}
            </n-text>
            <n-tag v-if="currentRouteName === '在线练习'" type="primary" size="small" round bordered>
              <template #icon><n-icon :component="CheckmarkCircleOutline" /></template>
              专注模式
            </n-tag>
          </n-space>
          <n-space align="center" :size="24">
            <div class="user-profile">
               <n-avatar round size="small" color="#10b981" style="color: white; font-weight: bold;">学</n-avatar>
               <span class="username">同学</span>
            </div>
          </n-space>
        </n-space>
      </n-layout-header>

      <n-layout-content
        content-style="padding: 24px; min-height: 100%;"
        :native-scrollbar="false"
        class="main-content"
      >
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup>
import { ref, h, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NLayout, NLayoutSider, NLayoutHeader, NLayoutContent, NMenu, NSpace, NText, NTag, NIcon, NAvatar } from 'naive-ui'
import {
  HomeOutline,
  ListOutline,
  SchoolOutline,
  BookmarkOutline,
  StatsChartOutline,
  CheckmarkCircleOutline,
  SwapHorizontalOutline
} from '@vicons/ionicons5'

const router = useRouter()
const route = useRoute()
const collapsed = ref(false)

const activeKey = computed(() => route.path)

// 根据路由显示当前页面标题
const currentRouteName = computed(() => {
  const map = {
    '/home': '复习概览',
    '/question-manage': '题目管理',
    '/question-converter': '题库转换',
    '/practice': '在线练习',
    '/wrong-book': '错题回顾',
    '/statistics': '学习统计'
  }
  return map[route.path] || '期末复习系统'
})

const renderIcon = (icon) => () => h(NIcon, null, { default: () => h(icon) })

const menuOptions = [
  { label: '首页概览', key: '/home', icon: renderIcon(HomeOutline) },
  { label: '题目管理', key: '/question-manage', icon: renderIcon(ListOutline) },
  { label: '题库转换', key: '/question-converter', icon: renderIcon(SwapHorizontalOutline) },
  { label: '开始练习', key: '/practice', icon: renderIcon(SchoolOutline) },
  { label: '错题本', key: '/wrong-book', icon: renderIcon(BookmarkOutline) },
  { label: '统计分析', key: '/statistics', icon: renderIcon(StatsChartOutline) }
]

const handleMenuSelect = (key) => router.push(key)
</script>

<style scoped>
.main-layout {
  background-color: #f8fafc;
}

.custom-sider {
  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.02);
  z-index: 20;
}

.logo-container {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  overflow: hidden;
  transition: all 0.3s;
  border-bottom: 1px solid #f1f5f9;
}

.logo-icon-bg {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 6px rgba(16, 185, 129, 0.2);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  white-space: nowrap;
  letter-spacing: 0.5px;
}

.custom-menu {
  padding-top: 12px;
}

.custom-header {
  height: 72px;
  padding: 0 32px;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0,0,0,0.03);
  z-index: 10;
  position: sticky;
  top: 0;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #f1f5f9;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
}

.user-profile:hover {
  background: #e2e8f0;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
