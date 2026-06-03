import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior(to, _from, savedPosition) {
    if (to.hash) {
      return { el: to.hash, behavior: 'smooth' }
    }
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
  routes: [
    // ===== 前台路由 =====
    {
      path: '/',
      component: () => import('@/layouts/front-layout.vue'),
      children: [
        {
          path: '',
          name: 'Home',
          meta: { title: '首页' },
          component: () => import('@/views/home/home.vue')
        },
        {
          path: 'products',
          name: 'ProductList',
          meta: { title: '产品中心' },
          component: () => import('@/views/product/list/product-list.vue')
        },
        {
          path: 'products/:uuid',
          name: 'ProductDetail',
          meta: { title: '产品详情' },
          component: () => import('@/views/product/detail/product-detail.vue')
        },
        {
          path: 'solutions',
          name: 'SolutionList',
          meta: { title: '解决方案' },
          component: () => import('@/views/content/list/content-list.vue')
        },
        {
          path: 'solutions/:uuid',
          name: 'SolutionDetail',
          meta: { title: '解决方案详情' },
          component: () => import('@/views/content/detail/content-detail.vue')
        },
        {
          path: 'support',
          name: 'Support',
          meta: { title: '服务支持' },
          component: () => import('@/views/support/support.vue')
        },
        {
          path: 'about',
          name: 'About',
          meta: { title: '关于我们' },
          component: () => import('@/views/about/about.vue')
        },
        {
          path: 'contact',
          name: 'Contact',
          meta: { title: '联系我们' },
          component: () => import('@/views/contact/contact.vue')
        },
        {
          path: 'news',
          name: 'NewsList',
          meta: { title: '新闻动态' },
          component: () => import('@/views/news/list/news-list.vue')
        },
        {
          path: 'news/:uuid',
          name: 'NewsDetail',
          meta: { title: '新闻详情' },
          component: () => import('@/views/news/detail/news-detail.vue')
        }
      ]
    },

    // ===== 登录/注册路由 =====
    {
      path: '/login',
      name: 'Login',
      meta: { title: '登录' },
      component: () => import('@/views/admin/login/login.vue')
    },
    {
      path: '/register',
      name: 'Register',
      meta: { title: '注册' },
      component: () => import('@/views/register/register.vue')
    },
    {
      path: '/user',
      component: () => import('@/layouts/user-layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'UserCenter',
          meta: { title: '首页概览', requiresAuth: true },
          component: () => import('@/views/user/user-center.vue')
        },
        {
          path: 'profile',
          name: 'UserProfile',
          meta: { title: '个人信息', requiresAuth: true },
          component: () => import('@/views/user/user-profile.vue')
        },
        {
          path: 'inquiries',
          name: 'UserInquiries',
          meta: { title: '我的咨询', requiresAuth: true },
          component: () => import('@/views/user/user-inquiries.vue')
        },
        {
          path: 'tickets',
          name: 'UserTickets',
          meta: { title: '我的工单', requiresAuth: true },
          component: () => import('@/views/user/user-tickets.vue')
        },
        {
          path: 'settings',
          name: 'UserSettings',
          meta: { title: '账户设置', requiresAuth: true },
          component: () => import('@/views/user/user-settings.vue')
        }
      ]
    },
    // ===== 员工后台路由 =====
    {
      path: '/staff',
      component: () => import('@/layouts/staff-layout.vue'),
      meta: { requiresAuth: true, requiresStaff: true },
      children: [
        {
          path: '',
          name: 'StaffDashboard',
          meta: { title: '首页概览', requiresAuth: true, requiresStaff: true },
          component: () => import('@/views/staff/dashboard/staff-dashboard.vue')
        },
        {
          path: 'profile',
          name: 'StaffProfile',
          meta: { title: '个人信息', requiresAuth: true, requiresStaff: true },
          component: () => import('@/views/staff/profile/staff-profile.vue')
        },
        {
          path: 'tasks',
          name: 'StaffTasks',
          meta: { title: '我的工单任务', requiresAuth: true, requiresStaff: true },
          component: () => import('@/views/staff/tasks/staff-task-list.vue')
        },
        {
          path: 'tasks/:uuid',
          name: 'StaffTaskDetail',
          meta: { title: '工单任务详情', requiresAuth: true, requiresStaff: true },
          component: () => import('@/views/staff/tasks/staff-task-detail.vue')
        },
        {
          path: 'inquiries',
          name: 'StaffInquiries',
          meta: { title: '我的咨询', requiresAuth: true, requiresStaff: true },
          component: () => import('@/views/staff/inquiries/staff-inquiries.vue')
        },
        {
          path: 'settings',
          name: 'StaffSettings',
          meta: { title: '账户设置', requiresAuth: true, requiresStaff: true },
          component: () => import('@/views/staff/settings/staff-settings.vue')
        }
      ]
    },

    // ===== 数据大屏路由（FR-4.13/4.14/4.15）=====
    {
      path: '/dashboard',
      name: 'Dashboard',
      meta: { title: '数据大屏', requiresAuth: true },
      component: () => import('@/views/dashboard/DashboardView.vue')
    },
    {
      path: '/dashboard/history',
      name: 'DashboardHistory',
      meta: { title: '历史数据', requiresAuth: true },
      component: () => import('@/views/dashboard/HistoryView.vue')
    },

    // ===== 后台路由 =====
    {
      path: '/admin',
      component: () => import('@/layouts/admin-layout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          name: 'AdminDashboard',
          meta: { title: '首页概览', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/dashboard/admin-dashboard.vue')
        },
        {
          path: 'products',
          name: 'AdminProducts',
          meta: { title: '产品管理', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/product/list/admin-product-list.vue')
        },
        {
          path: 'products/create',
          name: 'AdminProductCreate',
          meta: { title: '新增产品', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/product/edit/admin-product-edit.vue')
        },
        {
          path: 'products/:uuid/edit',
          name: 'AdminProductEdit',
          meta: { title: '编辑产品', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/product/edit/admin-product-edit.vue')
        },
        {
          path: 'contents',
          name: 'AdminContents',
          meta: { title: '内容管理', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/content/list/admin-content-list.vue')
        },
        {
          path: 'contents/create',
          name: 'AdminContentCreate',
          meta: { title: '新增内容', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/content/edit/admin-content-edit.vue')
        },
        {
          path: 'contents/:uuid/edit',
          name: 'AdminContentEdit',
          meta: { title: '编辑内容', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/content/edit/admin-content-edit.vue')
        },
        {
          path: 'categories',
          name: 'AdminCategories',
          meta: { title: '分类管理', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/category/list/admin-category-list.vue')
        },
        {
          path: 'messages',
          name: 'AdminMessages',
          meta: { title: '留言管理', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/message/list/admin-message-list.vue')
        },
        {
          path: 'staff',
          name: 'AdminStaff',
          meta: { title: '员工管理', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/staff/list/admin-staff-list.vue')
        },
        {
          path: 'staff/create',
          name: 'AdminStaffCreate',
          meta: { title: '新增员工', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/staff/edit/admin-staff-edit.vue')
        },
        {
          path: 'staff/:uuid/edit',
          name: 'AdminStaffEdit',
          meta: { title: '编辑员工', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/staff/edit/admin-staff-edit.vue')
        },
        {
          path: 'workorders',
          name: 'AdminWorkOrders',
          meta: { title: '工单管理', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/workorder/list/admin-workorder-list.vue')
        },
        {
          path: 'workorders/create',
          name: 'AdminWorkOrderCreate',
          meta: { title: '新建工单', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/workorder/edit/admin-workorder-edit.vue')
        },
        {
          path: 'workorders/:uuid/edit',
          name: 'AdminWorkOrderEdit',
          meta: { title: '工单详情', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/workorder/edit/admin-workorder-edit.vue')
        },
        {
          path: 'devices',
          name: 'AdminDevices',
          meta: { title: '设备管理', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/device/list/admin-device-list.vue')
        },
        {
          path: 'devices/create',
          name: 'AdminDeviceCreate',
          meta: { title: '新增设备', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/device/edit/admin-device-edit.vue')
        },
        {
          path: 'devices/:uuid',
          name: 'AdminDeviceDetail',
          meta: { title: '设备详情', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/device/detail/admin-device-detail.vue')
        },
        {
          path: 'devices/:uuid/edit',
          name: 'AdminDeviceEdit',
          meta: { title: '编辑设备', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/device/edit/admin-device-edit.vue')
        },
        {
          path: 'alert-rules',
          name: 'AdminAlertRules',
          meta: { title: '报警规则', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/alert-rule/list/admin-alert-rule-list.vue')
        },
        {
          path: 'alert-rules/create',
          name: 'AdminAlertRuleCreate',
          meta: { title: '新增规则', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/alert-rule/edit/admin-alert-rule-edit.vue')
        },
        {
          path: 'alert-rules/:uuid/edit',
          name: 'AdminAlertRuleEdit',
          meta: { title: '编辑规则', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/alert-rule/edit/admin-alert-rule-edit.vue')
        },
        {
          path: 'alerts',
          name: 'AdminAlerts',
          meta: { title: '报警记录', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/alert/list/admin-alert-list.vue')
        },
        {
          path: 'alerts/:uuid',
          name: 'AdminAlertDetail',
          meta: { title: '报警详情', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/alert/detail/admin-alert-detail.vue')
        },
        {
          path: 'notifications',
          name: 'AdminNotifications',
          meta: { title: '通知记录', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/notification/admin-notification-list.vue')
        },
        {
          path: 'customers/360',
          name: 'AdminCustomer360',
          meta: { title: '客户全景视图', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/customer/admin-customer-360.vue')
        },
        {
          path: 'system-config',
          name: 'AdminSystemConfig',
          meta: { title: '系统配置', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/system-config/admin-system-config.vue')
        },
        {
          path: 'downloads',
          name: 'AdminDownloads',
          meta: { title: '下载中心管理', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/download/admin-download-list.vue')
        },
        {
          path: 'operation-logs',
          name: 'AdminOperationLogs',
          meta: { title: '操作日志', requiresAuth: true, requiresAdmin: true },
          component: () => import('@/views/admin/operation-log/admin-operation-log.vue')
        }
      ]
    }
  ]
})

router.beforeEach(async (to, _from, next) => {
  const token = getToken()
  const isLoginPage = to.name === 'Login'
  const authStore = useAuthStore()

  if (isLoginPage && token) {
    if (!authStore.tokenVerified) {
      await authStore.verifyToken()
    }
    if (authStore.isAdmin) return next({ name: 'AdminDashboard' })
    if (authStore.isStaff) return next({ name: 'StaffDashboard' })
    next({ name: 'UserCenter' })
    return
  }

  if (!isLoginPage && token && !authStore.tokenVerified) {
    const valid = await authStore.verifyToken()
    if (!valid) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }

  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next(token ? { name: 'Home' } : { name: 'Login' })
    return
  }

  if (to.meta.requiresStaff && !authStore.isStaff) {
    next(token ? { name: 'Home' } : { name: 'Login' })
    return
  }

  next()
})

export default router
