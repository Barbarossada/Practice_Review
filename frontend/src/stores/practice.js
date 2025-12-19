import { defineStore } from 'pinia'

export const usePracticeStore = defineStore('practice', {
  state: () => ({
    currentQuestion: null,
    userAnswer: null,
    showAnalysis: false,
    practiceHistory: [],
    wrongQuestions: []
  }),

  getters: {
    totalPracticeCount: (state) => state.practiceHistory.length,
    correctCount: (state) => 
      state.practiceHistory.filter(item => item.isCorrect).length,
    wrongCount: (state) => 
      state.practiceHistory.filter(item => !item.isCorrect).length,
    correctRate: (state) => {
      if (state.practiceHistory.length === 0) return 0
      const correct = state.practiceHistory.filter(item => item.isCorrect).length
      return ((correct / state.practiceHistory.length) * 100).toFixed(2)
    }
  },

  actions: {
    setCurrentQuestion(question) {
      this.currentQuestion = question
      this.userAnswer = null
      this.showAnalysis = false
    },

    submitAnswer(answer) {
      this.userAnswer = answer
      this.showAnalysis = true

      const isCorrect = answer === this.currentQuestion.answer

      // 记录到历史
      this.practiceHistory.push({
        questionId: this.currentQuestion.id,
        question: this.currentQuestion.content,
        userAnswer: answer,
        correctAnswer: this.currentQuestion.answer,
        isCorrect,
        timestamp: Date.now()
      })

      // 如果答错，加入错题本
      if (!isCorrect) {
        const exists = this.wrongQuestions.find(
          q => q.id === this.currentQuestion.id
        )
        if (!exists) {
          this.wrongQuestions.push({ ...this.currentQuestion })
        }
      }
    },

    clearHistory() {
      this.practiceHistory = []
      this.wrongQuestions = []
    },

    reset() {
      this.currentQuestion = null
      this.userAnswer = null
      this.showAnalysis = false
    }
  },

  persist: {
    key: 'practice-store',
    storage: localStorage,
    paths: ['practiceHistory', 'wrongQuestions']
  }
})
