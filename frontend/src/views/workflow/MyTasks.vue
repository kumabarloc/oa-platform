<template>
  <div class="my-tasks">
    <el-card>
      <template #header>
        <span class="text-lg font-semibold">我的待办</span>
      </template>

      <el-table :data="tasks" v-loading="loading" stripe>
        <el-table-column prop="taskName" label="任务名称" width="200"/>
        <el-table-column prop="docTitle" label="公文标题" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="docType" label="类型" width="100">
          <template #default="{row}">
            <el-tag>{{ docTypeLabel(row.docType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{row}">
            <el-tag :type="priorityTag(row.priority)">{{ priorityLabel(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="接收时间" width="170"/>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="handleProcess(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && tasks.length === 0" description="暂无待办任务"/>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyTasks } from '@/api/workflow/document'

const router = useRouter()
const loading = ref(false)
const tasks = ref([])

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
