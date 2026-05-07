<template>
  <section class="page-section" style="max-width: 620px">
    <h2 class="page-title">密码修改</h2>
    <p class="page-subtitle">修改当前账号密码，修改后请妥善保管。</p>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" style="margin-top: 16px">
      <el-form-item label="新密码" prop="password">
        <el-input v-model="form.password" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="submit">确认修改</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { modifyPassword } from "../../api/auth";
import { useAuthStore } from "../../stores/auth";

const authStore = useAuthStore();
const saving = ref(false);
const formRef = ref();

const form = reactive({
  password: "",
  confirmPassword: ""
});

const rules = {
  password: [{ required: true, message: "请输入新密码", trigger: "blur" }],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    {
      validator: (_, value, callback) => {
        if (value !== form.password) {
          callback(new Error("两次输入密码不一致"));
          return;
        }
        callback();
      },
      trigger: "blur"
    }
  ]
};

function reset() {
  form.password = "";
  form.confirmPassword = "";
}

async function submit() {
  await formRef.value?.validate();
  saving.value = true;
  try {
    await modifyPassword({
      authority: authStore.role,
      userId: authStore.userId,
      account: authStore.userId,
      password: form.password
    });
    ElMessage.success("密码修改成功");
    reset();
  } catch (error) {
    ElMessage.error(error.message || "密码修改失败");
  } finally {
    saving.value = false;
  }
}
</script>
