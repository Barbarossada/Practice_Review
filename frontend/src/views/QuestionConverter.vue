<template>
  <div class="converter-container">
    <n-card title="题库转换工具" :bordered="false" class="converter-card">
      <n-space vertical size="large">
        <n-alert title="使用说明" type="info">
          <p>1. 选择包含题目的 txt 文件</p>
          <p>2. 系统会自动解析题目和答案</p>
          <p>3. 点击"转换为Excel"生成可导入的Excel文件</p>
          <p>4. 题目格式：题号开头，选项A-D/E，答案在文件末尾（1-5 A B C...格式）</p>
        </n-alert>

        <n-upload
          :max="1"
          accept=".txt"
          :on-before-upload="handleFileSelect"
          :show-file-list="false"
        >
          <n-button type="primary" size="large" block>
            <template #icon>
              <n-icon :component="DocumentTextOutline" />
            </template>
            选择 TXT 文件
          </n-button>
        </n-upload>

        <div v-if="fileName" class="file-info">
          <n-tag type="success" size="large">
            <template #icon>
              <n-icon :component="CheckmarkCircle" />
            </template>
            已选择: {{ fileName }}
          </n-tag>
        </div>

        <div v-if="parseResult" class="parse-result">
          <n-card title="解析结果" size="small">
            <n-space vertical>
              <n-statistic label="选择题" :value="parseResult.choiceCount" />
              <n-statistic label="判断题" :value="parseResult.judgeCount" />
              <n-statistic label="答案数" :value="parseResult.answerCount" />
            </n-space>
          </n-card>
        </div>

        <n-button
          v-if="parseResult"
          type="success"
          size="large"
          block
          @click="convertToExcel"
          :loading="converting"
        >
          <template #icon>
            <n-icon :component="DownloadOutline" />
          </template>
          转换为 Excel 并下载
        </n-button>
      </n-space>
    </n-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useMessage, NCard, NSpace, NAlert, NUpload, NButton, NIcon, NTag, NStatistic } from 'naive-ui'
import { DocumentTextOutline, CheckmarkCircle, DownloadOutline } from '@vicons/ionicons5'
import * as XLSX from 'xlsx'

const message = useMessage()
const fileName = ref('')
const fileContent = ref('')
const parseResult = ref(null)
const converting = ref(false)

const handleFileSelect = (options) => {
  const file = options.file.file
  fileName.value = file.name

  const reader = new FileReader()
  reader.onload = (e) => {
    fileContent.value = e.target.result
    parseQuestions()
  }
  reader.readAsText(file, 'utf-8')
  
  return false // 阻止自动上传
}

const parseQuestions = () => {
  try {
    const content = fileContent.value
    
    // 分离题目和答案部分
    const answerStartIndex = content.lastIndexOf('\n1-5')
    let questionPart = content
    let answerPart = ''
    
    if (answerStartIndex > 0) {
      questionPart = content.substring(0, answerStartIndex)
      answerPart = content.substring(answerStartIndex)
    }
    
    // 解析题目
    const { choiceQuestions, judgeQuestions } = parseQuestionText(questionPart)
    
    // 解析答案
    const answers = parseAnswers(answerPart)
    
    parseResult.value = {
      choiceQuestions,
      judgeQuestions,
      answers,
      choiceCount: choiceQuestions.length,
      judgeCount: judgeQuestions.length,
      answerCount: Object.keys(answers).length
    }
    
    message.success('题目解析成功！')
  } catch (error) {
    console.error('解析失败:', error)
    message.error('题目解析失败，请检查文件格式')
  }
}

const parseQuestionText = (content) => {
  const choiceQuestions = []
  const judgeQuestions = []
  
  const lines = content.trim().split('\n')
  
  // 跳过第一行（如果不是题号开头）
  let startIndex = 0
  if (lines.length > 0 && !/^\d+[\.\、]?\s*/.test(lines[0])) {
    startIndex = 1
  }
  
  let currentQuestion = null
  
  for (let i = startIndex; i < lines.length; i++) {
    const line = lines[i].trim()
    if (!line) continue
    
    // 匹配题号
    const questionMatch = line.match(/^(\d+)[\.\、]?\s*(.+)/)
    if (questionMatch) {
      // 保存上一题
      if (currentQuestion) {
        if (Object.keys(currentQuestion.options).length > 0) {
          choiceQuestions.push(currentQuestion)
        } else {
          judgeQuestions.push(currentQuestion)
        }
      }
      
      // 开始新题
      currentQuestion = {
        num: parseInt(questionMatch[1]),
        content: questionMatch[2].trim(),
        options: {},
        subject: '习思想',
        difficulty: 'medium'
      }
      continue
    }
    
    // 匹配选项
    const optionMatch = line.match(/^([A-E])\s*[\.\、\:：]?\s*(.+)/)
    if (optionMatch && currentQuestion) {
      const key = optionMatch[1]
      const value = optionMatch[2].trim()
      if (value) {
        currentQuestion.options[key] = value
      }
    }
  }
  
  // 保存最后一题
  if (currentQuestion) {
    if (Object.keys(currentQuestion.options).length > 0) {
      choiceQuestions.push(currentQuestion)
    } else {
      judgeQuestions.push(currentQuestion)
    }
  }
  
  return { choiceQuestions, judgeQuestions }
}

