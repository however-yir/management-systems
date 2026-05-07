import { request } from "../utils/request";

export function fetchCourses(data) {
  return request({ method: "post", url: "/get/condition/course", data });
}

export function fetchCourseById(courseId) {
  return request({ method: "get", url: "/get/course", params: { courseId } });
}

export function createCourse(data) {
  return request({ method: "post", url: "/insert/course", data });
}

export function updateCourse(data) {
  return request({ method: "post", url: "/update/course", data });
}

export function deleteCourse(courseId) {
  return request({ method: "post", url: "/delete/course", data: { courseId } });
}

export function fetchPlaces() {
  return request({ method: "get", url: "/get/all/place" });
}

export function fetchCourseSwitchStatus() {
  return request({ method: "get", url: "/course/switch/status" });
}

export function updateCourseSwitchStatus(courseSwitchStatus) {
  return request({ method: "put", url: "/update/course/status", params: { courseSwitchStatus } });
}

export function selectCourse(data) {
  return request({ method: "post", url: "/student/select/course", data });
}

export function checkCourseSelected(data) {
  return request({ method: "post", url: "/select/course/status", data });
}

export function exitCourse(data) {
  return request({ method: "post", url: "/exit/course", data });
}

export function fetchStudentSelectedCourses(studentId) {
  return request({ method: "get", url: "/student/select/course", params: { studentId } });
}

export function fetchCourseStudents(courseId) {
  return request({ method: "get", url: "/get/select/the/course/students", params: { courseId } });
}

export function updateStudentScore(data) {
  return request({ method: "post", url: "/update/student/score", data });
}

export function fetchTeacherCourses(teacherId) {
  return request({ method: "get", url: "/teacher/course/by/id", params: { teacherId } });
}
