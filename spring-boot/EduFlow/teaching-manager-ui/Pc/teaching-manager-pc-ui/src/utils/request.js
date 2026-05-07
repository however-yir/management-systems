import axios from "axios";
import { useAuthStore } from "../stores/auth";

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 15000
});

instance.interceptors.request.use((config) => {
  const authStore = useAuthStore();
  if (authStore.token) {
    config.headers.Authorization = authStore.token;
  }
  return config;
});

instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const detail = error.response?.data?.message || error.message || "网络请求失败";
    return Promise.reject(new Error(detail));
  }
);

export async function request(config) {
  const payload = await instance(config);
  if (payload?.code === 1) {
    return payload;
  }
  throw new Error(payload?.message || "接口返回异常");
}
