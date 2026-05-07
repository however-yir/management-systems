import { Collection, DocumentCopy, EditPen, Files, Finished, HomeFilled, Notebook, Reading, School, Tickets, User } from "@element-plus/icons-vue";
import { ROLES } from "./roles";

export const roleMenus = {
  [ROLES.ADMIN]: [
    { path: "/dashboard", title: "总览看板", icon: HomeFilled },
    { path: "/admin/teachers", title: "教师管理", icon: User },
    { path: "/admin/students", title: "学生管理", icon: School },
    { path: "/admin/courses", title: "课程管理", icon: Collection },
    { path: "/admin/examinations", title: "课程审核", icon: Finished },
    { path: "/profile/password", title: "密码修改", icon: EditPen }
  ],
  [ROLES.TEACHER]: [
    { path: "/dashboard", title: "总览看板", icon: HomeFilled },
    { path: "/teacher/my-courses", title: "我的课程", icon: Notebook },
    { path: "/teacher/course-apply", title: "课程申请", icon: DocumentCopy },
    { path: "/teacher/applications", title: "申请记录", icon: Files },
    { path: "/profile/password", title: "密码修改", icon: EditPen }
  ],
  [ROLES.STUDENT]: [
    { path: "/dashboard", title: "总览看板", icon: HomeFilled },
    { path: "/student/course-select", title: "选课中心", icon: Reading },
    { path: "/student/my-courses", title: "已选课程", icon: Tickets },
    { path: "/profile/password", title: "密码修改", icon: EditPen }
  ]
};
