<template>
  <section class="page-section">
    <h2 class="page-title">课程审核中心</h2>
    <p class="page-subtitle">处理教师提交的课程申请，支持通过/未通过流转。</p>

    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="待审批" name="wait" />
      <el-tab-pane label="已审批" name="done" />
    </el-tabs>

    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="courseApplicationId" label="申请ID" width="110" />
      <el-table-column prop="teacherName" label="申请教师" width="130" />
      <el-table-column prop="operationName" label="操作类型" width="100" />
      <el-table-column prop="courseName" label="课程名称" min-width="150" />
      <el-table-column prop="courseCredit" label="学分" width="80" />
      <el-table-column prop="courseHour" label="学时" width="80" />
      <el-table-column prop="courseExaminationName" label="状态" width="100" />
      <el-table-column prop="dateTime" label="申请时间" min-width="170" />
      <el-table-column label="操作" width="190" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
          <template v-if="activeTab === 'wait'">
            <el-button type="success" link @click="approve(row)">通过</el-button>
            <el-button type="danger" link @click="reject(row)">驳回</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="drawerVisible" title="申请详情" size="560px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="申请ID">{{ detail.courseApplicationId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="教师">{{ detail.teacherName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ detail.operationName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ detail.courseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ detail.courseCredit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="学时">{{ detail.courseHour || '-' }}</el-descriptions-item>
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
import { ElMessage, ElMessageBox } from "element-plus";
import {
  examineCourse,
  fetchAlreadyExaminations,
  fetchApplicationById,
  fetchWaitExaminations
} from "../../api/application";

const activeTab = ref("wait");
const loading = ref(false);
const tableData = ref([]);
const drawerVisible = ref(false);
const detail = reactive({});

async function loadData() {
  loading.value = true;
  try {
    if (activeTab.value === "wait") {
      const res = await fetchWaitExaminations("待审批");
      tableData.value = res.data || [];
    } else {
      const res = await fetchAlreadyExaminations("待审批");
      tableData.value = res.data || [];
    }
  } catch (error) {
    ElMessage.error(error.message || "审核数据加载失败");
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

async function doExamine(row, statusName) {
  try {
    const action = statusName === "通过" ? "通过" : "驳回";
    await ElMessageBox.confirm(`确认${action}该申请吗？`, "审批确认", { type: "warning" });
    await examineCourse({
      ...row,
      courseExaminationName: statusName
    });
    ElMessage.success("审批处理成功");
    loadData();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "审批处理失败");
    }
  }
}

function approve(row) {
  doExamine(row, "通过");
}

function reject(row) {
  doExamine(row, "未通过");
}

loadData();
</script>
