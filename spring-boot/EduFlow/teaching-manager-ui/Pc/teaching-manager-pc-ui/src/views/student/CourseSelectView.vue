<template>
  <section class="page-section">
    <h2 class="page-title">选课中心</h2>
    <p class="page-subtitle">按课程名称与教师检索课程，执行选课操作。</p>

    <div class="page-toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="课程名称">
          <el-input v-model="query.name" placeholder="课程名称" clearable />
        </el-form-item>
        <el-form-item label="教师姓名">
          <el-input v-model="query.teacherName" placeholder="教师姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table class="page-table" :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="courseId" label="ID" width="80" />
      <el-table-column prop="name" label="课程名称" min-width="150" />
      <el-table-column prop="teacherName" label="授课教师" width="130" />
      <el-table-column prop="credit" label="学分" width="80" />
      <el-table-column prop="hour" label="学时" width="80" />
      <el-table-column prop="time" label="上课时间" min-width="130" />
      <el-table-column prop="placeName" label="授课地点" min-width="130" />
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusType(row.courseStatusName)">{{ row.courseStatusName || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" :disabled="selectedCourseIds.includes(row.courseId)" @click="choose(row)">
            {{ selectedCourseIds.includes(row.courseId) ? '已选' : '选课' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="display: flex; justify-content: flex-end; margin-top: 14px">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        :current-page="pagination.currentPage"
        :page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        @current-change="(page) => ((pagination.currentPage = page), loadCourses())"
        @size-change="(size) => ((pagination.pageSize = size), (pagination.currentPage = 1), loadCourses())"
      />
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { fetchCourses, fetchStudentSelectedCourses, selectCourse } from "../../api/course";
import { useAuthStore } from "../../stores/auth";

const authStore = useAuthStore();

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const selectedCourseIds = ref([]);

const query = reactive({
  name: "",
  teacherName: ""
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10
});

function payload() {
  return {
    pageSize: pagination.pageSize,
    currentPage: pagination.currentPage,
    param: {
      name: query.name,
      teacherName: query.teacherName,
      courseStatusId: null
    }
  };
}

function statusType(name) {
  if (name === "可选") return "success";
  if (name === "待选") return "warning";
  return "info";
}

async function loadSelected() {
  try {
    const res = await fetchStudentSelectedCourses(authStore.userId);
    selectedCourseIds.value = (res.data || []).map((item) => item.courseId);
  } catch (error) {
    ElMessage.error(error.message || "已选课程读取失败");
  }
}

async function loadCourses() {
  loading.value = true;
  try {
    const res = await fetchCourses(payload());
    tableData.value = res.data || [];
    total.value = res.total || 0;
  } catch (error) {
    ElMessage.error(error.message || "课程加载失败");
  } finally {
    loading.value = false;
  }
}

async function choose(row) {
  try {
    await selectCourse({
      courseId: row.courseId,
      studentId: authStore.userId
    });
    ElMessage.success("选课成功");
    await loadSelected();
  } catch (error) {
    ElMessage.error(error.message || "选课失败");
  }
}

function handleSearch() {
  pagination.currentPage = 1;
  loadCourses();
}

function handleReset() {
  query.name = "";
  query.teacherName = "";
  handleSearch();
}

onMounted(async () => {
  await loadSelected();
  await loadCourses();
});
</script>
