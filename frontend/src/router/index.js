import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('@/views/login/index.vue'), meta: { title: '登录', requiresAuth: false } },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/layout/dashboard.vue'), meta: { title: '工作台' } },
      { path: 'system/user', name: 'UserManage', component: () => import('@/views/system/user/index.vue'), meta: { title: '用户管理', permissions: ['system:user:list'] } },
      { path: 'system/role', name: 'RoleManage', component: () => import('@/views/system/role/index.vue'), meta: { title: '角色管理', permissions: ['system:role:list'] } },
      { path: 'system/dept', name: 'DeptManage', component: () => import('@/views/system/dept/index.vue'), meta: { title: '部门管理', permissions: ['system:dept:list'] } },
      { path: 'system/menu', name: 'MenuManage', component: () => import('@/views/system/menu/index.vue'), meta: { title: '菜单管理', permissions: ['system:menu:list'] } },
      { path: 'system/notification', name: 'NotificationManage', component: () => import('@/views/system/notification/index.vue'), meta: { title: '通知管理' } },
      { path: 'system/log/login', name: 'LoginLogManage', component: () => import('@/views/system/log/LoginLog.vue'), meta: { title: '登录日志' } },
      { path: 'system/log/operation', name: 'OperationLogManage', component: () => import('@/views/system/log/OperationLog.vue'), meta: { title: '操作日志' } },
      { path: 'workflow/document/list', name: 'DocumentList', component: () => import('@/views/workflow/DocumentList.vue'), meta: { title: '发文管理' } },
      { path: 'workflow/document/create', name: 'DocumentCreate', component: () => import('@/views/workflow/DocumentForm.vue'), meta: { title: '新建发文' } },
      { path: 'workflow/document/edit/:id', name: 'DocumentEdit', component: () => import('@/views/workflow/DocumentForm.vue'), meta: { title: '编辑发文' } },
      { path: 'workflow/document/:id', name: 'DocumentDetail', component: () => import('@/views/workflow/DocumentDetail.vue'), meta: { title: '公文详情' } },
      { path: 'workflow/vehicle/list', name: 'VehicleList', component: () => import('@/views/workflow/VehicleList.vue'), meta: { title: '用车申请' } },
      { path: 'workflow/vehicle/create', name: 'VehicleCreate', component: () => import('@/views/workflow/VehicleForm.vue'), meta: { title: '新建用车申请' } },
      { path: 'workflow/vehicle/edit/:id', name: 'VehicleEdit', component: () => import('@/views/workflow/VehicleForm.vue'), meta: { title: '编辑用车申请' } },
      { path: 'workflow/vehicle/:id', name: 'VehicleDetail', component: () => import('@/views/workflow/VehicleDetail.vue'), meta: { title: '用车申请详情' } },
      { path: 'workflow/vehicle/my-tasks', name: 'MyVehicleTasks', component: () => import('@/views/workflow/MyVehicleTasks.vue'), meta: { title: '用车申请待办' } },
      { path: 'workflow/my-tasks', name: 'MyTasks', component: () => import('@/views/workflow/MyTasks.vue'), meta: { title: '我的待办' } },
      { path: 'workflow/incoming', name: 'IncomingManage', component: () => import('@/views/workflow/IncomingList.vue'), meta: { title: '收文管理' } },
      { path: 'workflow/incoming/:id', name: 'IncomingDetail', component: () => import('@/views/workflow/IncomingDetail.vue'), meta: { title: '收文详情' } },
    ],
  },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth !== false && !auth.isLoggedIn) return next('/login')
  if (to.path === '/login' && auth.isLoggedIn) return next('/dashboard')
  next()
})

export default router
