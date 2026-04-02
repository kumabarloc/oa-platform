<template>
  <el-card>
    <template #header><span>操作日志</span></template>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="操作模块"><el-input v-model="query.title" placeholder="请输入" clearable style="width:180px" /></el-form-item>
      <el-form-item label="操作人"><el-input v-model="query.operName" placeholder="请输入" clearable style="width:150px" /></el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button><el-button @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="操作模块" width="140" show-overflow-tooltip />
      <el-table-column prop="businessType" label="业务类型" width="100" />
      <el-table-column prop="operName" label="操作人" width="100" />
      <el-table-column prop="deptName" label="部门" width="120" />
      <el-table-column prop="requestMethod" label="请求方式" width="90">
        <template #default="{ row }">
          <el-tag size="small" :type="row.requestMethod==='GET'?'success':row.requestMethod==='POST'?'primary':'warning'">{{ row.requestMethod }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="url" label="请求地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="130" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">{{ row.status === 0 ? '正常' : '异常' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operTime" label="操作时间" width="170" />
    </el-table>
    <div class="pagination-wrap">
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </div>
  </el-card>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOperationLogList } from '@/api/system/log'
const loading = ref(false), tableData = ref([]), total = ref(0)
const query = reactive({ title: '', operName: '', pageNum: 1, pageSize: 10 })
async function loadData() { loading.value = true; try { const res = await getOperationLogList(query); tableData.value = res.list||[]; total.value = res.total||0 } catch {} finally { loading.value = false } }
function resetQuery() { Object.assign(query, { title:'', operName:'', pageNum:1 }); loadData() }
onMounted(loadData)
</script>
<style scoped>.search-bar{margin-bottom:16px}.pagination-wrap{display:flex;justify-content:flex-end;margin-top:16px}</style>
