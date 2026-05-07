<template>
  <section class="page-section">
    <h2 class="page-title">我的申请记录</h2>
    <p class="page-subtitle">按审批状态筛选课程申请，查看审批结果与详情。</p>

    <div class="page-toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="审批状态">
          <el-select v-model="query.status" style="width: 180px" @change="loadData">
            <el-option label="全部" value="ALL" />
            <el-option label="待审批" value="待审批" />
            <el-option label="通过" value="通过" />
            <el-option label="未通过" value="未通过" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="courseApplicationId" label="申请ID" width="110" />
      <el-table-column prop="operationName" label="操作类型" width="100" />
      <el-table-column prop="courseName" label="课程名称" min-width="150" />
      <el-table-column prop="courseCredit" label="学分" width="80" />
      <el-table-column prop="courseHour" label="学时" width="80" />
      <el-table-column prop="courseExaminationName" label="审批状态" width="110" />
      <el-table-column prop="dateTime" label="申请时间" min-width="170" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="drawerVisible" title="申请详情" size="560px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="申请ID">{{ detail.courseApplicationId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ detail.courseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ detail.operationName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程学分">{{ detail.courseCredit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程学时">{{ detail.courseHour || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上课时间">{{ detail.courseTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="授课地点">{{ detail.coursePlaceName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程描述">{{ detail.courseDescription || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">{{ detail.courseExaminationName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detail.dateTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </section>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { fetchAllApplications, fetchApplicationById, fetchApplicationsByStatus } from "../../api/application";
import { useAuthStore } from "../../stores/auth";

const authStore = useAuthStore();
const loading = ref(false);
const tableData = ref([]);
const drawerVisible = ref(false);
const detail = reactive({});

const query = reactive({
  status: "ALL"
});

async function loadData() {
  loading.value = true;
  try {
    if (query.status === "ALL") {
      const res = await fetchAllApplications(authStore.userId);
      tableData.value = res.data || [];
    } else {
      const res = await fetchApplicationsByStatus(authStore.userId, query.status);
      tableData.value = res.data || [];
    }
  } catch (error) {
    ElMessage.error(error.message || "申请记录加载失败");
  } finally {
    loading.value = false;
  }
}

async function viewDetail(row) {
  try {
    const res = await fetchApplicationById(row.courseApplicationId);
    Object.assign(detail, res.data || {});
    drawerVisible.value = true;
  } catch (error) {
    ElMessage.error(error.message || "详情加载失败");
  }
}

loadData();
</script>
