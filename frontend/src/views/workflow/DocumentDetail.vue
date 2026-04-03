<template>
  <div class="document-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="flex justify-between items-center">
          <span class="page-title">📄 公文详情</span>
          <el-button @click="handleBack">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
        </div>
      </template>

      <div v-if="doc">
        <!-- 状态横幅 -->
        <div class="status-banner" :class="`status-${doc.status}`">
          <el-tag :type="statusTag(doc.status)" size="large">{{ statusLabel(doc.status) }}</el-tag>
          <span class="banner-title">{{ doc.title }}</span>
          <span class="banner-meta" v-if="doc.currentStep">当前环节：{{ doc.currentStep }}</span>
        </div>

        <!-- 公文信息卡片 -->
        <el-card class="info-card" shadow="never">
          <template #header><span class="section-title">基本信息</span></template>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="类型"><el-tag size="small">{{ docTypeLabel(doc.docType) }}</el-tag></el-descriptions-item>
            <el-descriptions-item label="优先级">
              <el-tag :type="priorityTag(doc.priority)" size="small">{{ priorityLabel(doc.priority) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ doc.createTime }}</el-descriptions-item>
            <el-descriptions-item label="创建部门">{{ doc.deptName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="拟稿人">{{ doc.draftUserName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="发布时间">{{ doc.publishTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="3">{{ doc.remark || '无' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 正文内容 -->
        <el-card class="content-card" shadow="never" v-if="doc.content">
          <template #header><span class="section-title">正文内容</span></template>
          <div class="doc-content" v-html="doc.content" />
        </el-card>

        <!-- 操作按钮区 -->
        <div class="action-buttons" v-if="doc.status === 'pending' && isCurrentApprover">
          <el-button type="success" size="large" @click="handleApprove">
            <el-icon><Check /></el-icon> 批准
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
              <div class="step" :class="{ active: currentStep==='draft', done: status!=='draft' }">
                <div class="step-icon">1</div>
                <div class="step-label">起草</div>
              </div>
              <div class="step-line" />
              <div class="step" :class="{ active: currentStep==='dept', done: ['office','approved'].includes(status) }">
                <div class="step-icon">2</div>
                <div class="step-label">部门审核</div>
              </div>
              <div class="step-line" />
              <div class="step" :class="{ active: currentStep==='office', done: status==='approved' }">
                <div class="step-icon">3</div>
                <div class="step-label">综合办审核</div>
              </div>
              <div class="step-line" />
              <div class="step" :class="{ active: status==='approved' }">
                <div class="step-icon"><el-icon><Check /></el-icon></div>
                <div class="step-label">发布</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 审批历史时间线 -->
        <el-card class="history-card" shadow="never" v-if="history.length > 0">
          <template #header>
            <div class="section-title">🕐 审批历史</div>
          </template>
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
    <el-dialog v-model="showRejectDialog" title="驳回到拟稿人" width="450px" destroy-on-close>
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
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Check, Close, Back, Plus, Loading } from '@element-plus/icons-vue'
import { getDocumentDetail, getApprovalHistory, approveDocument, rejectDocument, addSignDocument, returnDocument } from '@/api/workflow/document'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const doc = ref(null)
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

const status = computed(() => doc.value?.status || 'draft')
const currentStep = computed(() => doc.value?.currentStep || 'draft')
const isCurrentApprover = computed(() => doc.value?.pendingApproverId === (JSON.parse(localStorage.getItem('oa_user') || '{}').id || 1))

onMounted(async () => {
  loading.value = true
  try {
    doc.value = await getDocumentDetail(route.params.id)
    history.value = await getApprovalHistory(route.params.id)
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
})

async function handleApprove() {
  try {
    await ElMessageBox.confirm('确认审批通过该公文？', '审批确认')
    const user = JSON.parse(localStorage.getItem('oa_user') || '{}')
    await approveDocument(route.params.id, user.id || 1)
    ElMessage.success('审批通过！')
    doc.value = await getDocumentDetail(route.params.id)
    history.value = await getApprovalHistory(route.params.id)
  } catch(e) { if (e !== 'cancel') ElMessage.error('审批失败') }
}

async function handleReject() {
  if (!rejectReason.value.trim()) { ElMessage.warning('请填写驳回原因'); return }
  rejecting.value = true
  try {
    const user = JSON.parse(localStorage.getItem('oa_user') || '{}')
    await rejectDocument(route.params.id, user.id || 1, rejectReason.value)
    ElMessage.success('已驳回')
    showRejectDialog.value = false
    rejectReason.value = ''
    doc.value = await getDocumentDetail(route.params.id)
    history.value = await getApprovalHistory(route.params.id)
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
    doc.value = await getDocumentDetail(route.params.id)
    history.value = await getApprovalHistory(route.params.id)
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
    doc.value = await getDocumentDetail(route.params.id)
    history.value = await getApprovalHistory(route.params.id)
  } catch { ElMessage.error('加签失败') }
  finally { addSigning.value = false }
}

function handleBack() { router.back() }

function docTypeLabel(v) { return {notice:'通知',info:'简报',report:'报告'}[v]||v }
function priorityLabel(v) { return {low:'低',normal:'普通',high:'紧急'}[v]||v }
function priorityTag(v) { return {low:'info',normal:'',high:'danger'}[v]||'' }
function statusLabel(v) { return {draft:'草稿',pending:'待审',approved:'已发布',rejected:'已驳回'}[v]||v }
function statusTag(v) { return {draft:'info',pending:'warning',approved:'success',rejected:'danger'}[v]||'' }
function actionTag(action) {
  return { '提交': '', '通过': 'success', '驳回': 'danger', '退回': 'warning', '通过': 'success' }[action] || 'info'
}
function actionType(action) {
  if (action === '驳回') return 'danger'
  if (action === '通过') return 'success'
  if (action === '提交') return 'primary'
  return 'info'
}
</script>

<style scoped>
.document-detail { padding: 4px 0; }
.page-title { font-size: 16px; font-weight: 600; }

/* 状态横幅 */
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
.banner-meta { font-size: 13px; opacity: 0.9; white-space: nowrap; }

/* 信息卡片 */
.info-card, .content-card, .history-card, .flow-card { margin-bottom: 16px; border-radius: 10px; }
.section-title { font-size: 14px; font-weight: 600; color: #303133; }

/* 正文样式 */
.doc-content {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  max-height: 400px;
  overflow-y: auto;
  padding: 8px;
}

/* 操作按钮区 */
.action-buttons {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 10px;
  margin-bottom: 16px;
}

/* 审批流程图 */
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
.step.done .step-icon {
  background: #67c23a;
}
.step-label {
  font-size: 12px;
  color: #909399;
  text-align: center;
}
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

/* 时间线卡片 */
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
