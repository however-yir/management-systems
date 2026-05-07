import { request } from "../utils/request";

export function fetchDepartments() {
  return request({ method: "get", url: "/departments" });
}

export function fetchTeachers(data) {
  return request({ method: "post", url: "/teachers", data });
}

export function fetchTeacherOptions() {
  return request({ method: "get", url: "/get/teachers" });
}

export function createTeacher(data) {
  return request({ method: "post", url: "/teacher", data });
}

export function updateTeacher(data) {
  return request({ method: "put", url: "/modify/teacher", data });
}

export function deleteTeachers(ids) {
  return request({ method: "delete", url: `/teachers/${ids.join(",")}` });
}

export function fetchStudents(data) {
  return request({ method: "post", url: "/students", data });
}

export function createStudent(data) {
  return request({ method: "post", url: "/student", data });
}

export function updateStudent(data) {
  return request({ method: "put", url: "/modify/student", data });
}

export function deleteStudents(ids) {
  return request({ method: "delete", url: `/students/${ids.join(",")}` });
}
