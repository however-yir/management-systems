<template>
  <el-container class="layout-root">
    <AppSidebar
      :menus="menus"
      :active-path="route.path"
      :collapsed="collapsed"
      @menu-select="handleMenuSelect"
    />
    <el-container class="layout-main-shell">
      <AppHeader
        :title="headerTitle"
        :subtitle="headerSubtitle"
        :user-name="authStore.name"
        :role-label="roleLabel"
        :collapsed="collapsed"
        @logout="handleLogout"
        @toggle-collapse="collapsed = !collapsed"
      />
      <el-main class="main-area">
        <div class="main-inner">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import AppSidebar from "../components/AppSidebar.vue";
import AppHeader from "../components/AppHeader.vue";
import { roleMenus } from "../constants/menu";
import { getRoleLabel } from "../constants/roles";
import { useAuthStore } from "../stores/auth";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const collapsed = ref(false);

const menus = computed(() => roleMenus[authStore.role] ?? []);
const roleLabel = computed(() => getRoleLabel(authStore.role));

const headerTitle = computed(() => {
  const current = menus.value.find((item) => item.path === route.path);
  return current?.title ?? "控制台";
});

const headerSubtitle = computed(() => `当前身份：${roleLabel.value} · 用户ID：${authStore.userId}`);

function handleMenuSelect(path) {
  router.push(path);
}

async function handleLogout() {
  await ElMessageBox.confirm("确认退出当前登录状态吗？", "退出登录", {
    type: "warning",
    confirmButtonText: "退出",
    cancelButtonText: "取消"
  });
  authStore.logout();
  router.replace("/login");
}
</script>

<style scoped>
.layout-root {
  min-height: 100vh;
  position: relative;
}

.layout-main-shell {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.54) 0%, rgba(245, 251, 255, 0.82) 100%);
}

.main-area {
  padding: 20px;
  background: transparent;
}

.main-inner {
  display: grid;
  gap: 16px;
}

@media (max-width: 900px) {
  .main-area {
    padding: 12px;
  }
}
</style>
