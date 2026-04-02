<template>
  <div class="my-tasks">
    <!-- 标签切换 -->
    <el-radio-group v-model="activeTab" size="large" class="mb-4">
      <el-radio-button value="pending">
        <el-badge :value="tasks.length" :hidden="tasks.length === 0">待处理</el-badge>
      </el-radio-button>
      <el-radio-button value="done">已处理</el-radio-button>
    </el-radio-group>

    <!-- 待办卡片列表 -->
    <div v-if="activeTab === 'pending'">
      <el-empty v-if="!loading && tasks.length === 0" description="太棒了，暂时没有待处理的任务！🎉" />
      <el-row :gutter="16" v-else>
        <el-col :span="8" v-for="task in tasks" :key="task.id">
          <el-card class="task-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-tag :type="priorityTag(task.priority)" size="small">{{ priorityLabel(task.priority) }}</el-tag>
                <el-tag type="warning" size="small">待审批</el-tag>
              </div>
            </template>
            <div class="task-body">
              <h3 class="task-title">{{ task.docTitle || '审批任务' }}</h3>
              <div class="task-info">
                <div class="info-row">
                  <el-icon><Clock /></el-icon>
                  <span>{{ task.createTime || '刚刚' }}</span>
                </div>
                <div class="info-row">
                  <el-icon><User /></el-icon>
                  <span>{{ task.assigneeName || '系统分配' }}</span>
                </div>
                <div class="info-row">
                  <el-icon><Document /></el-icon>
                  <span>{{ docTypeLabel(task.docType) }}</span>
                </div>
              </div>
              <div class="task-actions">
                <el-button type="primary" @click="handleProcess(task)" style="width:100%">处理</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 已处理列表 -->
    <div v-if="activeTab === 'done'">
      <el-empty v-if="!loadingDone && doneTasks.length === 0" description="暂无已处理记录" />
      <el-table v-else :data="doneTasks" stripe>
        <el-table-column prop="docTitle" label="公文标题" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="taskName" label="处理节点" width="160"/>
        <el-table-column prop="docType" label="类型" width="100">
          <template #default="{row}"><el-tag size="small">{{ docTypeLabel(row.docType) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="endTime" label="处理时间" width="170"/>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="$router.push(`/workflow/document/${row.docId}`)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Clock, User, Document } from '@element-plus/icons-vue'
import { getMyTasks } from '@/api/workflow/document'

const router = useRouter()
const loading = ref(false)
const loadingDone = ref(false)
const activeTab = ref('pending')
const tasks = ref([])
const doneTasks = ref([])

onMounted(() => loadTasks())

async function loadTasks() {
  loading.value = true
  try {
    const user = JSON.parse(localStorage.getItem('oa_user') || '{}')
    const res = await getMyTasks(user.id || 1)
    tasks.value = Array.isArray(res) ? res : []
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function handleProcess(row) {
  router.push(`/workflow/document/${row.docId}`)
}

function docTypeLabel(v) { return {notice:'通知',info:'简报',report:'报告'}[v]||v }
function priorityLabel(v) { return {low:'低',normal:'普通',high:'紧急'}[v]||v }
function priorityTag(v) { return {low:'info',normal:'',high:'danger'}[v]||'' }
</script>

<style scoped>
.my-tasks { padding: 4px 0; }
.mb-4 { margin-bottom: 16px; }
.task-card {
  margin-bottom: 16px;
  border-radius: 10px;
  transition: all 0.2s;
}
.task-card:hover { transform: translateY(-2px); }
.card-header { display: flex; gap: 8px; align-items: center; justify-content: space-between; }
.task-body { display: flex; flex-direction: column; gap: 12px; }
.task-title {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 44px;
}
.task-info { display: flex; flex-direction: column; gap: 6px; }
.info-row { display: flex; align-items: center; gap: 6px; color: #606266; font-size: 13px; }
.info-row .el-icon { color: #909399; }
.task-actions { margin-top: 4px; }
</style>
