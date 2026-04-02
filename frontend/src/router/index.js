import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/layout/dashboard.vue'),
        meta: { title: '工作台' },
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', permissions: ['system:user:list'] },
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', permissions: ['system:role:list'] },
      },
      {
        path: 'system/dept',
        name: 'DeptManage',
        component: () => import('@/views/system/dept/index.vue'),
        meta: { title: '部门管理', permissions: ['system:dept:list'] },
      },
      {
        path: 'system/menu',
        name: 'MenuManage',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', permissions: ['system:menu:list'] },
      },
      {
        path: 'workflow/document/list',
        name: 'DocumentList',
        component: () => import('@/views/workflow/DocumentList.vue'),
        meta: { title: '发文管理' },
      },
      {
        path: 'workflow/document/create',
        name: 'DocumentCreate',
        component: () => import('@/views/workflow/DocumentForm.vue'),
        meta: { title: '新建发文' },
      },
      {
        path: 'workflow/document/edit/:id',
        name: 'DocumentEdit',
        component: () => import('@/views/workflow/DocumentForm.vue'),
        meta: { title: '编辑发文' },
      },
      {
        path: 'workflow/document/:id',
        name: 'DocumentDetail',
        component: () => import('@/views/workflow/DocumentDetail.vue'),
        meta: { title: '公文详情' },
      },
      {
        path: 'workflow/my-tasks',
        name: 'MyTasks',
        component: () => import('@/views/workflow/MyTasks.vue'),
        meta: { title: '我的待办' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫：登录验证 + 权限过滤
router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  console.log('[Router Guard] path:', to.path, '| isLoggedIn:', auth.isLoggedIn, '| requiresAuth:', to.meta.requiresAuth)

  if (to.meta.requiresAuth !== false && !auth.isLoggedIn) {
    console.log('[Router Guard] → redirect to /login (not logged in)')
    return next('/login')
  }

  if (to.path === '/login' && auth.isLoggedIn) {
    console.log('[Router Guard] → redirect to /dashboard (already logged in)')
    return next('/dashboard')
  }

  next()
})

export default router