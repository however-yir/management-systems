<template>
  <section class="page-section">
    <h2 class="page-title">学生管理</h2>
    <p class="page-subtitle">支持学号、姓名、班级查询，支持新增、修改、删除。</p>

    <div class="page-toolbar">
      <el-form :inline="true" :model="query">
        <el-form-item label="学号">
          <el-input v-model="query.studentNumber" placeholder="学生学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="query.name" placeholder="学生姓名" clearable />
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="query.studentClass" placeholder="班级" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-space>
        <el-button type="primary" @click="openCreate">新增学生</el-button>
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
      <el-table-column prop="studentId" label="ID" width="80" />
      <el-table-column prop="studentNumber" label="学号" width="160" />
      <el-table-column prop="name" label="姓名" width="130" />
      <el-table-column prop="studentClass" label="班级" min-width="160" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑学生' : '新增学生'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="学号" prop="studentNumber">
          <el-input v-model="form.studentNumber" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="班级" prop="studentClass">
          <el-input v-model="form.studentClass" />
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
import { createStudent, deleteStudents, fetchStudents, updateStudent } from "../../api/member";

const loading = ref(false);
const saving = ref(false);
const tableData = ref([]);
const total = ref(0);
const selectedRows = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const formRef = ref();

const query = reactive({
  studentNumber: "",
  name: "",
  studentClass: ""
});

const pagination = reactive({
  currentPage: 1,
  pageSize: 10
});

const form = reactive({
  studentId: "",
  studentNumber: "",
  name: "",
  studentClass: ""
});

const rules = {
  studentNumber: [{ required: true, message: "请输入学生学号", trigger: "blur" }],
  name: [{ required: true, message: "请输入学生姓名", trigger: "blur" }],
  studentClass: [{ required: true, message: "请输入学生班级", trigger: "blur" }]
};

function resetForm() {
  form.studentId = "";
  form.studentNumber = "";
  form.name = "";
  form.studentClass = "";
}

function payload() {
  return {
    pageSize: pagination.pageSize,
    currentPage: pagination.currentPage,
    param: {
      studentNumber: query.studentNumber,
      name: query.name,
      studentClass: query.studentClass
    }
  };
}

async function loadData() {
  loading.value = true;
  try {
    const res = await fetchStudents(payload());
    tableData.value = res.data || [];
    total.value = res.total || 0;
  } catch (error) {
    ElMessage.error(error.message || "学生列表加载失败");
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pagination.currentPage = 1;
  loadData();
}

function handleReset() {
  query.studentNumber = "";
  query.name = "";
  query.studentClass = "";
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
    if (isEdit.value) {
      await updateStudent(form);
      ElMessage.success("学生信息更新成功");
    } else {
      await createStudent(form);
      ElMessage.success("学生新增成功");
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
  await ElMessageBox.confirm(`确认删除选中的 ${ids.length} 条学生记录吗？`, "删除确认", {
    type: "warning"
  });
  await deleteStudents(ids);
  ElMessage.success("删除成功");
  loadData();
}

async function removeRow(row) {
  try {
    await removeByIds([row.studentId]);
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "删除失败");
    }
  }
}

async function batchDelete() {
  const ids = selectedRows.value.map((item) => item.studentId);
  try {
    await removeByIds(ids);
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "批量删除失败");
    }
  }
}

onMounted(loadData);
</script>
