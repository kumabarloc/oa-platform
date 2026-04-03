<template>
  <div class="vehicle-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="page-title">🚗 用车申请详情</span>
          <el-button @click="handleBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
        </div>
      </template>

      <div v-if="vehicle">
        <!-- 状态横幅 -->
        <div class="status-banner" :class="`status-${vehicle.status}`">
          <el-tag :type="statusTag(vehicle.status)" size="large">{{ statusLabel(vehicle.status) }}</el-tag>
          <span class="banner-title">{{ vehicle.reason }}</span>
        </div>

        <!-- 申请信息卡片 -->
        <el-card class="info-card" shadow="never">
          <template #header><span class="section-title">基本信息</span></template>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="目的地">{{ vehicle.destination }}</el-descriptions-item>
            <el-descriptions-item label="用车人数">{{ vehicle.passengerCount }} 人</el-descriptions-item>
            <el-descriptions-item label="用车时间">{{ vehicle.vehicleTime }}</el-descriptions-item>
            <el-descriptions-item label="申请人">{{ vehicle.applicantName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="部门">{{ vehicle.deptName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ vehicle.createTime }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="3">{{ vehicle.remark || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 审批时间线 -->
        <el-card class="history-card" shadow="never" v-if="history.length > 0">
          <template #header><div class="section-title">🕐 审批流程</div></template>
          <el-steps direction="vertical" :space="60" :active="history.length - 1">
            <el-step
              v-for="(item, idx) in history"
              :key="idx"
              :title="item.taskName || '审批节点'"
              :description="`${item.assignee || '系统'} · ${item.startTime || ''}`"
            >
              <template #icon v-if="idx === history.length - 1 && vehicle.status === 'pending'">
                <el-icon color="#409eff"><Loading /></el-icon>
              </template>
              <template #title v-if="item.deleteReason === '驳回'">
                <span style="color:#f56c6c">驳回 · {{ item.endTime }}</span>
              </template>
              <template #title v-else-if="item.endTime">
                <span style="color:#67c23a">通过 · {{ item.endTime }}</span>
              </template>
              <template #title v-else>
                <span style="color:#409eff">处理中</span>
              </template>
            </el-step>
          </el-steps>
        </el-card>

        <!-- 操作按钮 -->
        <div class="action-bar" v-if="vehicle.status === 'pending'">
          <el-button type="success" size="large" @click="handleApprove">
            <el-icon><Check /></el-icon> 通过
          </el-button>
          <el-button type="danger" size="large" @click="showRejectDialog=true">
            <el-icon><Close /></el-icon> 驳回
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 驳回对话框 -->
    <el-dialog v-model="showRejectDialog" title="填写驳回原因" width="450px" destroy-on-close>
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请详细说明驳回原因" maxlength="500" show-word-limit />
      <template #footer>
        <el-button @click="showRejectDialog=false">取消</el-button>
        <el-button type="danger" @click="handleReject" :loading="rejecting">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Check, Close, Loading } from '@element-plus/icons-vue'
import { getVehicleDetail, approveVehicle, rejectVehicle } from '@/api/workflow/vehicle'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const vehicle = ref(null)
const history = ref([])
const showRejectDialog = ref(false)
const rejectReason = ref('')
const rejecting = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    vehicle.value = await getVehicleDetail(route.params.id)
    history.value = vehicle.value.history || []
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
})

async function handleApprove() {
  try {
    await ElMessageBox.confirm('确认审批通过该用车申请？', '审批确认')
    const user = JSON.parse(localStorage.getItem('oa_user') || '{}')
    await approveVehicle(route.params.id, { approverId: user.id || 1 })
    ElMessage.success('审批通过！')
    vehicle.value = await getVehicleDetail(route.params.id)
    history.value = vehicle.value.history || []
  } catch(e) { if (e !== 'cancel') ElMessage.error('审批失败') }
}

async function handleReject() {
  if (!rejectReason.value.trim()) { ElMessage.warning('请填写驳回原因'); return }
  rejecting.value = true
  try {
    await rejectVehicle(route.params.id, rejectReason.value)
    ElMessage.success('已驳回')
    showRejectDialog.value = false
    vehicle.value = await getVehicleDetail(route.params.id)
    history.value = vehicle.value.history || []
  } catch { ElMessage.error('驳回失败') }
  finally { rejecting.value = false }
}

function handleBack() { router.back() }

function statusLabel(v) { return {draft:'草稿',pending:'待审批',approved:'已通过',rejected:'已驳回'}[v]||v }
function statusTag(v) { return {draft:'info',pending:'warning',approved:'success',rejected:'danger'}[v]||'' }
</script>

<style scoped>
.vehicle-detail { padding: 4px 0; }
.page-title { font-size: 16px; font-weight: 600; }

.status-banner {
  border-radius: 10px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  color: #fff;
}
.status-draft { background: linear-gradient(135deg, #909399, #b3b8bd); }
.status-pending { background: linear-gradient(135deg, #e6a23c, #f5c87a); }
.status-approved { background: linear-gradient(135deg, #67c23a, #95d475); }
.status-rejected { background: linear-gradient(135deg, #f56c6c, #f89a9a); }
.banner-title { font-size: 16px; font-weight: 600; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.info-card, .history-card { margin-bottom: 16px; border-radius: 10px; }
.section-title { font-size: 14px; font-weight: 600; color: #303133; }

.action-bar {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}
</style>
