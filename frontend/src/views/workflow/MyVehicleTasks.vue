<template>
  <div class="my-vehicle-tasks">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <span class="text-lg font-semibold">🚗 用车申请待办</span>
          <el-badge :value="tasks.length" :hidden="tasks.length === 0">
            <el-tag type="warning" size="large">待处理</el-tag>
          </el-badge>
        </div>
      </template>

      <el-empty v-if="!loading && tasks.length === 0" description="太棒了，暂时没有待审批的用车申请！🎉" />

      <el-row :gutter="16" v-else>
        <el-col :span="8" v-for="task in tasks" :key="task.id">
          <el-card class="task-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <el-tag type="warning" size="small">待审批</el-tag>
                <span class="card-id">#{{ task.id }}</span>
              </div>
            </template>
            <div class="task-body">
              <h3 class="task-title">{{ task.reason || '用车申请' }}</h3>
              <div class="task-info">
                <div class="info-row">
                  <el-icon><Location /></el-icon>
                  <span>{{ task.destination }}</span>
                </div>
                <div class="info-row">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ task.vehicleTime }}</span>
                </div>
                <div class="info-row">
                  <el-icon><User /></el-icon>
                  <span>{{ task.passengerCount }} 人</span>
                </div>
                <div class="info-row">
                  <el-icon><Clock /></el-icon>
                  <span>{{ task.createTime }}</span>
                </div>
              </div>
              <div class="task-actions">
                <el-button type="primary" @click="handleProcess(task)" style="width:100%">处理</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Clock, User, Location, Calendar } from '@element-plus/icons-vue'
import { getMyVehicleTasks } from '@/api/workflow/vehicle'

const router = useRouter()
const loading = ref(false)
const tasks = ref([])

onMounted(() => loadTasks())

async function loadTasks() {
  loading.value = true
  try {
    const res = await getMyVehicleTasks()
    tasks.value = Array.isArray(res) ? res : []
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function handleProcess(row) {
  router.push(`/workflow/vehicle/${row.id}`)
}
</script>

<style scoped>
.my-vehicle-tasks { padding: 4px 0; }
.task-card {
  margin-bottom: 16px;
  border-radius: 10px;
  transition: all 0.2s;
}
.task-card:hover { transform: translateY(-2px); }
.card-header { display: flex; gap: 8px; align-items: center; justify-content: space-between; }
.card-id { font-size: 12px; color: #909399; }
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
