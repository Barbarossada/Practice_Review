<template>
  <n-space vertical :size="16">
    <n-card title="练习统计" :bordered="false">
      <n-grid :x-gap="24" :y-gap="24" :cols="4">
        <n-grid-item>
          <n-statistic label="总练习数" :value="practiceStore.totalPracticeCount">
            <template #prefix>
              <n-icon :component="SchoolOutline" />
            </template>
          </n-statistic>
        </n-grid-item>

        <n-grid-item>
          <n-statistic label="正确数" :value="practiceStore.correctCount">
            <template #prefix>
              <n-icon :component="CheckmarkCircleOutline" color="#18a058" />
            </template>
          </n-statistic>
        </n-grid-item>

        <n-grid-item>
          <n-statistic label="错误数" :value="practiceStore.wrongCount">
            <template #prefix>
              <n-icon :component="CloseCircleOutline" color="#d03050" />
            </template>
          </n-statistic>
        </n-grid-item>

        <n-grid-item>
          <n-statistic label="正确率" :value="practiceStore.correctRate" suffix="%">
            <template #prefix>
              <n-icon :component="StatsChartOutline" />
            </template>
          </n-statistic>
        </n-grid-item>
      </n-grid>
    </n-card>

    <n-card title="最近练习记录" :bordered="false">
      <n-empty v-if="practiceStore.practiceHistory.length === 0" description="暂无练习记录">
        <template #icon>
          <n-icon :component="DocumentTextOutline" size="80" />
        </template>
        <template #extra>
          <n-button type="primary" @click="router.push('/practice')">
            开始练习
          </n-button>
        </template>
      </n-empty>

      <n-list v-else bordered>
        <n-list-item v-for="(record, index) in recentRecords" :key="index">
          <n-space justify="space-between" align="center" style="width: 100%">
            <n-space vertical>
              <n-text>{{ record.question }}</n-text>
              <n-space>
                <n-text depth="3" size="small">
                  {{ formatTime(record.timestamp) }}
                </n-text>
                <n-tag size="small" :type="record.isCorrect ? 'success' : 'error'">
                  {{ record.isCorrect ? '正确' : '错误' }}
                </n-tag>
              </n-space>
            </n-space>

            <n-space>
              <n-text depth="3">你的答案：{{ record.userAnswer }}</n-text>
              <n-text depth="3">正确答案：{{ record.correctAnswer }}</n-text>
            </n-space>
          </n-space>
        </n-list-item>
      </n-list>
    </n-card>

    <n-card :bordered="false">
      <n-space justify="center">
        <n-button @click="clearHistory">
          清空练习记录
        </n-button>
      </n-space>
    </n-card>
  </n-space>
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
  StatsChartOutline, DocumentTextOutline
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
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 清空记录
const clearHistory = () => {
  dialog.warning({
    title: '确认',
    content: '确定要清空所有练习记录吗？此操作不可恢复！',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      practiceStore.clearHistory()
      message.success('已清空练习记录')
    }
  })
}
</script>
