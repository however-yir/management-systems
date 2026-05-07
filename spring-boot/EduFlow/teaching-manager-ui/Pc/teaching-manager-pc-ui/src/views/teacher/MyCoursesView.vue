<template>
  <section class="page-section">
    <h2 class="page-title">我的课程</h2>
    <p class="page-subtitle">查看当前教师名下课程，并支持按课程录入学生成绩。</p>

    <el-table :data="tableData" border stripe v-loading="loading" class="page-table">
      <el-table-column prop="courseId" label="ID" width="80" />
      <el-table-column prop="name" label="课程名称" min-width="150" />
      <el-table-column prop="credit" label="学分" width="80" />
      <el-table-column prop="hour" label="学时" width="80" />
      <el-table-column prop="time" label="上课时间" width="140" />
      <el-table-column prop="courseStatusName" label="状态" width="110" />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openStudents(row)">学生与成绩</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="studentDialogVisible" title="选课学生与成绩录入" width="780px">
      <p class="page-subtitle" style="margin-top: 0; margin-bottom: 12px">
        当前课程：{{ activeCourse?.name || '-' }}
      </p>
      <el-table :data="courseStudents" border stripe v-loading="studentLoading">
        <el-table-column prop="studentId" label="学生ID" width="100" />
        <el-table-column prop="studentNumber" label="学号" width="150" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="studentClass" label="班级" min-width="140" />
        <el-table-column label="成绩" width="180">
          <template #default="{ row }">
            <el-input-number
              v-model="row.score"
              :min="0"
              :max="100"
              :step="1"
              controls-position="right"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="saveScore(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage } from "element-plus";
import { fetchCourseStudents, fetchTeacherCourses, updateStudentScore } from "../../api/course";
import { useAuthStore } from "../../stores/auth";

const authStore = useAuthStore();
const loading = ref(false);
const tableData = ref([]);

const studentDialogVisible = ref(false);
const studentLoading = ref(false);
const activeCourse = ref(null);
const courseStudents = ref([]);

async function loadData() {
  loading.value = true;
  try {
    const res = await fetchTeacherCourses(authStore.userId);
    tableData.value = res.data || [];
  } catch (error) {
    ElMessage.error(error.message || "课程加载失败");
  } finally {
    loading.value = false;
  }
}

async function openStudents(row) {
  activeCourse.value = row;
  studentDialogVisible.value = true;
  studentLoading.value = true;
  try {
    const res = await fetchCourseStudents(row.courseId);
    courseStudents.value = (res.data || []).map((item) => ({
      ...item,
      score: item.score === null || item.score === undefined ? null : Number(item.score)
    }));
  } catch (error) {
    ElMessage.error(error.message || "学生数据加载失败");
  } finally {
    studentLoading.value = false;
  }
}

async function saveScore(row) {
  if (!activeCourse.value) return;
  try {
    await updateStudentScore({
      courseId: activeCourse.value.courseId,
      studentId: row.studentId,
      score: row.score
    });
    ElMessage.success("成绩保存成功");
  } catch (error) {
    ElMessage.error(error.message || "成绩保存失败");
  }
}

onMounted(loadData);
</script>
