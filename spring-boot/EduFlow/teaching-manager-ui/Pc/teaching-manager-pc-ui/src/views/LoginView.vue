<template>
  <div class="login-page">
    <div class="backdrop-layer"></div>
    <div class="panel">
      <section class="left">
        <p class="hint">EduFlow Teaching Operating System</p>
        <h1>教务流程从录入到审批一次串联</h1>
        <p class="desc">
          覆盖课程管理、选课流程、课程申请、审批和成绩录入，帮助管理员、教师、学生在同一系统中协同推进。
        </p>

        <div class="capabilities">
          <article>
            <h3>管理员</h3>
            <p>成员管理、课程维护、审批处理</p>
          </article>
          <article>
            <h3>教师</h3>
            <p>课程申请、课程查看、申请追踪</p>
          </article>
          <article>
            <h3>学生</h3>
            <p>在线选课、退课、课程成绩查询</p>
          </article>
        </div>

        <div class="signal-row">
          <span>Spring Boot 3</span>
          <span>Vue3 + Element Plus</span>
          <span>JWT Auth</span>
        </div>
      </section>

      <section class="right">
        <el-card shadow="never" class="login-card">
          <template #header>
            <div class="card-title">登录平台</div>
          </template>
          <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
            <el-form-item label="身份" prop="authority">
              <el-radio-group v-model="form.authority">
                <el-radio-button label="1">管理员</el-radio-button>
                <el-radio-button label="3">教师</el-radio-button>
                <el-radio-button label="2">学生</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="账号" prop="account">
              <el-input v-model="form.account" placeholder="请输入账号" clearable />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                show-password
                clearable
                @keyup.enter="submit"
              />
            </el-form-item>
            <el-button type="primary" :loading="loading" class="login-btn" @click="submit">
              登录
            </el-button>
          </el-form>

          <div class="account-tip">
            <p>演示账号</p>
            <span>管理员：admin / 123456</span>
          </div>
        </el-card>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { login } from "../api/auth";
import { ROLES } from "../constants/roles";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAuthStore();
const formRef = ref();
const loading = ref(false);

const form = reactive({
  authority: ROLES.ADMIN,
  account: "",
  password: ""
});

const rules = {
  authority: [{ required: true, message: "请选择身份", trigger: "change" }],
  account: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
};

function getHome(role) {
  if (role === ROLES.ADMIN) return "/admin/teachers";
  if (role === ROLES.TEACHER) return "/teacher/my-courses";
  if (role === ROLES.STUDENT) return "/student/course-select";
  return "/dashboard";
}

async function submit() {
  await formRef.value?.validate();
  loading.value = true;
  try {
    const res = await login(form);
    authStore.setSession(res.data);
    ElMessage.success("登录成功");
    router.replace(getHome(res.data.role));
  } catch (error) {
    ElMessage.error(error.message || "登录失败");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(circle at 8% 15%, rgba(16, 185, 129, 0.2), transparent 40%),
    radial-gradient(circle at 92% 10%, rgba(14, 165, 233, 0.22), transparent 36%),
    linear-gradient(140deg, #eef8ff, #f4fbff 40%, #f3f6fc);
  position: relative;
  overflow: hidden;
}

.backdrop-layer {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(102, 153, 219, 0.14) 1px, transparent 1px),
    linear-gradient(90deg, rgba(102, 153, 219, 0.14) 1px, transparent 1px);
  background-size: 34px 34px;
  opacity: 0.38;
}

.panel {
  position: relative;
  z-index: 1;
  width: min(1160px, 94vw);
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  border-radius: 24px;
  overflow: hidden;
  border: 1px solid rgba(129, 172, 229, 0.35);
  box-shadow: 0 26px 64px rgba(13, 38, 75, 0.22);
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(10px);
}

.left {
  padding: 44px;
  background:
    radial-gradient(circle at 20% 0%, rgba(68, 238, 255, 0.22), transparent 45%),
    linear-gradient(160deg, #062046 0%, #0b3b76 55%, #0b66cf 100%);
  color: #f5fbff;
}

.hint {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  opacity: 0.86;
}

.left h1 {
  margin: 14px 0 12px;
  font-size: clamp(28px, 3.2vw, 38px);
  line-height: 1.2;
  font-family: "Sora", "Noto Sans SC", sans-serif;
}

.desc {
  margin: 0;
  color: #d7eaff;
  line-height: 1.7;
}

.capabilities {
  margin-top: 20px;
  display: grid;
  gap: 10px;
}

.capabilities article {
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid rgba(196, 220, 255, 0.26);
  background: rgba(255, 255, 255, 0.08);
}

.capabilities h3 {
  margin: 0;
  font-size: 14px;
}

.capabilities p {
  margin: 6px 0 0;
  font-size: 12px;
  color: #d2e6ff;
}

.signal-row {
  margin-top: 18px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.signal-row span {
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 11px;
  background: rgba(255, 255, 255, 0.15);
}

.right {
  padding: 34px;
  display: flex;
  align-items: center;
}

.login-card {
  width: 100%;
  border-radius: 16px;
  border: 1px solid #d4e5fb;
  box-shadow: 0 12px 30px rgba(18, 44, 78, 0.1);
}

.card-title {
  font-size: 18px;
  font-weight: 700;
  color: #13385f;
}

.login-btn {
  width: 100%;
}

.account-tip {
  margin-top: 14px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f4f9ff;
  border: 1px solid #d8e8fc;
}

.account-tip p {
  margin: 0;
  font-size: 12px;
  color: #4c6786;
}

.account-tip span {
  margin-top: 4px;
  display: block;
  font-size: 12px;
  color: #2b5078;
}

@media (max-width: 980px) {
  .panel {
    grid-template-columns: 1fr;
  }

  .left,
  .right {
    padding: 22px;
  }
}
</style>
