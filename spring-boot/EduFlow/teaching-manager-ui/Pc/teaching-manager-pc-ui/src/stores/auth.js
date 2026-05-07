import { computed, ref } from "vue";
import { defineStore } from "pinia";

const STORAGE_KEY = "eduflow-auth";

export const useAuthStore = defineStore("auth", () => {
  const token = ref("");
  const role = ref("");
  const userId = ref("");
  const name = ref("");

  const isLoggedIn = computed(() => Boolean(token.value && role.value && userId.value));

  function hydrate() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return;
    try {
      const parsed = JSON.parse(raw);
      token.value = parsed.token ?? "";
      role.value = parsed.role ?? "";
      userId.value = parsed.userId ?? "";
      name.value = parsed.name ?? "";
    } catch (error) {
      logout();
    }
  }

  function setSession(data) {
    token.value = data.token;
    role.value = data.role;
    userId.value = data.id;
    name.value = data.name;
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify({
        token: token.value,
        role: role.value,
        userId: userId.value,
        name: name.value
      })
    );
  }

  function logout() {
    token.value = "";
    role.value = "";
    userId.value = "";
    name.value = "";
    localStorage.removeItem(STORAGE_KEY);
  }

  return {
    token,
    role,
    userId,
    name,
    isLoggedIn,
    hydrate,
    setSession,
    logout
  };
});
