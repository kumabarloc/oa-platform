<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>部门管理</span>
        <el-button type="primary" @click="handleCreate()">新增部门</el-button>
      </div>
    </template>
    <el-table :data="tableData" v-loading="loading" row-key="id" default-expand-all>
      <el-table-column prop="deptName" label="部门名称" width="200" />
      <el-table-column prop="deptCode" label="编码" width="120" />
      <el-table-column prop="deptType" label="类型" width="120">
        <template #default="{ row }">
          <el-tag>{{ deptTypeMap[row.deptType] || row.deptType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column prop="leaderName" label="负责人" width="100" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px">
    <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
      <el-form-item label="上级部门" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="deptTree"
          :props="{ label: 'deptName', value: 'id', children: 'children' }"
          check-strictly
          placeholder="顶级部门请选择 0 或留空"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="部门名称" prop="deptName">
        <el-input v-model="form.deptName" />
      </el-form-item>
      <el-form-item label="部门编码" prop="deptCode">
        <el-input v-model="form.deptCode" />
      </el-form-item>
      <el-form-item label="部门类型" prop="deptType">
        <el-select v-model="form.deptType" placeholder="请选择" style="width: 100%">
          <el-option v-for="(v, k) in deptTypeMap" :key="k" :label="v" :value="k" />
        </el-select>
      </el-form-item>
      <el-form-item label="排序" prop="sortOrder">
        <el-input-number v-model="form.sortOrder" :min="0" />
      </el-form-item>
      <el-form-item label="负责人" prop="leader">
        <el-select v-model="form.leader" placeholder="请选择" clearable style="width: 100%">
          <el-option v-for="u in userOptions" :key="u.id" :label="u.userName" :value="u.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" />
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
import { getDeptTree, createDept, updateDept, deleteDept } from '@/api/dept'
import { getUserList } from '@/api/user'

const deptTypeMap = {
  unit: '站领导',
  vice_leader: '副站长',
  engineer: '总工程师',
  director: '室主任',
  deputy_director: '副主任',
  section_chief: '科长',
}

const loading = ref(false)
const tableData = ref([])
const deptTree = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const submitting = ref(false)
const formRef = ref()
const form = reactive({ id: null, parentId: 0, deptName: '', deptCode: '', deptType: '', sortOrder: 0, leader: null, phone: '', status: 0 })
const formRules = { deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }] }
const userOptions = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await getDeptTree()
    deptTree.value = res.data || []
    // 扁平化用于表格展示
    tableData.value = flattenTree(deptTree.value)
  } finally {
    loading.value = false
  }
}

function flattenTree(nodes, result = []) {
  for (const n of nodes) {
    result.push(n)
    if (n.children?.length) flattenTree(n.children, result)
  }
  return result
}

function handleCreate() {
  dialogTitle.value = '新增部门'
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑部门'
  Object.assign(form, { ...row, parentId: row.parentId === 0 ? 0 : row.parentId })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) await updateDept(form.id, form)
    else await createDept(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除部门「${row.deptName}」？`, '警告', { type: 'warning' })
  await deleteDept(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(async () => {
  loadData()
  const res = await getUserList({ pageNum: 1, pageSize: 100 })
  userOptions.value = res.data?.list || []
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>