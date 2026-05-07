<template>
  <section class="page-section">
    <h2 class="page-title">已选课程</h2>
    <p class="page-subtitle">查看已选课程与成绩，支持退课操作。</p>

    <el-table :data="tableData" border stripe v-loading="loading" class="page-table">
      <el-table-column prop="courseId" label="ID" width="80" />
      <el-table-column prop="name" label="课程名称" min-width="150" />
      <el-table-column prop="teacherName" label="授课教师" width="130" />
      <el-table-column prop="time" label="上课时间" min-width="130" />
      <el-table-column prop="placeName" label="授课地点" min-width="130" />
      <el-table-column prop="courseStatusName" label="课程状态" width="110" />
      <el-table-column label="成绩" width="100">
        <template #default="{ row }">
          {{ row.score ?? '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button type="danger" link @click="drop(row)">退课</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { exitCourse, fetchStudentSelectedCourses } from "../../api/course";
import { useAuthStore } from "../../stores/auth";

const authStore = useAuthStore();
const loading = ref(false);
const tableData = ref([]);

async function loadData() {
  loading.value = true;
  try {
    const res = await fetchStudentSelectedCourses(authStore.userId);
    tableData.value = res.data || [];
  } catch (error) {
    ElMessage.error(error.message || "已选课程加载失败");
  } finally {
    loading.value = false;
  }
}

async function drop(row) {
  try {
    await ElMessageBox.confirm("确认退选课程“" + row.name + "”吗？", "退课确认", { type: "warning" });
    await exitCourse({
      studentId: authStore.userId,
      courseId: row.courseId
    });
    ElMessage.success("退课成功");
    loadData();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "退课失败");
    }
  }
}

onMounted(loadData);
</script>
