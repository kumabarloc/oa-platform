<template>
  <div class="vehicle-form">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑用车申请' : '新建用车申请' }}</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="申请事由" prop="reason">
          <el-input v-model="form.reason" maxlength="200" placeholder="请输入用车申请事由"/>
        </el-form-item>

        <el-form-item label="用车时间" prop="vehicleTime">
          <el-date-picker
            v-model="form.vehicleTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width:400px"
          />
        </el-form-item>

        <el-form-item label="目的地" prop="destination">
          <el-input v-model="form.destination" maxlength="200" placeholder="请输入目的地" style="width:300px"/>
        </el-form-item>

        <el-form-item label="用车人数" prop="passengerCount">
          <el-input-number v-model="form.passengerCount" :min="1" :max="50" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="4" maxlength="500" placeholder="补充说明（如车型要求、特殊需求等）" style="width:500px"/>
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
import { createVehicle, updateVehicle, getVehicleDetail } from '@/api/workflow/vehicle'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const saving = ref(false)
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  reason: '',
  vehicleTime: '',
  destination: '',
  passengerCount: 1,
  remark: '',
})

const rules = {
  reason: [{ required: true, message: '请输入申请事由', trigger: 'blur' }],
  vehicleTime: [{ required: true, message: '请选择用车时间', trigger: 'change' }],
  destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
  passengerCount: [{ required: true, message: '请填写用车人数', trigger: 'blur' }],
}

onMounted(async () => {
  if (isEdit.value) {
    try {
      const res = await getVehicleDetail(route.params.id)
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
      await updateVehicle(route.params.id, form)
      ElMessage.success('更新成功')
    } else {
      await createVehicle(form)
      ElMessage.success('创建成功')
    }
    router.push('/workflow/vehicle/list')
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

function handleBack() { router.back() }
</script>
