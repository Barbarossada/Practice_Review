<template>
  <div class="home-container">
    <n-card :bordered="false" class="welcome-card">
      <div class="welcome-content">
        <div>
          <h2 class="welcome-title">早安，同学！准备好今天的复习了吗？👋</h2>
          <p class="welcome-desc">距离期末考试越来越近了，保持节奏，你没问题的！</p>
        </div>
        <n-icon :component="SchoolOutline" size="100" class="welcome-icon" />
      </div>
    </n-card>

    <n-grid :x-gap="20" :y-gap="20" :cols="4" item-responsive responsive="screen">
      <n-grid-item span="4 m:2 l:1">
        <div class="stat-card blue-gradient" @click="router.push('/question-manage')">
          <div class="stat-info">
            <span class="stat-label">题目总库</span>
            <span class="stat-value">{{ statistics.totalQuestions }}</span>
          </div>
          <n-icon :component="ListOutline" class="stat-icon" />
        </div>
      </n-grid-item>

      <n-grid-item span="4 m:2 l:1">
        <div class="stat-card green-gradient" @click="router.push('/practice')">
          <div class="stat-info">
            <span class="stat-label">已刷题目</span>
            <span class="stat-value">{{ statistics.practiced }}</span>
          </div>
          <n-icon :component="CreateOutline" class="stat-icon" />
        </div>
      </n-grid-item>

      <n-grid-item span="4 m:2 l:1">
        <div class="stat-card orange-gradient" @click="router.push('/wrong-book')">
          <div class="stat-info">
            <span class="stat-label">待攻克错题</span>
            <span class="stat-value">{{ statistics.wrongCount }}</span>
          </div>
          <n-icon :component="BookmarkOutline" class="stat-icon" />
        </div>
      </n-grid-item>

      <n-grid-item span="4 m:2 l:1">
        <div class="stat-card purple-gradient">
          <div class="stat-info">
            <span class="stat-label">正确率</span>
            <span class="stat-value">{{ statistics.correctRate }}%</span>
          </div>
          <n-icon :component="StatsChartOutline" class="stat-icon" />
        </div>
      </n-grid-item>
    </n-grid>

    <div class="section-title">快速开始</div>
    <n-grid :x-gap="20" :cols="3">
      <n-grid-item>
        <n-card hoverable class="action-card" @click="router.push('/practice')">
          <n-space align="center">
            <div class="icon-box green">
              <n-icon :component="PlayOutline" color="#fff" size="24" />
            </div>
            <div>
              <div class="action-title">随机练习</div>
              <div class="action-desc">从题库中随机抽取题目</div>
            </div>
          </n-space>
        </n-card>
      </n-grid-item>
      
      <n-grid-item>
        <n-card hoverable class="action-card" @click="router.push('/wrong-book')">
          <n-space align="center">
            <div class="icon-box orange">
              <n-icon :component="FlashOutline" color="#fff" size="24" />
            </div>
            <div>
              <div class="action-title">错题突击</div>
              <div class="action-desc">专注于做错的题目</div>
            </div>
          </n-space>
        </n-card>
      </n-grid-item>

      <n-grid-item>
        <n-card hoverable class="action-card" @click="router.push('/question-manage')">
          <n-space align="center">
            <div class="icon-box blue">
              <n-icon :component="CloudUploadOutline" color="#fff" size="24" />
            </div>
            <div>
              <div class="action-title">导入题库</div>
              <div class="action-desc">上传Excel更新题目</div>
            </div>
          </n-space>
        </n-card>
      </n-grid-item>
    </n-grid>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NCard, NGrid, NGridItem, NIcon, NSpace, useMessage } from 'naive-ui'
import { 
  ListOutline, CreateOutline, BookmarkOutline, StatsChartOutline, 
  SchoolOutline, PlayOutline, FlashOutline, CloudUploadOutline 
} from '@vicons/ionicons5'
import { getStatistics } from '@/api/practice'

const router = useRouter()
const message = useMessage()

const statistics = ref({
  totalQuestions: 0,
  practiced: 0,
  wrongCount: 0,
  correctRate: 0
})

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    if (res.code === 200) {
      statistics.value.totalQuestions = res.data.totalQuestions || 0
      statistics.value.practiced = res.data.totalPracticeCount || 0
      statistics.value.wrongCount = res.data.wrongQuestionCount || 0
      statistics.value.correctRate = parseFloat(res.data.correctRate || 0)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    message.error('加载统计数据失败')
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  background: linear-gradient(135deg, #fff 0%, #f0fdf4 100%);
  margin-bottom: 24px;
  border-radius: 12px;
}
.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.welcome-title {
  font-size: 24px;
  margin-bottom: 8px;
  color: #18a058;
}
.welcome-desc {
  color: #666;
  font-size: 14px;
}
.welcome-icon {
  opacity: 0.1;
  color: #18a058;
}

/* 统计卡片样式 */
.stat-card {
  height: 120px;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
}
.stat-card:hover {
  transform: translateY(-4px);
}
.stat-info {
  z-index: 1;
}
.stat-label {
  display: block;
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
}
.stat-icon {
  font-size: 64px;
  opacity: 0.2;
  position: absolute;
  right: -10px;
  bottom: -10px;
}

/* 渐变色 */
.blue-gradient { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); }
.green-gradient { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
.orange-gradient { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); }
.purple-gradient { background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); }

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 32px 0 16px;
  color: #333;
}

/* 快捷入口卡片 */
.action-card {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}
.action-card:hover {
  border-color: #18a058;
}
.icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon-box.green { background: #18a058; }
.icon-box.orange { background: #f59e0b; }
.icon-box.blue { background: #3b82f6; }

.action-title {
  font-weight: bold;
  font-size: 16px;
}
.action-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
