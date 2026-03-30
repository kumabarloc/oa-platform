<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6">
        <div class="stat-card blue">
          <div class="stat-icon"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-num">42</div>
            <div class="stat-label">用户总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card green">
          <div class="stat-icon"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <div class="stat-num">18</div>
            <div class="stat-label">待审批公文</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card orange">
          <div class="stat-icon"><el-icon><Clock /></el-icon></div>
          <div class="stat-info">
            <div class="stat-num">7</div>
            <div class="stat-label">我待办</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card purple">
          <div class="stat-icon"><el-icon><Bell /></el-icon></div>
          <div class="stat-info">
            <div class="stat-num">23</div>
            <div class="stat-label">未读通知</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="16">
        <el-card>
          <template #header><span>近期公文</span></template>
          <el-table :data="recentDocs" style="width: 100%">
            <el-table-column prop="documentNo" label="公文编号" width="160" />
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="type" label="类型" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)">{{ row.statusText }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>快捷操作</span></template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/document/dispatch/create')">新建发文</el-button>
            <el-button @click="$router.push('/document/receive/create')">登记收文</el-button>
          </div>
        </el-card>
        <el-card style="margin-top: 16px">
          <template #header><span>通知公告</span></template>
          <div class="notice-list">
            <div v-for="n in notices" :key="n.id" class="notice-item">
              <span class="notice-title">{{ n.title }}</span>
              <span class="notice-date">{{ n.date }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { User, Document, Clock, Bell } from '@element-plus/icons-vue'

const recentDocs = ref([
  { documentNo: 'FW-20260330-001', title: '关于开展年度考核的通知', type: '发文', status: 'pending', statusText: '审批中', createTime: '2026-03-30 10:23' },
  { documentNo: 'SW-20260330-002', title: '关于xxx事项的函', type: '收文', status: 'approved', statusText: '已通过', createTime: '2026-03-30 09:15' },
  { documentNo: 'FW-20260329-003', title: '关于调整作息时间的通知', type: '发文', status: 'draft', statusText: '草稿', createTime: '2026-03-29 16:40' },
])

const notices = ref([
  { id: 1, title: '清明节放假通知', date: '03-30' },
  { id: 2, title: '系统升级维护公告', date: '03-28' },
  { id: 3, title: '第一季度工作总结会议', date: '03-25' },
])

function statusType(status) {
  const map = { draft: 'info', pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}
</script>

<style scoped>
.dashboard {}
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  color: #fff;
}
.stat-card.blue { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-card.green { background: linear-gradient(135deg, #11998e, #38ef7d); }
.stat-card.orange { background: linear-gradient(135deg, #f093fb, #f5576c); }
.stat-card.purple { background: linear-gradient(135deg, #4facfe, #00f2fe); }
.stat-icon { font-size: 36px; opacity: 0.8; margin-right: 16px; }
.stat-num { font-size: 28px; font-weight: bold; }
.stat-label { font-size: 14px; opacity: 0.85; margin-top: 4px; }
.quick-actions { display: flex; gap: 10px; flex-wrap: wrap; }
.notice-list .notice-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}
.notice-item:last-child { border-bottom: none; }
.notice-title { color: #333; cursor: pointer; }
.notice-title:hover { color: #409eff; }
.notice-date { color: #999; }
</style>