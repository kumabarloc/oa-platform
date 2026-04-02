<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="aside">
      <div class="logo">
        <img src="@/assets/logo.svg" alt="OA" />
        <span v-if="!isCollapsed">OA 平台</span>
      </div>
      <el-menu :default-active="activeMenu" :collapse="isCollapsed" :router="true" class="sidebar-menu">
        <template v-for="item in filteredMenus" :key="item.id">
          <el-sub-menu v-if="item.children?.length" :index="item.path || String(item.id)">
            <template #title><el-icon><component :is="item.icon || 'Menu'" /></el-icon><span>{{ item.menuName }}</span></template>
            <el-menu-item v-for="sub in item.children" :key="sub.id" :index="sub.path">{{ sub.menuName }}</el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path"><el-icon><component :is="item.icon || 'Menu'" /></el-icon><span>{{ item.menuName }}</span></el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="left"><el-icon class="collapse-btn" @click="isCollapsed = !isCollapsed"><Expand v-if="isCollapsed" /><Fold v-else /></el-icon></div>
        <div class="right">
          <el-badge :value="unreadCount" :hidden="!unreadCount" class="notif-badge">
            <el-icon size="20" style="cursor:pointer;margin-right:16px" @click="$router.push('/system/notification')"><Bell /></el-icon>
          </el-badge>
          <el-dropdown @command="handleCommand">
            <span class="user-info"><el-avatar :size="32">{{ userInfo?.nickName?.[0] || 'U' }}</el-avatar><span class="username">{{ userInfo?.userName || '用户' }}</span></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Fold, Expand, Bell } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const isCollapsed = ref(false)
const userInfo = computed(() => auth.userInfo)
const unreadCount = ref(0)

const allMenus = [
  { id: 1, menuName: '工作台', path: '/dashboard', icon: 'HomeFilled' },
  { id: 2, menuName: '系统管理', icon: 'Setting', children: [
    { id: 21, menuName: '用户管理', path: '/system/user', perms: 'system:user:list' },
    { id: 22, menuName: '角色管理', path: '/system/role', perms: 'system:role:list' },
    { id: 23, menuName: '部门管理', path: '/system/dept', perms: 'system:dept:list' },
    { id: 24, menuName: '菜单管理', path: '/system/menu', perms: 'system:menu:list' },
    { id: 25, menuName: '通知管理', path: '/system/notification' },
    { id: 26, menuName: '登录日志', path: '/system/log/login' },
    { id: 27, menuName: '操作日志', path: '/system/log/operation' },
  ]},
  { id: 3, menuName: '公文管理', icon: 'Document', children: [
    { id: 31, menuName: '发文管理', path: '/workflow/document/list', perms: 'oa:document:list' },
    { id: 32, menuName: '收文管理', path: '/workflow/incoming' },
    { id: 33, menuName: '我的待办', path: '/workflow/my-tasks' },
  ]},
]

const activeMenu = computed(() => route.path)
const filteredMenus = computed(() => allMenus)

function handleCommand(cmd) {
  if (cmd === 'logout') { auth.logout(); router.push('/login'); ElMessage.success('已退出登录') }
  else if (cmd === 'profile') { router.push('/profile') }
}

onMounted(async () => {
  if (!auth.userInfo) await auth.fetchUserInfo()
  try {
    const { getUnreadCount } = await import('@/api/system/notification')
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch {}
})
</script>

<style scoped>
.layout-container { height: 100vh; }
.aside { background: #304156; transition: width 0.3s; overflow: hidden; }
.logo { height: 60px; display: flex; align-items: center; padding: 0 16px; color: #fff; font-size: 18px; font-weight: bold; border-bottom: 1px solid #3a4a5a; }
.logo img { width: 32px; height: 32px; margin-right: 8px; }
.sidebar-menu { border-right: none; background: #304156; }
.sidebar-menu:not(.el-menu--collapse) { width: 220px; }
:deep(.el-menu) { background: #304156; }
:deep(.el-menu-item), :deep(.el-sub-menu__title) { color: #bfcbd9; }
:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) { background: #263445 !important; }
:deep(.el-menu-item.is-active) { background: #409eff !important; color: #fff; }
.header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e6e6e6; padding: 0 16px; }
.collapse-btn { font-size: 20px; cursor: pointer; color: #666; }
.user-info { display: flex; align-items: center; cursor: pointer; gap: 8px; }
.username { color: #333; font-size: 14px; }
.main { background: #f0f2f5; padding: 16px; }
.notif-badge { display: flex; align-items: center; }
</style>
