import { request } from "../utils/request";

export function applyAddCourse(data) {
  return request({ method: "post", url: "/apply/add/course", data });
}

export function fetchAllApplications(teacherId) {
  return request({ method: "get", url: "/all/application", params: { teacherId } });
}

export function fetchApplicationsByStatus(teacherId, courseExaminationName) {
  return request({ method: "get", url: "/application", params: { teacherId, courseExaminationName } });
}

export function fetchApplicationById(courseApplicationId) {
  return request({ method: "get", url: "/get/application", params: { courseApplicationId } });
}

export function fetchWaitExaminations(examinationName = "待审批") {
  return request({ method: "get", url: "/wait/examination", params: { examinationName } });
}

export function fetchAlreadyExaminations(examinationName = "待审批") {
  return request({ method: "get", url: "/already/examination", params: { examinationName } });
}

export function examineCourse(data) {
  return request({ method: "post", url: "/course/examination", data });
}
