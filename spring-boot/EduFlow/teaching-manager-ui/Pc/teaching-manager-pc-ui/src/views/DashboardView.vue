<template>
  <section class="dashboard-hero page-section">
    <div class="hero-copy">
      <p class="hero-eyebrow">EduFlow Command</p>
      <h2 class="page-title">欢迎回来，{{ authStore.name }}</h2>
      <p class="hero-desc">
        当前你以「{{ roleLabel }}」身份在线，系统已同步教学数据。可从右侧快捷入口继续处理课程与审批任务。
      </p>
      <div class="hero-meta">
        <span>用户ID：{{ authStore.userId }}</span>
        <span>角色：{{ roleLabel }}</span>
        <span>更新时间：{{ nowLabel }}</span>
      </div>
    </div>

    <div class="hero-actions">
      <el-button
        v-for="action in quickActions"
        :key="action.path"
        :type="action.primary ? 'primary' : 'default'"
        @click="router.push(action.path)"
      >
        {{ action.label }}
      </el-button>
    </div>
  </section>

  <section class="dashboard-stream">
    <article v-for="item in stats" :key="item.label" class="metric-tile">
      <p class="metric-label">{{ item.label }}</p>
      <p class="metric-value">{{ item.value }}</p>
      <p class="metric-tip">{{ item.tip }}</p>
    </article>
  </section>

  <section class="page-section dashboard-note">
    <h3>今日建议</h3>
    <div class="note-grid">
      <article v-for="tip in operationTips" :key="tip.title" class="note-item">
        <p class="note-title">{{ tip.title }}</p>
        <p class="note-desc">{{ tip.desc }}</p>
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { fetchAllApplications, fetchWaitExaminations } from "../api/application";
import { fetchCourses, fetchStudentSelectedCourses, fetchTeacherCourses } from "../api/course";
import { fetchStudents, fetchTeachers } from "../api/member";
import { ROLES, getRoleLabel } from "../constants/roles";
import { useAuthStore } from "../stores/auth";

const authStore = useAuthStore();
const router = useRouter();
const roleLabel = computed(() => getRoleLabel(authStore.role));
const nowLabel = new Date().toLocaleString("zh-CN", { hour12: false });

const stats = ref([]);

const emptyCourseQuery = {
  pageSize: 1,
  currentPage: 1,
  param: {
    name: "",
    teacherName: "",
    courseStatusId: null
  }
};

const emptyTeacherQuery = {
  pageSize: 1,
  currentPage: 1,
  param: {
    teacherNumber: "",
    name: "",
    departmentName: ""
  }
};

const emptyStudentQuery = {
  pageSize: 1,
  currentPage: 1,
  param: {
    studentNumber: "",
    name: "",
    studentClass: ""
  }
};

const quickActions = computed(() => {
  if (authStore.role === ROLES.ADMIN) {
    return [
      { label: "进入审批中心", path: "/admin/examinations", primary: true },
      { label: "维护课程库", path: "/admin/courses", primary: false }
    ];
  }
  if (authStore.role === ROLES.TEACHER) {
    return [
      { label: "提交课程申请", path: "/teacher/course-apply", primary: true },
      { label: "查看申请记录", path: "/teacher/my-applications", primary: false }
    ];
  }
  return [
    { label: "前往选课中心", path: "/student/course-select", primary: true },
    { label: "查看已选课程", path: "/student/my-courses", primary: false }
  ];
});

const operationTips = computed(() => {
  if (authStore.role === ROLES.ADMIN) {
    return [
      { title: "优先处理待审批申请", desc: "先清理待审批课程可减少教师阻塞。" },
      { title: "检查课程状态流转", desc: "定时切换选课开关，避免开放窗口遗漏。" }
    ];
  }
  if (authStore.role === ROLES.TEACHER) {
    return [
      { title: "完善课程信息", desc: "课程时段、地点填写完整可提升审批通过率。" },
      { title: "跟踪申请结果", desc: "及时根据审核意见回填课程信息再提交。" }
    ];
  }
  return [
    { title: "先看可选课程", desc: "优先筛选状态为“可选”的课程提交选课。" },
    { title: "关注已选成绩", desc: "课程结课后可在已选课程页查看最新成绩。" }
  ];
});

