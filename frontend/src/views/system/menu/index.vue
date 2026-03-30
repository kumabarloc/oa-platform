<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>菜单管理</span>
        <el-button type="primary" @click="handleCreate()">新增菜单</el-button>
      </div>
    </template>
    <el-table :data="tableData" v-loading="loading" row-key="id" default-expand-all>
      <el-table-column prop="menuName" label="菜单名称" width="180" />
      <el-table-column prop="icon" label="图标" width="80" align="center">
        <template #default="{ row }">
          <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="menuType" label="类型" width="80" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.menuType === 'M'">目录</el-tag>
          <el-tag v-else-if="row.menuType === 'C'" type="success">菜单</el-tag>
          <el-tag v-else type="info">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由" min-width="150" />
      <el-table-column prop="component" label="组件" min-width="180" />
      <el-table-column prop="perms" label="权限标识" min-width="150" />
      <el-table-column prop="visible" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.visible === 0 ? 'success' : 'danger'">{{ row.visible === 0 ? '显示' : '隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
    <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
      <el-form-item label="上级菜单" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="menuTree"
          :props="{ label: 'menuName', value: 'id', children: 'children' }"
          check-strictly
          placeholder="顶级菜单请选择 0"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="菜单类型" prop="menuType">
        <el-radio-group v-model="form.menuType">
          <el-radio label="M">目录</el-radio>
          <el-radio label="C">菜单</el-radio>
          <el-radio label="F">按钮</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="菜单名称" prop="menuName">
        <el-input v-model="form.menuName" />
      </el-form-item>
      <el-form-item label="路由地址" prop="path" v-if="form.menuType !== 'F'">
        <el-input v-model="form.path" placeholder="如: /system/user" />
      </el-form-item>
      <el-form-item label="组件路径" prop="component" v-if="form.menuType === 'C'">
        <el-input v-model="form.component" placeholder="如: system/user/index" />
      </el-form-item>
      <el-form-item label="权限字符" prop="perms" v-if="form.menuType === 'F'">
        <el-input v-model="form.perms" placeholder="如: system:user:add" />
      </el-form-item>
      <el-form-item label="菜单图标" prop="icon" v-if="form.menuType !== 'F'">
        <el-input v-model="form.icon" placeholder="Element Plus 图标名" />
      </el-form-item>
      <el-form-item label="排序" prop="sortOrder">
        <el-input-number v-model="form.sortOrder" :min="0" />
      </el-form-item>
      <el-form-item label="状态" prop="visible">
        <el-radio-group v-model="form.visible">
          <el-radio :label="0">显示</el-radio>
          <el-radio :label="1">隐藏</el-radio>
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
import request from '@/utils/request'

// 菜单接口 - 使用通用的 request
async function getMenuTree() {
  return request({ url: '/system/menu/tree', method: 'get' })
}
async function createMenu(data) {
  return request({ url: '/system/menu', method: 'post', data })
}
async function updateMenu(id, data) {
  return request({ url: `/system/menu/${id}`, method: 'put', data })
}
async function deleteMenu(id) {
  return request({ url: `/system/menu/${id}`, method: 'delete' })
}

const loading = ref(false)
const tableData = ref([])
const menuTree = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const submitting = ref(false)
const formRef = ref()
const form = reactive({ id: null, parentId: 0, menuType: 'C', menuName: '', path: '', component: '', perms: '', icon: '', sortOrder: 0, visible: 0 })
const formRules = { menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const res = await getMenuTree()
    menuTree.value = [{ id: 0, menuName: '顶级菜单', children: res.data || [] }]
    tableData.value = flattenTree(res.data || [])
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
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, { ...row, parentId: row.parentId === 0 ? 0 : row.parentId })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (form.id) await updateMenu(form.id, form)
    else await createMenu(form)
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确认删除菜单「${row.menuName}」？`, '警告', { type: 'warning' })
  await deleteMenu(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>