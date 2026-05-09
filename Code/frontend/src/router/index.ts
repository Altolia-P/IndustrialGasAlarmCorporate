import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ===== 前台路由 =====
    {
      path: '/',
      component: () => import('@/layouts/FrontLayout.vue'),
      children: [
        {
          path: '',
          name: 'Home',
          meta: { title: '首页' },
          component: () => import('@/views/home/index.vue')
        },
        {
          path: 'products',
          name: 'ProductList',
          meta: { title: '产品中心' },
          component: () => import('@/views/product/list/index.vue')
        },
        {
          path: 'products/:uuid',
          name: 'ProductDetail',
          meta: { title: '产品详情' },
          component: () => import('@/views/product/detail/index.vue')
        },
        {
          path: 'solutions',
          name: 'SolutionList',
          meta: { title: '解决方案' },
          component: () => import('@/views/content/list/index.vue')
        },
        {
          path: 'solutions/:uuid',
          name: 'SolutionDetail',
          meta: { title: '解决方案详情' },
          component: () => import('@/views/content/detail/index.vue')
        },
        {
          path: 'contact',
          name: 'Contact',
          meta: { title: '联系我们' },
          component: () => import('@/views/contact/index.vue')
        }
      ]
    },

    // ===== 后台路由 =====
    {
      path: '/admin/login',
      name: 'AdminLogin',
      meta: { title: '管理员登录' },
      component: () => import('@/views/admin/login/index.vue')
    },
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'AdminDashboard',
          meta: { title: '首页概览', requiresAuth: true },
          component: () => import('@/views/admin/dashboard/index.vue')
        },
        {
          path: 'products',
          name: 'AdminProducts',
          meta: { title: '产品管理', requiresAuth: true },
          component: () => import('@/views/admin/product/list/index.vue')
        },
        {
          path: 'products/create',
          name: 'AdminProductCreate',
          meta: { title: '新增产品', requiresAuth: true },
          component: () => import('@/views/admin/product/edit/index.vue')
        },
        {
          path: 'products/:uuid/edit',
          name: 'AdminProductEdit',
          meta: { title: '编辑产品', requiresAuth: true },
          component: () => import('@/views/admin/product/edit/index.vue')
        },
        {
          path: 'contents',
          name: 'AdminContents',
          meta: { title: '内容管理', requiresAuth: true },
          component: () => import('@/views/admin/content/list/index.vue')
        },
        {
          path: 'contents/create',
          name: 'AdminContentCreate',
          meta: { title: '新增内容', requiresAuth: true },
          component: () => import('@/views/admin/content/edit/index.vue')
        },
        {
          path: 'contents/:uuid/edit',
          name: 'AdminContentEdit',
          meta: { title: '编辑内容', requiresAuth: true },
          component: () => import('@/views/admin/content/edit/index.vue')
        },
        {
          path: 'messages',
          name: 'AdminMessages',
          meta: { title: '留言管理', requiresAuth: true },
          component: () => import('@/views/admin/message/list/index.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = getToken()

  if (to.meta.requiresAuth && !token) {
    next({ name: 'AdminLogin', query: { redirect: to.fullPath } })
  } else if (to.name === 'AdminLogin' && token) {
    next({ name: 'AdminDashboard' })
  } else {
    next()
  }
})

export default router
