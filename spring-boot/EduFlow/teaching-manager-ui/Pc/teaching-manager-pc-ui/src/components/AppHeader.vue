<template>
  <header class="header">
    <div class="left">
      <el-button class="collapse-btn" text @click="$emit('toggle-collapse')">
        <el-icon><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
      </el-button>
      <div class="title-wrap">
        <p class="eyebrow">EduFlow Workspace</p>
        <p class="title">{{ title }}</p>
        <p class="subtitle">{{ subtitle }}</p>
      </div>
    </div>

    <div class="right">
      <el-tag effect="plain" type="primary">{{ roleLabel }}</el-tag>
      <el-dropdown>
        <span class="dropdown-trigger">
          {{ userName }}
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/profile/password')">密码修改</el-dropdown-item>
            <el-dropdown-item divided @click="$emit('logout')">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
import { ArrowDown, Expand, Fold } from "@element-plus/icons-vue";

defineProps({
  title: {
    type: String,
    default: "教务管理"
  },
  subtitle: {
    type: String,
    default: ""
  },
  userName: {
    type: String,
    default: ""
  },
  roleLabel: {
    type: String,
    default: ""
  },
  collapsed: {
    type: Boolean,
    default: false
  }
});

defineEmits(["logout", "toggle-collapse"]);
</script>

<style scoped>
.header {
  min-height: 72px;
  border-bottom: 1px solid #d7e4f4;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.95), rgba(248, 252, 255, 0.9));
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
}

.left,
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: #eef5ff;
}

.title-wrap .eyebrow {
  margin: 0;
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: #6f829b;
}

.title-wrap .title {
  margin: 2px 0 0;
  font-size: 17px;
  font-weight: 700;
}

.title-wrap .subtitle {
  margin: 2px 0 0;
  font-size: 12px;
  color: #5f7085;
}

.dropdown-trigger {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 10px;
  background: #f2f8ff;
  cursor: pointer;
  color: #29466c;
  transition: background 0.2s ease;
}

.dropdown-trigger:hover {
  background: #e7f1ff;
}

@media (max-width: 900px) {
  .header {
    padding: 10px 12px;
    min-height: 64px;
  }

  .title-wrap .subtitle {
    display: none;
  }
}
</style>
