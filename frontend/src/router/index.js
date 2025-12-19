import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: '/question-manage',
        name: 'QuestionManage',
        component: () => import('@/views/QuestionManage.vue'),
        meta: { title: '题目管理' }
      },
      {
        path: '/question-converter',
        name: 'QuestionConverter',
        component: () => import('@/views/QuestionConverter.vue'),
        meta: { title: '题库转换' }
      },
      {
        path: '/practice',
        name: 'Practice',
        component: () => import('@/views/Practice.vue'),
        meta: { title: '开始练习' }
      },
      {
        path: '/wrong-book',
        name: 'WrongBook',
        component: () => import('@/views/WrongBook.vue'),
        meta: { title: '错题本' }
      },
      {
        path: '/statistics',
        name: 'Statistics',
        component: () => import('@/views/Statistics.vue'),
        meta: { title: '统计分析' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
