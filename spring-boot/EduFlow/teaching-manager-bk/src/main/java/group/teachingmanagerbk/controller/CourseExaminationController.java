package group.teachingmanagerbk.controller;

import group.teachingmanagerbk.dto.application.CourseApplication;
import group.teachingmanagerbk.security.RequirePermission;
import group.teachingmanagerbk.security.RoleConstants;
import group.teachingmanagerbk.service.CourseExaminationService;
import group.teachingmanagerbk.utils.ReturnResult.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequirePermission({RoleConstants.ROLE_ADMIN})
public class CourseExaminationController {

    @Autowired
    CourseExaminationService courseExaminationService;

    //查询所有待审批的数据
    @GetMapping("/wait/examination")
    public Result getWaitExamination(String examinationName) {
        ArrayList<CourseApplication> data = courseExaminationService.getWaitExamination(examinationName);
        return new Result().success(data);
    }

    //查询所有已审批的数据
    @GetMapping("/already/examination")
    public Result getAlreadyExamination(String examinationName) {
        ArrayList<CourseApplication> data = courseExaminationService.getAlreadyExamination(examinationName);
        return new Result().success(data);
    }

    //审批一条记录
    @PostMapping("/course/examination")
    public Result examineACourse(@RequestBody CourseApplication json) {
        courseExaminationService.examineACourse(json);
        return new Result().success();
    }

}
