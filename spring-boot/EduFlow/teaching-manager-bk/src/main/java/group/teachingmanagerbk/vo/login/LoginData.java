package group.teachingmanagerbk.vo.login;

import lombok.Data;

/* 用户登录之后返回给前端的数据 */
@Data
public class LoginData {
    private String role;    //角色   1代表管理员，2代表学生，3代表教师
    private String token;   //token
    private String name;    //姓名
    private String id;      //用户id
}