async function loadAdminStats() {
  const [teacherRes, studentRes, courseRes, examinationRes] = await Promise.all([
    fetchTeachers(emptyTeacherQuery),
    fetchStudents(emptyStudentQuery),
    fetchCourses(emptyCourseQuery),
    fetchWaitExaminations("待审批")
  ]);
  stats.value = [
    { label: "教师总数", value: teacherRes.total || 0, tip: "支持分页、筛选与增删改" },
    { label: "学生总数", value: studentRes.total || 0, tip: "班级与学号信息可维护" },
    { label: "课程总数", value: courseRes.total || 0, tip: "课程状态可批量流转" },
    { label: "待审批申请", value: examinationRes.data?.length || 0, tip: "课程新增申请待管理员处理" }
  ];
}

async function loadTeacherStats() {
  const [courseRes, applicationRes] = await Promise.all([
    fetchTeacherCourses(authStore.userId),
    fetchAllApplications(authStore.userId)
  ]);
  stats.value = [
    { label: "我的课程", value: courseRes.data?.length || 0, tip: "查看课程状态与授课安排" },
    { label: "我的申请", value: applicationRes.data?.length || 0, tip: "新增/修改/删除申请追踪" }
  ];
}

async function loadStudentStats() {
  const [selectedRes, allCourseRes] = await Promise.all([
    fetchStudentSelectedCourses(authStore.userId),
    fetchCourses(emptyCourseQuery)
  ]);
  stats.value = [
    { label: "已选课程", value: selectedRes.data?.length || 0, tip: "支持退课与成绩展示" },
    { label: "可检索课程", value: allCourseRes.total || 0, tip: "可按名称与教师筛选课程" }
  ];
}

async function loadStats() {
  try {
    if (authStore.role === ROLES.ADMIN) {
      await loadAdminStats();
    } else if (authStore.role === ROLES.TEACHER) {
      await loadTeacherStats();
    } else {
      await loadStudentStats();
    }
  } catch (error) {
    ElMessage.error(error.message || "加载概览数据失败");
  }
}

onMounted(loadStats);
</script>

<style scoped>
.dashboard-hero {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 18px;
  background:
    radial-gradient(circle at 15% 15%, rgba(38, 171, 255, 0.2), transparent 48%),
    linear-gradient(120deg, #0e2d56 0%, #0d4c95 60%, #0b6df4 100%);
  color: #eef6ff;
  border: none;
  box-shadow: 0 18px 42px rgba(10, 44, 88, 0.28);
}

.hero-copy .page-title {
  color: #f8fbff;
}

.hero-eyebrow {
  margin: 0;
  font-size: 11px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  opacity: 0.9;
}

.hero-desc {
  margin: 10px 0 0;
  max-width: 620px;
  font-size: 14px;
  line-height: 1.6;
  color: #d9ebff;
}

.hero-meta {
  margin-top: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hero-meta span {
  display: inline-flex;
  align-items: center;
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.14);
}

.hero-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
}

.hero-actions .el-button {
  margin: 0;
}

.hero-actions :deep(.el-button:not(.el-button--primary)) {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.24);
}

.hero-actions :deep(.el-button:not(.el-button--primary):hover) {
  background: rgba(255, 255, 255, 0.2);
}

.dashboard-stream {
  margin-top: 14px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 12px;
}

.metric-tile {
  border-radius: 16px;
  border: 1px solid #d8e6f7;
  background: linear-gradient(180deg, #ffffff, #f6fbff);
  padding: 16px;
  box-shadow: 0 10px 28px rgba(16, 38, 72, 0.08);
}

.metric-label {
  margin: 0;
  color: #5f6f83;
  font-size: 13px;
}

.metric-value {
  margin: 8px 0 0;
  font-size: 30px;
  font-family: "Sora", "Noto Sans SC", sans-serif;
  font-weight: 700;
  color: #123154;
}

.metric-tip {
  margin: 8px 0 0;
  color: #62768f;
  font-size: 12px;
}

.dashboard-note {
  margin-top: 14px;
}

.dashboard-note h3 {
  margin: 0;
  font-size: 18px;
}

.note-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 10px;
}

.note-item {
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #d9e8fc;
  background: #f8fbff;
}

.note-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
}

.note-desc {
  margin: 8px 0 0;
  font-size: 12px;
  color: #637892;
  line-height: 1.5;
}

@media (max-width: 900px) {
  .dashboard-hero {
    grid-template-columns: 1fr;
  }
}
</style>
