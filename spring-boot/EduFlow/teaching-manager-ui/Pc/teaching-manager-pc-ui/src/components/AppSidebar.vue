<template>
  <el-aside class="sidebar" :width="collapsed ? '72px' : '232px'">
    <div class="brand" @click="$router.push('/dashboard')">
      <span class="logo">E</span>
      <div v-if="!collapsed" class="brand-meta">
        <span class="name">EduFlow</span>
        <span class="tagline">Teaching Ops Hub</span>
      </div>
    </div>

    <el-scrollbar>
      <el-menu
        class="menu"
        :collapse="collapsed"
        :default-active="activePath"
        unique-opened
        @select="onSelect"
      >
        <el-menu-item
          v-for="item in menus"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>

    <div v-if="!collapsed" class="footer-note">
      <p>Spring Boot + Vue3</p>
      <span>教务流程在线协同</span>
    </div>
  </el-aside>
</template>

<script setup>
const props = defineProps({
  menus: {
    type: Array,
    default: () => []
  },
  activePath: {
    type: String,
    default: ""
  },
  collapsed: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(["menu-select"]);

function onSelect(path) {
  emit("menu-select", path);
}
</script>

<style scoped>
.sidebar {
  background: linear-gradient(180deg, #061b3a 0%, #0b2850 45%, #0b3462 100%);
  color: #d0d6e3;
  border-right: 1px solid #1f3d67;
  transition: width 0.25s ease;
  display: flex;
  flex-direction: column;
}

.brand {
  min-height: 74px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  cursor: pointer;
  border-bottom: 1px solid rgba(132, 179, 239, 0.18);
}

.logo {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(145deg, #40b5ff, #0b6df4);
  color: #fff;
  font-weight: 700;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.brand-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.name {
  color: #eef6ff;
  font-size: 17px;
  font-weight: 700;
}

.tagline {
  color: #9ebfe3;
  font-size: 11px;
}

.menu {
  border-right: none;
  background: transparent;
  padding: 8px 0;
}

.menu :deep(.el-menu-item) {
  color: #d6e7fb;
  margin: 6px 8px;
  border-radius: 10px;
  height: 42px;
}

.menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(55, 154, 255, 0.36), rgba(24, 129, 236, 0.22));
  color: #fff;
}

.menu :deep(.el-menu-item:hover) {
  background: rgba(163, 196, 237, 0.18);
}

.footer-note {
  margin-top: auto;
  padding: 12px 14px 16px;
  border-top: 1px solid rgba(132, 179, 239, 0.18);
  color: #9ebfe3;
}

.footer-note p {
  margin: 0;
  font-size: 12px;
}

.footer-note span {
  font-size: 11px;
  opacity: 0.9;
}
</style>
