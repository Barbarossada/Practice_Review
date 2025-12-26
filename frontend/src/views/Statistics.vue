<template>
  <div class="stats-container">
    <div class="page-header">
      <h2 class="page-title">数据统计</h2>
      <p class="page-subtitle">查看你的练习进度和分析</p>
    </div>

    <n-space vertical :size="24">
      <!-- 核心指标卡片 -->
      <n-grid :x-gap="24" :y-gap="24" cols="1 600:2 1000:4">
        <n-grid-item>
          <n-card :bordered="false" class="stat-card blue-gradient">
            <n-statistic label="总练习数" :value="practiceStore.totalPracticeCount">
              <template #prefix>
                <div class="icon-wrapper">
                  <n-icon :component="SchoolOutline" />
                </div>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>

        <n-grid-item>
          <n-card :bordered="false" class="stat-card green-gradient">
            <n-statistic label="正确数" :value="practiceStore.correctCount">
              <template #prefix>
                <div class="icon-wrapper">
                  <n-icon :component="CheckmarkCircleOutline" />
                </div>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>

        <n-grid-item>
          <n-card :bordered="false" class="stat-card red-gradient">
            <n-statistic label="错误数" :value="practiceStore.wrongCount">
              <template #prefix>
                <div class="icon-wrapper">
                  <n-icon :component="CloseCircleOutline" />
                </div>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>

        <n-grid-item>
          <n-card :bordered="false" class="stat-card orange-gradient">
            <n-statistic label="正确率" :value="practiceStore.correctRate" suffix="%">
              <template #prefix>
                <div class="icon-wrapper">
                  <n-icon :component="StatsChartOutline" />
                </div>
              </template>
            </n-statistic>
          </n-card>
        </n-grid-item>
      </n-grid>

      <!-- 历史记录 -->
      <n-card title="最近练习记录" :bordered="false" class="history-card">
        <template #header-extra>
          <n-button text type="error" @click="clearHistory" size="small">
            <template #icon><n-icon :component="TrashOutline"/></template>
            清空记录
          </n-button>
        </template>

        <n-empty v-if="practiceStore.practiceHistory.length === 0" description="暂无练习记录" class="empty-state">
          <template #icon>
            <n-icon :component="DocumentTextOutline" size="64" color="#e5e7eb" />
          </template>
          <template #extra>
            <n-button type="primary" size="large" @click="router.push('/practice')" class="start-btn">
              开始第一次练习
            </n-button>
          </template>
        </n-empty>

        <n-list v-else hoverable clickable class="custom-list">
          <n-list-item v-for="(record, index) in recentRecords" :key="index" class="list-item">
            <div class="record-content">
              <div class="record-main">
                <div class="record-meta">
                   <n-tag :type="record.isCorrect ? 'success' : 'error'" size="small" round>
                    {{ record.isCorrect ? '正确' : '错误' }}
                  </n-tag>
                  <span class="record-time">{{ formatTime(record.timestamp) }}</span>
                </div>
                <div class="record-question">{{ record.question }}</div>
              </div>
              <div class="record-answer">
                <div class="answer-box user">
                  <span class="label">你的答案</span>
                  <span class="value" :class="{ 'correct': record.isCorrect, 'wrong': !record.isCorrect }">{{ record.userAnswer }}</span>
                </div>
                <div class="answer-box correct" v-if="!record.isCorrect">
                  <span class="label">正确答案</span>
                  <span class="value">{{ record.correctAnswer }}</span>
                </div>
              </div>
            </div>
          </n-list-item>
        </n-list>
      </n-card>
    </n-space>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  NSpace, NCard, NGrid, NGridItem, NStatistic, NIcon, NList, NListItem,
  NText, NTag, NButton, NEmpty, useDialog, useMessage
} from 'naive-ui'
import {
  SchoolOutline, CheckmarkCircleOutline, CloseCircleOutline,
  StatsChartOutline, DocumentTextOutline, TrashOutline
} from '@vicons/ionicons5'
import { usePracticeStore } from '@/stores/practice'

const router = useRouter()
const dialog = useDialog()
const message = useMessage()
const practiceStore = usePracticeStore()

// 最近20条记录
const recentRecords = computed(() => {
  return practiceStore.practiceHistory.slice(-20).reverse()
})

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 清空记录
const clearHistory = () => {
  dialog.warning({
    title: '确认清空',
    content: '确定要清空所有练习记录吗？此操作不可恢复！',
    positiveText: '确定清空',
    negativeText: '取消',
    onPositiveClick: () => {
      practiceStore.clearHistory()
      message.success('已清空练习记录')
    }
  })
}
</script>

<style scoped>
.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 40px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.page-subtitle {
  color: #6b7280;
  font-size: 16px;
}

.stat-card {
  border-radius: 16px;
  border: none;
  transition: transform 0.2s, box-shadow 0.2s;
  overflow: hidden;
  position: relative;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.08);
}

/* 渐变背景 */
.blue-gradient { background: linear-gradient(135deg, #e0f2fe 0%, #ffffff 100%); }
.green-gradient { background: linear-gradient(135deg, #dcfce7 0%, #ffffff 100%); }
.red-gradient { background: linear-gradient(135deg, #fee2e2 0%, #ffffff 100%); }
.orange-gradient { background: linear-gradient(135deg, #ffedd5 0%, #ffffff 100%); }

.icon-wrapper {
  background: rgba(255, 255, 255, 0.6);
  padding: 8px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.history-card {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}

.start-btn {
  font-weight: 600;
  padding: 0 32px;
}

.custom-list {
  background: transparent;
}

.list-item {
  padding: 16px 20px !important;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.2s;
}

.list-item:hover {
  background-color: #f9fafb;
}

.record-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.record-main {
  flex: 1;
  min-width: 300px;
}

.record-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.record-time {
  color: #9ca3af;
  font-size: 13px;
}

.record-question {
  font-size: 16px;
  color: #374151;
  font-weight: 500;
  line-height: 1.5;
}

.record-answer {
  display: flex;
  gap: 24px;
}

.answer-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.answer-box .label {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 2px;
}

.answer-box .value {
  font-weight: 700;
  font-size: 16px;
  color: #4b5563;
}

.answer-box .value.correct { color: #10b981; }
.answer-box .value.wrong { color: #ef4444; }
.answer-box.correct .value { color: #10b981; }

</style>
