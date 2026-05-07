package group.teachingmanagerbk.dto.login;

import lombok.Data;

@Data
public class LoginParam {
    private String account;     //账号
    private String password;    //密码
    private String authority;   //身份权限
}
