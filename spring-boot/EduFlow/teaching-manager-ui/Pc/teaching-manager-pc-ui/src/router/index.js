import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";
import { ROLES } from "../constants/roles";

const routes = [
  {
    path: "/login",
    name: "login",
    component: () => import("../views/LoginView.vue"),
    meta: { public: true }
  },
  {
    path: "/",
    component: () => import("../layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        redirect: "/dashboard"
      },
      {
        path: "dashboard",
        name: "dashboard",
        component: () => import("../views/DashboardView.vue")
      },
      {
        path: "admin/teachers",
        name: "admin-teachers",
        component: () => import("../views/admin/TeachersView.vue"),
        meta: { roles: [ROLES.ADMIN] }
      },
      {
        path: "admin/students",
        name: "admin-students",
        component: () => import("../views/admin/StudentsView.vue"),
        meta: { roles: [ROLES.ADMIN] }
      },
      {
        path: "admin/courses",
        name: "admin-courses",
        component: () => import("../views/admin/CoursesView.vue"),
        meta: { roles: [ROLES.ADMIN] }
      },
      {
        path: "admin/examinations",
        name: "admin-examinations",
        component: () => import("../views/admin/ExaminationsView.vue"),
        meta: { roles: [ROLES.ADMIN] }
      },
      {
        path: "teacher/my-courses",
        name: "teacher-my-courses",
        component: () => import("../views/teacher/MyCoursesView.vue"),
        meta: { roles: [ROLES.TEACHER] }
      },
      {
        path: "teacher/course-apply",
        name: "teacher-course-apply",
        component: () => import("../views/teacher/CourseApplyView.vue"),
        meta: { roles: [ROLES.TEACHER] }
      },
      {
        path: "teacher/applications",
        name: "teacher-applications",
        component: () => import("../views/teacher/MyApplicationsView.vue"),
        meta: { roles: [ROLES.TEACHER] }
      },
      {
        path: "student/course-select",
        name: "student-course-select",
        component: () => import("../views/student/CourseSelectView.vue"),
        meta: { roles: [ROLES.STUDENT] }
      },
      {
        path: "student/my-courses",
        name: "student-my-courses",
        component: () => import("../views/student/MyCoursesView.vue"),
        meta: { roles: [ROLES.STUDENT] }
      },
      {
        path: "profile/password",
        name: "profile-password",
        component: () => import("../views/shared/PasswordView.vue")
      }
    ]
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/dashboard"
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

function getHomePathByRole(role) {
  switch (role) {
    case ROLES.ADMIN:
      return "/admin/teachers";
    case ROLES.TEACHER:
      return "/teacher/my-courses";
    case ROLES.STUDENT:
      return "/student/course-select";
    default:
      return "/dashboard";
  }
}

router.beforeEach((to) => {
  const authStore = useAuthStore();
  const isPublic = to.meta?.public;

  if (isPublic && authStore.isLoggedIn) {
    return getHomePathByRole(authStore.role);
  }

  if (!isPublic && !authStore.isLoggedIn) {
    return "/login";
  }

  const allowedRoles = to.meta?.roles;
  if (allowedRoles && !allowedRoles.includes(authStore.role)) {
    return getHomePathByRole(authStore.role);
  }

  return true;
});

export default router;
