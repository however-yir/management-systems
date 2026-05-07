import { request } from "../utils/request";

export function login(data) {
  return request({ method: "post", url: "/login", data });
}

export function checkLogin(data) {
  return request({ method: "post", url: "/check/login", data });
}

export function modifyPassword(data) {
  return request({ method: "post", url: "/modify/user/password", data });
}
