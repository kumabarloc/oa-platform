<template>
  <div class="document-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="flex justify-between items-center">
          <span>公文详情</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <div v-if="doc">
        <el-descriptions :column="2" border class="mb-6">
          <el-descriptions-item label="标题">{{ doc.title }}</el-descriptions-item>
          <el-descriptions-item label="类型"><el-tag>{{ docTypeLabel(doc.docType) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="优先级"><el-tag :type="priorityTag(doc.priority)">{{ priorityLabel(doc.priority) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="状态"><el-tag :type="statusTag(doc.status)">{{ statusLabel(doc.status) }}</el-tag></el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ doc.createTime }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ doc.publishTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前步骤" :span="2">{{ doc.currentStep || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ doc.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="mb-6">
          <div class="font-semibold mb-2">正文内容</div>
          <el-card shadow="never"><div v-html="doc.content || '暂无正文内容'"/></el-card>
        </div>

        <div v-if="history.length > 0">
          <div class="font-semibold mb-4">审批历史</div>
          <el-table :data="history" stripe size="small">
            <el-table-column prop="taskName" label="审批节点" width="160"/>
            <el-table-column prop="assignee" label="审批人" width="120"/>
            <el-table-column prop="startTime" label="开始时间" width="170"/>
            <el-table-column prop="endTime" label="结束时间" width="170"/>
            <el-table-column prop="deleteReason" label="审批结果">
              <template #default="{row}">{{ row.deleteReason || '通过' }}</template>
            </el-table-column>
          </el-table>
        </div>

        <div class="mt-4 flex gap-2" v-if="doc.status === 'pending'">
          <el-button type="success" @click="handleApprove">通过</el-button>
          <el-button type="danger" @click="showRejectDialog=true">驳回</el-button>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="showRejectDialog" title="驳回原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请输入驳回原因"/>
      <template #footer>
        <el-button @click="showRejectDialog=false">取消</el-button>
        <el-button type="danger" @click="handleReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDocumentDetail, getApprovalHistory, approveDocument, rejectDocument } from '@/api/workflow/document'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const doc = ref(null)
const history = ref([])
const showRejectDialog = ref(false)
const rejectReason = ref('')

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
    await ElMessageBox.confirm('确认审批通过?', '审批')
    const user = JSON.parse(localStorage.getItem('oa_user') || '{}')
    await approveDocument(route.params.id, user.id || 1)
    ElMessage.success('审批成功')
    doc.value = await getDocumentDetail(route.params.id)
    history.value = await getApprovalHistory(route.params.id)
  } catch(e) { if (e !== 'cancel') ElMessage.error('审批失败') }
}

async function handleReject() {
  try {
    const user = JSON.parse(localStorage.getItem('oa_user') || '{}')
    await rejectDocument(route.params.id, user.id || 1, rejectReason.value)
    ElMessage.success('已驳回')
    showRejectDialog.value = false
    doc.value = await getDocumentDetail(route.params.id)
    history.value = await getApprovalHistory(route.params.id)
  } catch { ElMessage.error('驳回失败') }
}

function handleBack() { router.back() }
function docTypeLabel(v) { return {notice:'通知',info:'简报',report:'报告'}[v]||v }
function priorityLabel(v) { return {low:'低',normal:'普通',high:'紧急'}[v]||v }
function priorityTag(v) { return {low:'info',normal:'',high:'danger'}[v]||'' }
function statusLabel(v) { return {draft:'草稿',pending:'待审',approved:'已发布',rejected:'已驳回'}[v]||v }
function statusTag(v) { return {draft:'info',pending:'warning',approved:'success',rejected:'danger'}[v]||'' }
</script>
