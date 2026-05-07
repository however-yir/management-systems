package group.teachingmanagerbk.controller;

import group.teachingmanagerbk.exception.BusinessException;
import group.teachingmanagerbk.dto.login.LoginParam;
import group.teachingmanagerbk.dto.login.ModifyPasswordParam;
import group.teachingmanagerbk.security.RequirePermission;
import group.teachingmanagerbk.security.RoleConstants;
import group.teachingmanagerbk.service.LoginService;
import group.teachingmanagerbk.utils.ReturnResult.Result;
import group.teachingmanagerbk.vo.login.LoginData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    LoginService loginService;

    //登录
    @PostMapping("/login")
    public Result login(@RequestBody LoginParam json) {
        log.info(json.toString());
        // 1代表管理员，2代表学生，3代表教师
        LoginData data = loginService.login(json);
        if (data == null) {
            throw new BusinessException(401, "登录失败！");
        }
        return new Result().success(data);
    }

    //检查用户登录
    @PostMapping("/check/login")
    public Result checkLogin(@RequestBody LoginData data) {
        try {
            loginService.checkLogin(data);
            return new Result().success();
        } catch (Exception e) {
            return new Result().error(e.getMessage());
        }
    }

    //更新用户密码
    @PostMapping("/modify/user/password")
    @RequirePermission({RoleConstants.ROLE_ALL})
    public Result modifyUserPassword(@RequestBody ModifyPasswordParam json) {
        loginService.modifyUserPassword(json);
        return new Result().success();
    }

}
