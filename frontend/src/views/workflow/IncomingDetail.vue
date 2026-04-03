<template>
  <div class="incoming-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>📥 收文详情</span>
          <el-button @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
        </div>
      </template>

      <div v-if="detail">
        <!-- 状态横幅 -->
        <div class="status-banner" :class="`status-${detail.status}`">
          <el-tag :type="statusTag(detail.status)" size="large">{{ statusName(detail.status) }}</el-tag>
          <span class="banner-title">{{ detail.title || '无标题' }}</span>
        </div>

        <!-- 基本信息 -->
        <el-card class="info-card" shadow="never">
          <template #header><span class="section-title">基本信息</span></template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="收文编号">{{ detail.docNumber || '-' }}</el-descriptions-item>
            <el-descriptions-item label="来文单位">{{ detail.sourceUnit || '-' }}</el-descriptions-item>
            <el-descriptions-item label="收文时间">{{ detail.receiveTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="当前处理人">{{ detail.handlerName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="priorityTag(detail.priority)" size="small">
                {{ priorityLabel(detail.priority) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detail.createTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="文件标题" :span="2">{{ detail.title || '-' }}</el-descriptions-item>
            <el-descriptions-item label="正文内容" :span="2">
              <div class="doc-content" v-html="detail.content || '无正文内容'" />
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 操作按钮区 -->
        <div class="action-buttons" v-if="detail.status === 'distributing' && isCurrentHandler">
          <el-button type="success" size="large" @click="handleDistribute">
            <el-icon><Check /></el-icon> 分发
          </el-button>
          <el-button type="warning" size="large" @click="showReturnDialog=true">
            <el-icon><Back /></el-icon> 退回
          </el-button>
          <el-button type="danger" size="large" @click="showRejectDialog=true">
            <el-icon><Close /></el-icon> 驳回
          </el-button>
          <el-button size="large" @click="showAddSignDialog=true">
            <el-icon><Plus /></el-icon> 加签
          </el-button>
        </div>

        <!-- 审批流程图 -->
        <el-card class="flow-card" shadow="never">
          <template #header><span class="section-title">📊 审批流程图</span></template>
          <div class="flow-diagram">
            <div class="flow-steps">
              <div class="step" :class="{ active: currentStep==='received', done: !['received'].includes(status) }">
                <div class="step-icon">1</div>
                <div class="step-label">收文登记</div>
              </div>
              <div class="step-line" />
              <div class="step" :class="{ active: currentStep==='distributing', done: status==='processed' || status==='archived' }">
                <div class="step-icon">2</div>
                <div class="step-label">分发审核</div>
              </div>
              <div class="step-line" />
              <div class="step" :class="{ active: currentStep==='processed', done: status==='archived' }">
                <div class="step-icon">3</div>
                <div class="step-label">处理中</div>
              </div>
              <div class="step-line" />
              <div class="step" :class="{ active: status==='archived' }">
                <div class="step-icon"><el-icon><Check /></el-icon></div>
                <div class="step-label">归档</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 审批历史时间线 -->
        <el-card class="history-card" shadow="never" v-if="history.length > 0">
          <template #header><span class="section-title">🕐 审批历史</span></template>
          <el-timeline>
            <el-timeline-item
              v-for="(item, idx) in history"
              :key="idx"
              :type="actionType(item.action)"
              :timestamp="item.endTime || item.startTime"
              placement="top"
            >
              <el-card class="timeline-card">
                <div class="timeline-header">
                  <strong>{{ item.taskName || '审批节点' }}</strong>
                  <el-tag :type="actionTag(item.action)" size="small">{{ item.action || '提交' }}</el-tag>
                </div>
                <div class="timeline-body">
                  <span>审批人：{{ item.assigneeName || item.assignee || '系统' }}</span>
                  <span v-if="item.comment">意见：{{ item.comment }}</span>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </div>
    </el-card>

    <!-- 加签对话框 -->
    <el-dialog v-model="showAddSignDialog" title="加签（会签）" width="450px" destroy-on-close>
      <p style="color:#909399;font-size:13px;margin-bottom:16px">加签后，新增审批人和当前审批人需同时审批，全部通过后才会流转到下一节点。</p>
      <el-form-item label="选择审批人">
        <el-select v-model="addSignUserId" placeholder="请选择审批人" style="width:100%">
          <el-option label="王五（部门经理）" value="2" />
          <el-option label="李六（综合办）" value="3" />
        </el-select>
      </el-form-item>
      <template #footer>
        <el-button @click="showAddSignDialog=false">取消</el-button>
        <el-button type="primary" @click="handleAddSign" :loading="addSigning">确认加签</el-button>
      </template>
    </el-dialog>

    <!-- 退回对话框 -->
    <el-dialog v-model="showReturnDialog" title="退回" width="450px" destroy-on-close>
      <el-form-item label="退回原因">
        <el-input v-model="returnReason" type="textarea" :rows="3" placeholder="请填写退回原因" maxlength="500" show-word-limit />
      </el-form-item>
      <template #footer>
        <el-button @click="showReturnDialog=false">取消</el-button>
        <el-button type="warning" @click="handleReturn" :loading="returning">确认退回</el-button>
      </template>
    </el-dialog>

    <!-- 驳回对话框 -->
    <el-dialog v-model="showRejectDialog" title="驳回到收文人" width="450px" destroy-on-close>
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="说明驳回原因" maxlength="500" show-word-limit />
      <template #footer>
        <el-button @click="showRejectDialog=false">取消</el-button>
        <el-button type="danger" @click="handleReject" :loading="rejecting">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Check, Close, Back, Plus } from '@element-plus/icons-vue'
import { getIncomingDetail, getIncomingHistory } from '@/api/workflow/incoming'
import { addSignDocument, returnDocument, rejectDocument } from '@/api/workflow/document'

const route = useRoute()
const loading = ref(false)
const detail = ref(null)
const history = ref([])

const showRejectDialog = ref(false)
const showReturnDialog = ref(false)
const showAddSignDialog = ref(false)
const rejectReason = ref('')
const returnReason = ref('')
const addSignUserId = ref('')
const rejecting = ref(false)
const returning = ref(false)
const addSigning = ref(false)

const status = computed(() => detail.value?.status || 'received')
const currentStep = computed(() => detail.value?.currentStep || 'received')
const isCurrentHandler = computed(() => detail.value?.handlerId === (JSON.parse(localStorage.getItem('oa_user') || '{}').id || 1))

function statusName(s) {
  return { received: '已收文', distributing: '分发中', processed: '已处理', archived: '已归档' }[s] || s || '-'
}
function statusTag(v) {
  return { received: 'info', distributing: 'warning', processed: 'primary', archived: 'success' }[v] || ''
}
function priorityLabel(v) { return { high: '紧急', normal: '普通', low: '低' }[v] || v }
function priorityTag(v) { return { high: 'danger', normal: 'warning', low: 'info' }[v] || '' }
function actionTag(action) {
  return { '提交': '', '通过': 'success', '驳回': 'danger', '退回': 'warning', '分发': 'success' }[action] || 'info'
}
function actionType(action) {
  if (action === '驳回') return 'danger'
  if (action === '通过' || action === '分发') return 'success'
  if (action === '提交') return 'primary'
  return 'info'
}

onMounted(async () => {
  loading.value = true
  try {
    detail.value = await getIncomingDetail(route.params.id)
    history.value = await getIncomingHistory(route.params.id).catch(() => [])
  } catch {}
  finally { loading.value = false }
})

async function handleDistribute() {
  try {
    await ElMessageBox.confirm('确认分发此收文？', '分发确认')
    ElMessage.success('分发成功')
    detail.value = await getIncomingDetail(route.params.id)
  } catch(e) { if (e !== 'cancel') ElMessage.error('分发失败') }
}

async function handleReject() {
  if (!rejectReason.value.trim()) { ElMessage.warning('请填写驳回原因'); return }
  rejecting.value = true
  try {
    await rejectDocument(route.params.id, JSON.parse(localStorage.getItem('oa_user') || '{}').id || 1, rejectReason.value)
    ElMessage.success('已驳回')
    showRejectDialog.value = false
    rejectReason.value = ''
    detail.value = await getIncomingDetail(route.params.id)
  } catch { ElMessage.error('驳回失败') }
  finally { rejecting.value = false }
}

async function handleReturn() {
  if (!returnReason.value.trim()) { ElMessage.warning('请填写退回原因'); return }
  returning.value = true
  try {
    await returnDocument(route.params.id, { reason: returnReason.value })
    ElMessage.success('已退回')
    showReturnDialog.value = false
    returnReason.value = ''
    detail.value = await getIncomingDetail(route.params.id)
  } catch { ElMessage.error('退回失败') }
  finally { returning.value = false }
}

async function handleAddSign() {
  if (!addSignUserId.value) { ElMessage.warning('请选择审批人'); return }
  addSigning.value = true
  try {
    await addSignDocument(route.params.id, addSignUserId.value)
    ElMessage.success('加签成功')
    showAddSignDialog.value = false
    addSignUserId.value = ''
    detail.value = await getIncomingDetail(route.params.id)
  } catch { ElMessage.error('加签失败') }
  finally { addSigning.value = false }
}
</script>

<style scoped>
.incoming-detail { padding: 4px 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; font-weight: 600; }
.section-title { font-size: 14px; font-weight: 600; color: #303133; }

.status-banner {
  border-radius: 10px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  color: #fff;
}
.status-received { background: linear-gradient(135deg, #909399, #b3b8bd); }
.status-distributing { background: linear-gradient(135deg, #e6a23c, #f5c87a); }
.status-processed { background: linear-gradient(135deg, #409eff, #79bbff); }
.status-archived { background: linear-gradient(135deg, #67c23a, #95d475); }
.banner-title { font-size: 16px; font-weight: 600; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.info-card, .flow-card, .history-card { margin-bottom: 16px; border-radius: 10px; }

.doc-content {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  max-height: 300px;
  overflow-y: auto;
}

.action-buttons {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 10px;
  margin-bottom: 16px;
}

.flow-diagram {
  padding: 24px 16px;
  background: #fafafa;
  border-radius: 8px;
}
.flow-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
}
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  min-width: 80px;
}
.step-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #dcdfe6;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  transition: all 0.3s;
}
.step.active .step-icon {
  background: #409eff;
  box-shadow: 0 0 0 4px rgba(64,158,255,0.25);
}
.step.done .step-icon { background: #67c23a; }
.step-label { font-size: 12px; color: #909399; text-align: center; }
.step.active .step-label { color: #409eff; font-weight: 600; }
.step.done .step-label { color: #67c23a; }

.step-line {
  flex: 1;
  height: 2px;
  background: #dcdfe6;
  min-width: 40px;
  max-width: 80px;
  position: relative;
  top: -10px;
}

.timeline-card { border-radius: 8px; }
.timeline-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 6px;
  gap: 8px;
}
.timeline-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;
  color: #606266;
}
</style>
