<template>
  <el-card>
    <template #header><div class="card-header"><span>收文详情</span><el-button @click="$router.back()">返回</el-button></div></template>
    <div v-if="loading" style="text-align:center;padding:40px"><el-icon class="is-loading"><Loading /></el-icon> 加载中...</div>
    <div v-else-if="detail">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="收文编号">{{ detail.docNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="来文单位">{{ detail.sourceUnit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收文时间">{{ detail.receiveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前处理人">{{ detail.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="detail.priority === 'high' ? 'danger' : detail.priority === 'low' ? 'info' : 'warning'" size="small">
            {{ { high: '紧急', normal: '普通', low: '低' }[detail.priority] || detail.priority }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag type="primary">{{ statusName(detail.status) }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="文件标题" :span="2">{{ detail.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2"><div v-html="detail.content || '无正文内容'"></div></el-descriptions-item>
      </el-descriptions>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElIcon } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { getIncomingDetail } from '@/api/workflow/incoming'

const route = useRoute()
const loading = ref(false)
const detail = ref(null)

function statusName(s) {
  return { received: '已收文', distributing: '分发中', processed: '已处理', archived: '已归档' }[s] || s || '-'
}

onMounted(async () => {
  loading.value = true
  try {
    detail.value = await getIncomingDetail(route.params.id)
  } catch {}
  finally { loading.value = false }
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
