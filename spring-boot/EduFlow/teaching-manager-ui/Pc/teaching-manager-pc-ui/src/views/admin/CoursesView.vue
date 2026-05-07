<template>
  <section class="page-section">
    <h2 class="page-title">课程管理</h2>
    <p class="page-subtitle">课程信息维护、选课状态切换、已选学生成绩录入。</p>

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

      <el-space>
        <el-tag type="info">选课开关：{{ courseSwitchStatusLabel }}</el-tag>
        <el-button type="warning" plain @click="toggleSwitch">切换选课状态</el-button>
        <el-button type="primary" @click="openCreate">新增课程</el-button>
      </el-space>
    </div>

    <el-table class="page-table" :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="courseId" label="ID" width="80" />
      <el-table-column prop="name" label="课程名" min-width="140" />
      <el-table-column prop="teacherName" label="授课教师" width="130" />
      <el-table-column prop="credit" label="学分" width="80" />
      <el-table-column prop="hour" label="学时" width="80" />
      <el-table-column prop="time" label="上课时间" width="130" />
      <el-table-column prop="placeName" label="地点" min-width="130" />
      <el-table-column prop="courseStatusName" label="状态" width="100" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-button type="success" link @click="openStudents(row)">选课学生</el-button>
          <el-button type="danger" link @click="removeRow(row)">删除</el-button>
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
        @current-change="(page) => ((pagination.currentPage = page), loadData())"
        @size-change="(size) => ((pagination.pageSize = size), (pagination.currentPage = 1), loadData())"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-row :gutter="14">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授课教师" prop="teacherId">
              <el-select v-model="form.teacherId" style="width: 100%" filterable>
                <el-option
                  v-for="teacher in teachers"
                  :key="teacher.teacherId"
                  :label="`${teacher.name} (${teacher.teacherNumber})`"
                  :value="teacher.teacherId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程学分" prop="credit">
              <el-input v-model="form.credit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程学时" prop="hour">
              <el-input v-model="form.hour" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课时间">
              <el-input v-model="form.time" placeholder="例：周一 1-2 节" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="授课地点" prop="placeId">
              <el-select v-model="form.placeId" style="width: 100%" filterable>
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
            <el-form-item label="课程描述">
              <el-input v-model="form.description" type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="studentDialogVisible" title="选课学生与成绩录入" width="800px">
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
import { computed, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  createCourse,
  deleteCourse,
  fetchCourseStudents,
  fetchCourseSwitchStatus,
  fetchCourses,
  fetchPlaces,
  updateCourse,
  updateCourseSwitchStatus,
  updateStudentScore
} from "../../api/course";
import { fetchTeacherOptions } from "../../api/member";

const loading = ref(false);
const saving = ref(false);
const tableData = ref([]);
const total = ref(0);
const teachers = ref([]);
const places = ref([]);
const courseSwitchStatus = ref("0");
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();

const studentDialogVisible = ref(false);
const studentLoading = ref(false);
const activeCourse = ref(null);
const courseStudents = ref([]);

const query = reactive({
  name: "",
  teacherName: ""
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10
});

const form = reactive({
  courseId: "",
  name: "",
  teacherId: "",
  credit: "",
  hour: "",
  time: "",
  placeId: "",
  description: ""
});

const rules = {
  name: [{ required: true, message: "请输入课程名称", trigger: "blur" }],
  teacherId: [{ required: true, message: "请选择授课教师", trigger: "change" }],
  credit: [{ required: true, message: "请输入课程学分", trigger: "blur" }],
  hour: [{ required: true, message: "请输入课程学时", trigger: "blur" }],
  placeId: [{ required: true, message: "请选择授课地点", trigger: "change" }]
};

const courseSwitchStatusLabel = computed(() =>
  courseSwitchStatus.value === "1" ? "已开启（待选 -> 可选）" : "已关闭（可选 -> 授课中）"
);

function resetForm() {
  form.courseId = "";
  form.name = "";
  form.teacherId = "";
  form.credit = "";
  form.hour = "";
  form.time = "";
  form.placeId = "";
  form.description = "";
}

function buildQueryPayload() {
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

async function loadData() {
  loading.value = true;
  try {
    const res = await fetchCourses(buildQueryPayload());
    tableData.value = res.data || [];
    total.value = res.total || 0;
  } catch (error) {
    ElMessage.error(error.message || "课程列表加载失败");
  } finally {
    loading.value = false;
  }
}

async function loadOptions() {
  try {
    const [teacherRes, placeRes] = await Promise.all([fetchTeacherOptions(), fetchPlaces()]);
    teachers.value = teacherRes.data || [];
    places.value = placeRes.data || [];
  } catch (error) {
    ElMessage.error(error.message || "教师/地点数据加载失败");
  }
}

async function loadCourseSwitchStatus() {
  try {
    const res = await fetchCourseSwitchStatus();
    courseSwitchStatus.value = String(res.data ?? "0");
  } catch (error) {
    ElMessage.error(error.message || "选课开关读取失败");
  }
}

function handleSearch() {
  pagination.currentPage = 1;
  loadData();
}

function handleReset() {
  query.name = "";
  query.teacherName = "";
  handleSearch();
}

function openCreate() {
  isEdit.value = false;
  resetForm();
  dialogVisible.value = true;
}

function openEdit(row) {
  isEdit.value = true;
  resetForm();
  Object.assign(form, row);
  dialogVisible.value = true;
}

async function save() {
  await formRef.value?.validate();
  saving.value = true;
  try {
    const payload = {
      courseId: form.courseId,
      name: form.name,
      teacherId: form.teacherId,
      credit: form.credit,
      hour: form.hour,
      time: form.time,
      placeId: form.placeId,
      description: form.description
    };
    if (isEdit.value) {
      await updateCourse(payload);
      ElMessage.success("课程信息更新成功");
    } else {
      await createCourse(payload);
      ElMessage.success("课程新增成功");
    }
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    ElMessage.error(error.message || "保存失败");
  } finally {
    saving.value = false;
  }
}

async function removeRow(row) {
  try {
    await ElMessageBox.confirm(`确认删除课程“${row.name}”吗？`, "删除确认", { type: "warning" });
    await deleteCourse(row.courseId);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "删除失败");
    }
  }
}

async function toggleSwitch() {
  const target = courseSwitchStatus.value === "1" ? "0" : "1";
  const tip = target === "1" ? "将把待选课程切换为可选。" : "将把可选课程切换为授课中。";
  try {
    await ElMessageBox.confirm(`确认执行课程状态流转？${tip}`, "状态切换", { type: "warning" });
    await updateCourseSwitchStatus(target);
    ElMessage.success("课程状态已切换");
    await Promise.all([loadCourseSwitchStatus(), loadData()]);
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "状态切换失败");
    }
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
    ElMessage.error(error.message || "选课学生加载失败");
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
    ElMessage.success(`已保存 ${row.name} 的成绩`);
  } catch (error) {
    ElMessage.error(error.message || "成绩保存失败");
  }
}

onMounted(() => {
  loadOptions();
  loadCourseSwitchStatus();
  loadData();
});
</script>