const parseAnswers = (answerText) => {
  const answers = {}
  const lines = answerText.trim().split('\n')
  
  for (const line of lines) {
    const trimmed = line.trim()
    if (!trimmed) continue
    
    const parts = trimmed.split(/\s+/)
    
    let i = 0
    while (i < parts.length) {
      const part = parts[i]
      
      if (part.includes('-')) {
        const dashPos = part.indexOf('-')
        const startStr = part.substring(0, dashPos)
        const afterDash = part.substring(dashPos + 1)
        
        // 提取数字部分作为end
        let endStr = ''
        let firstAnswer = ''
        for (const char of afterDash) {
          if (/\d/.test(char)) {
            endStr += char
          } else {
            firstAnswer = afterDash.substring(endStr.length)
            break
          }
        }
        
        if (!endStr) {
          i++
          continue
        }
        
        const start = parseInt(startStr)
        const end = parseInt(endStr)
        
        i++
        let collected = 0
        
        if (firstAnswer) {
          answers[start] = firstAnswer
          collected = 1
        }
        
        while (i < parts.length && collected < (end - start + 1)) {
          if (parts[i].includes('-')) break
          answers[start + collected] = parts[i]
          collected++
          i++
        }
      } else {
        i++
      }
    }
  }
  
  return answers
}

const convertToExcel = () => {
  converting.value = true
  
  try {
    const { choiceQuestions, judgeQuestions, answers } = parseResult.value
    
    // 创建工作簿
    const wb = XLSX.utils.book_new()
    
    // 创建选择题工作表
    const choiceData = [
      ['题目', '选项A', '选项B', '选项C', '选项D', '答案', '解析', '科目', '难度']
    ]
    
    for (const q of choiceQuestions) {
      choiceData.push([
        q.content,
        q.options['A'] || '',
        q.options['B'] || '',
        q.options['C'] || '',
        q.options['D'] || '',
        answers[q.num] || '',
        '',
        q.subject,
        q.difficulty
      ])
    }
    
    const wsChoice = XLSX.utils.aoa_to_sheet(choiceData)
    
    // 设置列宽
    wsChoice['!cols'] = [
      { wch: 50 }, // 题目
      { wch: 30 }, // 选项A
      { wch: 30 }, // 选项B
      { wch: 30 }, // 选项C
      { wch: 30 }, // 选项D
      { wch: 10 }, // 答案
      { wch: 40 }, // 解析
      { wch: 12 }, // 科目
      { wch: 10 }  // 难度
    ]
    
    XLSX.utils.book_append_sheet(wb, wsChoice, '选择题')
    
    // 创建判断题工作表（如果有）
    if (judgeQuestions.length > 0) {
      const judgeData = [
        ['题目', '答案', '解析', '科目', '难度']
      ]
      
      for (const q of judgeQuestions) {
        let answer = answers[q.num] || ''
        if (['T', 'True', '对', '√'].includes(answer)) {
          answer = '正确'
        } else if (['F', 'False', '错', '×'].includes(answer)) {
          answer = '错误'
        }
        
        judgeData.push([
          q.content,
          answer,
          '',
          q.subject,
          q.difficulty
        ])
      }
      
      const wsJudge = XLSX.utils.aoa_to_sheet(judgeData)
      wsJudge['!cols'] = [
        { wch: 60 },
        { wch: 10 },
        { wch: 40 },
        { wch: 12 },
        { wch: 10 }
      ]
      
      XLSX.utils.book_append_sheet(wb, wsJudge, '判断题')
    }
    
    // 生成文件名
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19)
    const filename = `习思想题库_${timestamp}.xlsx`
    
    // 下载文件
    XLSX.writeFile(wb, filename)
    
    message.success('Excel 文件已生成并下载！')
  } catch (error) {
    console.error('转换失败:', error)
    message.error('转换失败，请重试')
  } finally {
    converting.value = false
  }
}
</script>

<style scoped>
.converter-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.converter-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.file-info {
  padding: 16px;
  background: #f0fdf4;
  border-radius: 12px;
  text-align: center;
}

.parse-result {
  margin-top: 20px;
}
</style>
