<template>
  <div class="document-list">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <span class="text-lg font-semibold">公文管理</span>
          <el-button type="primary" @click="handleCreate">新建发文</el-button>
        </div>
      </template>

      <!-- 筛选条件 -->
      <el-form inline class="mb-4">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable placeholder="全部" style="width:120px">
            <el-option label="草稿" value="draft"/>
            <el-option label="待审" value="pending"/>
            <el-option label="已发布" value="approved"/>
            <el-option label="已驳回" value="rejected"/>
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryParams.docType" clearable placeholder="全部" style="width:120px">
            <el-option label="通知" value="notice"/>
            <el-option label="简报" value="info"/>
            <el-option label="报告" value="report"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80"/>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="docType" label="类型" width="100">
          <template #default="{row}">
            <el-tag :type="docTypeTag(row.docType)">{{ docTypeLabel(row.docType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="{row}">
            <el-tag :type="priorityTag(row.priority)">{{ priorityLabel(row.priority) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="statusTag(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"/>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{row}">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status==='draft' || row.status==='rejected'" link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status==='draft' || row.status==='rejected'" link type="success" size="small" @click="handleSubmit(row)">提交</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="mt-4"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10,20,50]"
        layout="total, sizes, prev, pager, next"
        @change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDocumentList, deleteDocument, submitDocument } from '@/api/workflow/document'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({
  status: '',
  docType: '',
  pageNum: 1,
  pageSize: 10,
})

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await getDocumentList(queryParams)
    tableData.value = res.records || res.list || []
    total.value = res.total || tableData.value.length
  } catch(e) { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

function resetQuery() {
  queryParams.status = ''
  queryParams.docType = ''
  queryParams.pageNum = 1
  loadData()
}

function handleCreate() { router.push('/workflow/document/create') }
function handleEdit(row) { router.push(`/workflow/document/edit/${row.id}`) }
function handleView(row) { router.push(`/workflow/document/${row.id}`) }

async function handleSubmit(row) {
  try {
    await ElMessageBox.confirm('确认提交此公文进行审批?', '提交送审')
    const user = JSON.parse(localStorage.getItem('oa_user') || '{}')
    await submitDocument(row.id, user.id || 1)
    ElMessage.success('提交成功')
    loadData()
  } catch(e) { if (e !== 'cancel') ElMessage.error('提交失败') }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除此公文?', '删除')
    await deleteDocument(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch(e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

function docTypeLabel(v) { return {notice:'通知',info:'简报',report:'报告'}[v]||v }
function docTypeTag(v) { return {notice:'',info:'success',report:'warning'}[v]||'' }
function priorityLabel(v) { return {low:'低',normal:'普通',high:'紧急'}[v]||v }
function priorityTag(v) { return {low:'info',normal:'',high:'danger'}[v]||'' }
function statusLabel(v) { return {draft:'草稿',pending:'待审',approved:'已发布',rejected:'已驳回'}[v]||v }
function statusTag(v) { return {draft:'info',pending:'warning',approved:'success',rejected:'danger'}[v]||'' }
</script>
