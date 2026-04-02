<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>通知公告</span>
        <el-button type="primary" @click="handleCreate">发布通知</el-button>
      </div>
    </template>

    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="标题">
        <el-input v-model="query.title" placeholder="请输入标题" clearable style="width:200px" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="query.type" placeholder="全部" clearable style="width:140px">
          <el-option label="系统通知" value="system" />
          <el-option label="普通通知" value="normal" />
          <el-option label="重要通知" value="important" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.type)" size="small">{{ typeName(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{ row }">
          <el-tag :type="row.priority === 'high' ? 'danger' : row.priority === 'low' ? 'info' : 'warning'" size="small">
            {{ {high:'紧急', normal:'普通', low:'低'}[row.priority] || '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="senderName" label="发布人" width="100" />
      <el-table-column prop="readStatus" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.readStatus === 1 ? 'success' : 'danger'" size="small">
            {{ row.readStatus === 1 ? '已读' : '未读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
          <el-button link type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="loadData"
      />
    </div>
  </el-card>

  <!-- 查看详情弹窗 -->
  <el-dialog v-model="viewVisible" title="通知详情" width="600px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="标题">{{ viewData.title }}</el-descriptions-item>
      <el-descriptions-item label="类型">{{ typeName(viewData.type) }}</el-descriptions-item>
      <el-descriptions-item label="优先级">{{ {high:'紧急',normal:'普通',low:'低'}[viewData.priority] }}</el-descriptions-item>
      <el-descriptions-item label="发布人">{{ viewData.senderName }}</el-descriptions-item>
      <el-descriptions-item label="发布时间">{{ viewData.createTime }}</el-descriptions-item>
      <el-descriptions-item label="内容">
        <div v-html="viewData.content"></div>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="viewVisible = false">关闭</el-button>
      <el-button v-if="viewData.readStatus === 0" type="primary" @click="handleMarkRead(viewData.id)">标记已读</el-button>
    </template>
  </el-dialog>

  <!-- 发布/编辑通知弹窗 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
    <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入通知标题" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="form.type" placeholder="请选择" style="width:100%">
          <el-option label="系统通知" value="system" />
          <el-option label="普通通知" value="normal" />
          <el-option label="重要通知" value="important" />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-select v-model="form.priority" placeholder="请选择" style="width:100%">
          <el-option label="紧急" value="high" />
          <el-option label="普通" value="normal" />
          <el-option label="低" value="low" />
        </el-select>
      </el-form-item>
      <el-form-item label="推送范围" prop="targetType">
        <el-select v-model="form.targetType" placeholder="请选择" style="width:100%">
          <el-option label="全部用户" value="all" />
          <el-option label="指定部门" value="dept" />
          <el-option label="指定用户" value="user" />
        </el-select>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入通知内容" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNotificationList, getNotificationDetail, createNotification,
  updateNotification, deleteNotification, markRead
} from '@/api/system/notification'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ title: '', type: '', pageNum: 1, pageSize: 10 })

const dialogVisible = ref(false)
const dialogTitle = ref('发布通知')
const submitting = ref(false)
const formRef = ref()
const form = reactive({ id: null, title: '', type: 'system', priority: 'normal', targetType: 'all', content: '' })
const formRules = { title: [{ required: true, message: '请输入标题', trigger: 'blur' }] }

const viewVisible = ref(false)
const viewData = ref({})

function typeName(t) { return { system:'系统通知', normal:'普通通知', important:'重要通知' }[t] || t }
function typeTag(t) { return { system:'primary', normal:'', important:'danger' }[t] || '' }

async function loadData() {
  loading.value = true
  try {
    const res = await getNotificationList(query)
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch {} finally { loading.value = false }
}

function resetQuery() { Object.assign(query, { title: '', type: '', pageNum: 1 }); loadData() }

function handleCreate() {
  dialogTitle.value = '发布通知'
  Object.assign(form, { id: null, title: '', type: 'system', priority: 'normal', targetType: 'all', content: '' })
  dialogVisible.value = true
}

async function handleEdit(row) {
  const res = await getNotificationDetail(row.id)
  dialogTitle.value = '编辑通知'
  Object.assign(form, { id: res.id, title: res.title, type: res.type, priority: res.priority || 'normal', targetType: res.targetType || 'all', content: res.content || '' })
  dialogVisible.value = true
}

async function handleView(row) {
  const res = await getNotificationDetail(row.id)
  viewData.value = res
  viewVisible.value = true
  if (row.readStatus === 0) await markRead(row.id)
}

async function handleMarkRead(id) { await markRead(id); loadData(); viewVisible.value = false }

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) { await updateNotification(form.id, form); ElMessage.success('更新成功') }
    else { await createNotification(form); ElMessage.success('发布成功') }
    dialogVisible.value = false; loadData()
  } catch {} finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除通知「${row.title}」？`, '警告', { type: 'warning' })
  await deleteNotification(row.id)
  ElMessage.success('删除成功'); loadData()
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display:flex; justify-content:space-between; align-items:center; }
.search-bar { margin-bottom:16px; }
.pagination-wrap { display:flex; justify-content:flex-end; margin-top:16px; }
</style>
