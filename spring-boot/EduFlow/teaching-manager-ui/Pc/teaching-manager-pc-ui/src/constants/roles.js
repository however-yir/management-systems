export const ROLES = {
  ADMIN: "1",
  STUDENT: "2",
  TEACHER: "3"
};

export const ROLE_LABELS = {
  [ROLES.ADMIN]: "管理员",
  [ROLES.STUDENT]: "学生",
  [ROLES.TEACHER]: "教师"
};

export function getRoleLabel(role) {
  return ROLE_LABELS[role] ?? "未知角色";
}
