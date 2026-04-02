<template>
  <el-card>
    <template #header><span>登录日志</span></template>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="用户名"><el-input v-model="query.username" placeholder="请输入" clearable style="width:180px" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width:120px">
          <el-option label="成功" value="0" /><el-option label="失败" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" @click="loadData">查询</el-button><el-button @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="ip" label="IP地址" width="140" />
      <el-table-column prop="location" label="登录地点" width="150" />
      <el-table-column prop="browser" label="浏览器" width="120" />
      <el-table-column prop="os" label="操作系统" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === '0' ? 'success' : 'danger'" size="small">{{ row.status === '0' ? '成功' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="msg" label="消息" min-width="150" show-overflow-tooltip />
      <el-table-column prop="loginTime" label="登录时间" width="170" />
    </el-table>
    <div class="pagination-wrap">
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </div>
  </el-card>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLoginLogList } from '@/api/system/log'
const loading = ref(false), tableData = ref([]), total = ref(0)
const query = reactive({ username: '', status: '', pageNum: 1, pageSize: 10 })
async function loadData() { loading.value = true; try { const res = await getLoginLogList(query); tableData.value = res.list||[]; total.value = res.total||0 } catch {} finally { loading.value = false } }
function resetQuery() { Object.assign(query, { username:'', status:'', pageNum:1 }); loadData() }
onMounted(loadData)
</script>
<style scoped>.search-bar{margin-bottom:16px}.pagination-wrap{display:flex;justify-content:flex-end;margin-top:16px}</style>
