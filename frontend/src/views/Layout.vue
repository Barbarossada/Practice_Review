<template>
  <n-layout has-sider style="height: 100vh" class="main-layout">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="220"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
      class="custom-sider"
    >
      <div class="logo-container" :class="{ collapsed: collapsed }">
        <n-icon :component="SchoolOutline" size="32" color="#18a058" />
        <span v-if="!collapsed" class="logo-text">复习题库</span>
      </div>
      
      <n-menu
        :collapsed="collapsed"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>

    <n-layout>
      <n-layout-header class="custom-header">
        <n-space align="center" justify="space-between" style="width: 100%">
          <n-space align="center">
            <n-text class="page-title">
              {{ currentRouteName }}
            </n-text>
          </n-space>
          <n-space align="center">
            <n-tag type="success" round size="small">
              <template #icon><n-icon :component="CheckmarkCircleOutline" /></template>
              备考模式
            </n-tag>
            <n-avatar round size="small" style="background-color: #18a058">学</n-avatar>
          </n-space>
        </n-space>
      </n-layout-header>

      <n-layout-content
        content-style="padding: 24px; background-color: #f0f2f5;"
        :native-scrollbar="false"
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
.custom-header {
  height: 64px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2225;
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(20px);
}
</style>
