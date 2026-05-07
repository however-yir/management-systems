<template>
  <section class="page-section">
    <h2 class="page-title">课程申请</h2>
    <p class="page-subtitle">当前后端已支持“新增课程申请”流程，提交后进入管理员审核。</p>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="108px" style="max-width: 760px; margin-top: 14px">
      <el-row :gutter="14">
        <el-col :span="12">
          <el-form-item label="课程名称" prop="courseName">
            <el-input v-model="form.courseName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程学分" prop="courseCredit">
            <el-input v-model="form.courseCredit" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="课程学时" prop="courseHour">
            <el-input v-model="form.courseHour" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="上课时间">
            <el-input v-model="form.courseTime" placeholder="例：周三 3-4 节" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="授课地点">
            <el-select v-model="form.coursePlaceId" clearable style="width: 100%">
              <el-option
                v-for="place in places"
                :key="place.placeId"
                :label="place.placeName"
                :value="place.placeId"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="课程描述" prop="courseDescription">
            <el-input v-model="form.courseDescription" type="textarea" :rows="4" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="submit">提交申请</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { applyAddCourse } from "../../api/application";
import { fetchPlaces } from "../../api/course";
import { useAuthStore } from "../../stores/auth";

const authStore = useAuthStore();
const saving = ref(false);
const places = ref([]);
const formRef = ref();

const form = reactive({
  courseName: "",
  courseCredit: "",
  courseHour: "",
  courseTime: "",
  coursePlaceId: "",
  courseDescription: ""
});

const rules = {
  courseName: [{ required: true, message: "请输入课程名称", trigger: "blur" }],
  courseCredit: [{ required: true, message: "请输入课程学分", trigger: "blur" }],
  courseHour: [{ required: true, message: "请输入课程学时", trigger: "blur" }],
  courseDescription: [{ required: true, message: "请输入课程描述", trigger: "blur" }]
};

function reset() {
  form.courseName = "";
  form.courseCredit = "";
  form.courseHour = "";
  form.courseTime = "";
  form.coursePlaceId = "";
  form.courseDescription = "";
}

async function loadPlaces() {
  try {
    const res = await fetchPlaces();
    places.value = res.data || [];
  } catch (error) {
    ElMessage.error(error.message || "地点数据加载失败");
  }
}

async function submit() {
  await formRef.value?.validate();
  saving.value = true;
  try {
    await applyAddCourse({
      teacherId: authStore.userId,
      courseName: form.courseName,
      courseCredit: form.courseCredit,
      courseHour: form.courseHour,
      courseTime: form.courseTime,
      coursePlaceId: form.coursePlaceId,
      courseDescription: form.courseDescription,
      operationName: "新增",
      courseExaminationName: "待审批"
    });
    ElMessage.success("申请提交成功");
    reset();
  } catch (error) {
    ElMessage.error(error.message || "申请提交失败");
  } finally {
    saving.value = false;
  }
}

onMounted(loadPlaces);
</script>
