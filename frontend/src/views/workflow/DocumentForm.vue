<template>
  <div class="document-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑发文' : '新建发文' }}</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" maxlength="200" placeholder="请输入公文标题"/>
        </el-form-item>

        <el-form-item label="类型" prop="docType">
          <el-select v-model="form.docType" style="width:200px">
            <el-option label="通知" value="notice"/>
            <el-option label="简报" value="info"/>
            <el-option label="报告" value="report"/>
          </el-select>
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio value="low">低</el-radio>
            <el-radio value="normal">普通</el-radio>
            <el-radio value="high">紧急</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="正文内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入正文内容（支持富文本HTML）"/>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="500"/>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="saving">保存</el-button>
          <el-button @click="handleBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createDocument, updateDocument, getDocumentDetail } from '@/api/workflow/document'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const saving = ref(false)
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  docType: 'notice',
  priority: 'normal',
  content: '',
  remark: '',
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  docType: [{ required: true, message: '请选择类型', trigger: 'change' }],
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const res = await getDocumentDetail(route.params.id)
      Object.assign(form, res)
    } catch { ElMessage.error('加载失败') }
  }
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    if (isEdit.value) {
      await updateDocument(route.params.id, form)
      ElMessage.success('更新成功')
    } else {
      await createDocument(form)
      ElMessage.success('创建成功')
    }
    router.push('/workflow/document/list')
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

function handleBack() { router.back() }
</script>
