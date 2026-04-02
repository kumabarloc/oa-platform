<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户管理</span>
        <el-button type="primary" @click="handleCreate">新增用户</el-button>
      </div>
    </template>

    <!-- 搜索栏 -->
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="用户名">
        <el-input v-model="query.userName" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="query.phone" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width:120px">
          <el-option label="正常" :value="0" />
          <el-option label="停用" :value="1" />
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
      <el-table-column prop="avatar" label="头像" width="70">
        <template #default="{ row }">
          <el-avatar :size="32">
            <img v-if="row.avatar" :src="row.avatar" @error="e => e.target.style.display='none'" />
            <span v-else>{{ row.nickName?.[0] || row.userName?.[0] || 'U' }}</span>
          </el-avatar>
        </template>
      </el-table-column>
      <el-table-column prop="userName" label="用户名" width="120" />
      <el-table-column prop="nickName" label="昵称" width="120" />
      <el-table-column prop="empNo" label="工号" width="100" />
      <el-table-column prop="deptName" label="部门" width="130" />
      <el-table-column prop="postName" label="岗位" width="100" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="160" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
            {{ row.status === 0 ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="warning" size="small" @click="handleResetPwd(row)">重置密码</el-button>
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

  <!-- 新增/编辑弹窗 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @closed="dialogClosed">
    <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
      <el-form-item label="用户名" prop="userName">
        <el-input v-model="form.userName" :disabled="!!form.id" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="form.nickName" />
      </el-form-item>
      <el-form-item label="工号" prop="empNo">
        <el-input v-model="form.empNo" />
      </el-form-item>
      <el-form-item label="部门" prop="deptId">
        <el-tree-select
          v-model="form.deptId"
          :data="deptTree"
          :props="{ label: 'deptName', value: 'id', children: 'children' }"
          check-strictly
          placeholder="请选择部门"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="岗位" prop="postId">
        <el-select v-model="form.postId" placeholder="请选择" style="width: 100%">
          <el-option v-for="p in postOptions" :key="p.id" :label="p.postName" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="角色" prop="roleIds">
        <el-select v-model="form.roleIds" multiple placeholder="请选择" style="width: 100%">
          <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :label="0">正常</el-radio>
          <el-radio :label="1">停用</el-radio>
        </el-radio-group>
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
import { getUserList, createUser, updateUser, deleteUser, resetPassword } from '@/api/system/user'
import { getDeptTree } from '@/api/dept'
import { getRoleList } from '@/api/role'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = reactive({ userName: '', phone: '', status: null, pageNum: 1, pageSize: 10 })

const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const submitting = ref(false)
const formRef = ref()
const form = reactive({
  id: null, userName: '', nickName: '', empNo: '', deptId: null, postId: null,
  roleIds: [], phone: '', email: '', status: 0,
})
const formRules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
}

const deptTree = ref([])
const roleOptions = ref([])
const postOptions = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getUserList(query)
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch {} finally { loading.value = false }
}

function resetQuery() {
  Object.assign(query, { userName: '', phone: '', status: null, pageNum: 1 })
  loadData()
}

function handleCreate() {
  dialogTitle.value = '新增用户'
  Object.assign(form, { id: null, userName: '', nickName: '', empNo: '',
    deptId: null, postId: null, roleIds: [], phone: '', email: '', status: 0 })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑用户'
  Object.assign(form, {
    id: row.id, userName: row.userName, nickName: row.nickName, empNo: row.empNo,
    deptId: row.deptId, postId: row.postId, roleIds: row.roleIds || [],
    phone: row.phone || '', email: row.email || '', status: row.status,
  })
  dialogVisible.value = true
}

function dialogClosed() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null })
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) {
      await updateUser(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createUser({ ...form, password: '123456' })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch {} finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除用户「${row.userName}」？`, '警告', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleResetPwd(row) {
  await ElMessageBox.confirm(`确认重置「${row.userName}」的密码为 123456？`, '提示', { type: 'info' })
  await resetPassword(row.id)
  ElMessage.success('密码已重置')
}

onMounted(async () => {
  loadData()
  const [deptRes, roleRes] = await Promise.allSettled([getDeptTree(), getRoleList()])
  if (deptRes.status === 'fulfilled') deptTree.value = deptRes.value.data || []
  if (roleRes.status === 'fulfilled') roleOptions.value = roleRes.value.data || []
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-bar { margin-bottom: 16px; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
