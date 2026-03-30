<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="title">OA 办公平台</h1>
      <el-form ref="formRef" :model="form" :rules="rules" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="用户名 / 工号"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="captcha">
          <el-input
            v-model="form.captcha"
            placeholder="验证码"
            prefix-icon="CircleCheck"
            size="large"
            style="width: 60%"
          />
          <div class="captcha-box" @click="refreshCaptcha">
            <img v-if="captchaData.url" :src="captchaData.url" alt="验证码" />
            <span v-else class="captcha-placeholder">点击刷新</span>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { getCaptcha } from '@/api/auth'

const router = useRouter()
const auth = useAuthStore()
const formRef = ref()
const loading = ref(false)
const captchaData = reactive({ key: '', url: '' })

const form = reactive({
  username: '',
  password: '',
  captcha: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

async function refreshCaptcha() {
  try {
    const res = await getCaptcha()
    captchaData.key = res.data?.key || ''
    captchaData.url = res.data?.image || ''
    form.captcha = ''
  } catch {
    // 忽略
  }
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await auth.login({ ...form, captchaKey: captchaData.key })
    if (res.data?.token) {
      ElMessage.success('登录成功')
      router.push('/dashboard')
    }
  } catch {
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,.15);
}
.title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
}
.captcha-box {
  width: 38%;
  height: 40px;
  margin-left: 2%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.captcha-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.captcha-placeholder {
  font-size: 12px;
  color: #999;
}
</style>