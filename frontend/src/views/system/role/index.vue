<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>角色管理</span>
        <el-button type="primary" @click="handleCreate">新增角色</el-button>
      </div>
    </template>
    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleKey" label="权限字符" width="150" />
      <el-table-column prop="dataScope" label="数据范围" width="120">
        <template #default="{ row }">
          {{ dataScopeMap[row.dataScope] || row.dataScope }}
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="primary" @click="handleAssignMenus(row)">分配菜单</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" :disabled="!!form.id" placeholder="如: system:user:list" />
        </el-form-item>
        <el-form-item label="数据范围" prop="dataScope">
          <el-select v-model="form.dataScope" style="width: 100%">
            <el-option v-for="(v, k) in dataScopeMap" :key="k" :label="v" :value="k" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="450px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        node-key="id"
        :default-checked-keys="checkedMenuIds"
        show-checkbox
        style="max-height: 400px; overflow-y: auto"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit" :loading="menuSubmitting">确定</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, getRoleMenus, assignMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'

const dataScopeMap = { '1': '全部', '2': '本部门及以下', '3': '本部门', '4': '仅本人', '5': '自定义' }

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const submitting = ref(false)
const formRef = ref()
const form = reactive({ id: null, roleName: '', roleKey: '', dataScope: '1', sortOrder: 0, status: 0, remark: '' })
const formRules = { roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }], roleKey: [{ required: true, message: '请输入权限字符', trigger: 'blur' }] }

const menuDialogVisible = ref(false)
const menuTree = ref([])
const checkedMenuIds = ref([])
const menuTreeRef = ref()
const menuSubmitting = ref(false)
const currentRoleId = ref(null)

async function loadData() {
  loading.value = true
  try {
    const res = await getRoleList()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

function handleCreate() {
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑角色'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) await updateRole(form.id, form)
    else await createRole(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除角色「${row.roleName}」？`, '警告', { type: 'warning' })
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleAssignMenus(row) {
  currentRoleId.value = row.id
  try {
    const menuRes = await getMenuTree()
    menuTree.value = menuRes.data || []
    
    const roleMenuRes = await getRoleMenus(row.id)
    checkedMenuIds.value = roleMenuRes.data || []
  } catch (e) {
    console.error('加载菜单失败', e)
  }
  menuDialogVisible.value = true
}

async function handleMenuSubmit() {
  const ids = menuTreeRef.value.getCheckedKeys().concat(menuTreeRef.value.getHalfCheckedKeys())
  await assignMenus(currentRoleId.value, ids)
  ElMessage.success('菜单分配成功')
  menuDialogVisible.value = false
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>