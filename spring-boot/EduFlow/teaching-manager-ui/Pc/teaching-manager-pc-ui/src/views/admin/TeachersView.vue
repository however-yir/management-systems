<template>
  <section class="page-section">
    <h2 class="page-title">教师管理</h2>
    <p class="page-subtitle">支持按工号/姓名/学院筛选，支持新增、修改和批量删除。</p>

    <div class="page-toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="工号">
          <el-input v-model="query.teacherNumber" placeholder="教师工号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="教师姓名" clearable />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="query.departmentName" placeholder="学院名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-space>
        <el-button type="primary" @click="openCreate">新增教师</el-button>
        <el-button type="danger" plain :disabled="selectedRows.length === 0" @click="batchDelete">批量删除</el-button>
      </el-space>
    </div>

    <el-table
      class="page-table"
      :data="tableData"
      border
      stripe
      v-loading="loading"
      @selection-change="(rows) => (selectedRows = rows)"
    >
      <el-table-column type="selection" width="48" />
      <el-table-column prop="teacherId" label="ID" width="80" />
      <el-table-column prop="teacherNumber" label="教师工号" width="140" />
      <el-table-column prop="name" label="姓名" width="130" />
      <el-table-column prop="departmentName" label="学院" min-width="160" />
      <el-table-column prop="dateTime" label="更新时间" min-width="170" />
      <el-table-column label="操作" width="190" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑教师' : '新增教师'" width="540px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="工号" prop="teacherNumber">
          <el-input v-model="form.teacherNumber" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="学院" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择学院" style="width: 100%">
            <el-option
              v-for="department in departments"
              :key="department.departmentId"
              :label="department.departmentName"
              :value="department.departmentId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { createTeacher, deleteTeachers, fetchDepartments, fetchTeachers, updateTeacher } from "../../api/member";

const loading = ref(false);
const saving = ref(false);
const tableData = ref([]);
const total = ref(0);
const selectedRows = ref([]);
const departments = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();

const query = reactive({
  teacherNumber: "",
  name: "",
  departmentName: ""
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10
});

const form = reactive({
  teacherId: "",
  teacherNumber: "",
  name: "",
  departmentId: "",
  departmentName: ""
});

const rules = {
  teacherNumber: [{ required: true, message: "请输入教师工号", trigger: "blur" }],
  name: [{ required: true, message: "请输入教师姓名", trigger: "blur" }],
  departmentId: [{ required: true, message: "请选择学院", trigger: "change" }]
};

function resetForm() {
  form.teacherId = "";
  form.teacherNumber = "";
  form.name = "";
  form.departmentId = "";
  form.departmentName = "";
}

function payload() {
  return {
    pageSize: pagination.pageSize,
    currentPage: pagination.currentPage,
    param: {
      teacherNumber: query.teacherNumber,
      name: query.name,
      departmentName: query.departmentName
    }
  };
}

async function loadData() {
  loading.value = true;
  try {
    const res = await fetchTeachers(payload());
    tableData.value = res.data || [];
    total.value = res.total || 0;
  } catch (error) {
    ElMessage.error(error.message || "教师列表加载失败");
  } finally {
    loading.value = false;
  }
}

async function loadDepartments() {
  try {
    const res = await fetchDepartments();
    departments.value = res.data || [];
  } catch (error) {
    ElMessage.error(error.message || "学院数据加载失败");
  }
}

function handleSearch() {
  pagination.currentPage = 1;
  loadData();
}

function handleReset() {
  query.teacherNumber = "";
  query.name = "";
  query.departmentName = "";
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

function fillDepartmentName() {
  const target = departments.value.find((item) => item.departmentId === form.departmentId);
  form.departmentName = target?.departmentName || "";
}

async function save() {
  await formRef.value?.validate();
  fillDepartmentName();
  saving.value = true;
  try {
    if (isEdit.value) {
      await updateTeacher(form);
      ElMessage.success("教师信息更新成功");
    } else {
      await createTeacher(form);
      ElMessage.success("教师新增成功");
    }
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    ElMessage.error(error.message || "保存失败");
  } finally {
    saving.value = false;
  }
}

async function removeByIds(ids) {
  if (ids.length === 0) return;
  await ElMessageBox.confirm(`确认删除选中的 ${ids.length} 条教师记录吗？`, "删除确认", {
    type: "warning"
  });
  await deleteTeachers(ids);
  ElMessage.success("删除成功");
  loadData();
}

async function removeRow(row) {
  try {
    await removeByIds([row.teacherId]);
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "删除失败");
    }
  }
}

async function batchDelete() {
  const ids = selectedRows.value.map((item) => item.teacherId);
  try {
    await removeByIds(ids);
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "批量删除失败");
    }
  }
}

onMounted(() => {
  loadDepartments();
  loadData();
});
</script>
