<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-text">
        <h2>👋 您好，{{ userInfo?.nickName || userInfo?.userName || '用户' }}！</h2>
        <p>今天是 {{ today }}，继续加油！</p>
      </div>
      <div class="quick-stats">
        <div class="stat-item stat-pending">
          <div class="stat-num">{{ stats.pending }}</div>
          <div class="stat-label">待审批</div>
        </div>
        <div class="stat-item stat-draft">
          <div class="stat-num">{{ stats.draft }}</div>
          <div class="stat-label">草稿</div>
        </div>
        <div class="stat-item stat-published">
          <div class="stat-num">{{ stats.published }}</div>
          <div class="stat-label">已发布</div>
        </div>
        <div class="stat-item stat-rejected">
          <div class="stat-num">{{ stats.rejected }}</div>
          <div class="stat-label">已驳回</div>
        </div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <el-row :gutter="16" class="quick-actions mb-4">
      <el-col :span="6">
        <div class="action-card action-primary" @click="$router.push('/workflow/document/create')">
          <el-icon><EditPen /></el-icon>
          <span>新建发文</span>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="action-card action-success" @click="$router.push('/workflow/document/list')">
          <el-icon><Document /></el-icon>
          <span>发文管理</span>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="action-card action-warning" @click="$router.push('/workflow/my-tasks')">
          <el-icon><List /></el-icon>
          <span>我的待办</span>
          <el-badge :value="stats.pending" class="badge" v-if="stats.pending > 0" />
        </div>
      </el-col>
      <el-col :span="6">
        <div class="action-card action-info" @click="$router.push('/system/user')">
          <el-icon><User /></el-icon>
          <span>系统管理</span>
        </div>
      </el-col>
    </el-row>

    <!-- 最近待办 -->
    <el-card class="recent-tasks" v-loading="loadingTasks">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="card-title">🔥 最近待办</span>
          <el-button link type="primary" @click="$router.push('/workflow/my-tasks')">查看全部</el-button>
        </div>
      </template>
      <el-empty v-if="!loadingTasks && recentTasks.length === 0" description="暂无待办任务，太棒了！" />
      <div v-else class="task-list">
        <div v-for="task in recentTasks" :key="task.id" class="task-item" @click="$router.push(`/workflow/document/${task.docId}`)">
          <div class="task-left">
            <el-tag :type="priorityTag(task.priority)" size="small">{{ priorityLabel(task.priority) }}</el-tag>
            <span class="task-name">{{ task.taskName || '审批任务' }}</span>
          </div>
          <div class="task-right">
            <span class="task-title text-gray-500">{{ task.docTitle }}</span>
            <el-button link type="primary" size="small">处理 →</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 最近发文 -->
    <el-card class="recent-docs" v-loading="loadingDocs">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="card-title">📄 最近发文</span>
          <el-button link type="primary" @click="$router.push('/workflow/document/list')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentDocs" stripe size="small" :show-header="true">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
          <template #default="{row}">
            <span class="doc-title-link">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="docType" label="类型" width="100">
          <template #default="{row}"><el-tag size="small">{{ docTypeLabel(row.docType) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}"><el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="$router.push(`/workflow/document/${row.id}`)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { getDocumentList, getMyTasks } from '@/api/workflow/document'
import { EditPen, Document, List, User } from '@element-plus/icons-vue'

const auth = useAuthStore()
const userInfo = computed(() => auth.userInfo)
const today = new Date().toLocaleDateString('zh-CN', { year:'numeric', month:'long', day:'numeric', weekday:'long' })

const loadingTasks = ref(false)
const loadingDocs = ref(false)
const recentTasks = ref([])
const recentDocs = ref([])

const stats = reactive({ pending: 0, draft: 0, published: 0, rejected: 0 })

onMounted(() => { loadDashboard() })

async function loadDashboard() {
  const user = JSON.parse(localStorage.getItem('oa_user') || '{}')
  const userId = user.id || 1

  // 并行加载
  try {
    const [taskRes, draftRes, pubRes, rejectRes] = await Promise.all([
      getMyTasks(userId).catch(() => []),
      getDocumentList({ status: 'draft', pageNum: 1, pageSize: 1 }).catch(() => ({})),
      getDocumentList({ status: 'approved', pageNum: 1, pageSize: 1 }).catch(() => ({})),
      getDocumentList({ status: 'rejected', pageNum: 1, pageSize: 1 }).catch(() => ({})),
    ])
    recentTasks.value = (Array.isArray(taskRes) ? taskRes : []).slice(0, 5)
    stats.pending = Array.isArray(taskRes) ? taskRes.length : 0
    stats.draft = draftRes.total || 0
    stats.published = pubRes.total || 0
    stats.rejected = rejectRes.total || 0

    const docsRes = await getDocumentList({ pageNum: 1, pageSize: 5 }).catch(() => ({}))
    recentDocs.value = docsRes.records || docsRes.list || []
  } catch {}
}

function docTypeLabel(v) { return {notice:'通知',info:'简报',report:'报告'}[v]||v }
function priorityLabel(v) { return {low:'低',normal:'普通',high:'紧急'}[v]||v }
function priorityTag(v) { return {low:'info',normal:'',high:'danger'}[v]||'' }
function statusLabel(v) { return {draft:'草稿',pending:'待审',approved:'已发布',rejected:'已驳回'}[v]||v }
function statusTag(v) { return {draft:'info',pending:'warning',approved:'success',rejected:'danger'}[v]||'' }
</script>

<style scoped>
.dashboard { padding: 0; }

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(102,126,234,0.3);
}
.welcome-text h2 { margin: 0 0 4px; font-size: 20px; font-weight: 600; }
.welcome-text p { margin: 0; opacity: 0.85; font-size: 14px; }

.quick-stats { display: flex; gap: 24px; }
.stat-item { text-align: center; }
.stat-num { font-size: 28px; font-weight: 700; line-height: 1; }
.stat-label { font-size: 12px; opacity: 0.8; margin-top: 4px; }

/* 快捷入口 */
.action-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #f0f0f0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  position: relative;
  min-height: 90px;
  justify-content: center;
}
.action-card .el-icon { font-size: 28px; }
.action-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
.action-primary { border-left: 4px solid #409eff; }
.action-primary .el-icon { color: #409eff; }
.action-success { border-left: 4px solid #67c23a; }
.action-success .el-icon { color: #67c23a; }
.action-warning { border-left: 4px solid #e6a23c; }
.action-warning .el-icon { color: #e6a23c; }
.action-info { border-left: 4px solid #909399; }
.action-info .el-icon { color: #909399; }
.badge { position: absolute; top: 8px; right: 8px; }

/* 最近待办 */
.task-list { display: flex; flex-direction: column; gap: 0; }
.task-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 4px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.15s;
  border-radius: 6px;
}
.task-item:hover { background: #f9fafb; }
.task-item:last-child { border-bottom: none; }
.task-left { display: flex; align-items: center; gap: 10px; }
.task-name { font-weight: 500; color: #303133; }
.task-right { display: flex; align-items: center; gap: 12px; }
.task-title { font-size: 13px; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* 最近发文 */
.doc-title-link { color: #409eff; }
.doc-title-link:hover { text-decoration: underline; }
.card-title { font-size: 15px; font-weight: 600; color: #303133; }
.mb-4 { margin-bottom: 16px; }
.mb-6 { margin-bottom: 24px; }
</style>
