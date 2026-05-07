package group.teachingmanagerbk.controller;

import group.teachingmanagerbk.dto.application.CourseApplication;
import group.teachingmanagerbk.exception.BusinessException;
import group.teachingmanagerbk.security.RequirePermission;
import group.teachingmanagerbk.security.RoleConstants;
import group.teachingmanagerbk.service.CourseApplicationService;
import group.teachingmanagerbk.utils.ReturnResult.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequirePermission({RoleConstants.ROLE_ADMIN, RoleConstants.ROLE_TEACHER})
public class CourseApplicationController {

    @Autowired
    CourseApplicationService courseApplicationService;

    //申请新增一门课程
    @PostMapping("/apply/add/course")
    public Result applyANewCourse(@RequestBody CourseApplication json) {
        if (courseApplicationService.applyANewCourse(json)) {
           return new Result().success();
        }
        throw new BusinessException(400, "申请新增失败，不存在这样的审批或者操作！");
    }

    //申请修改一门课程
    @PostMapping("/apply/modify/course")
    public Result applyModifyCourse(@RequestBody CourseApplication json) {
        throw new BusinessException(400, "申请修改失败，不存在这样的审批或操作！");
    }

    //申请删除一门课程
    @PostMapping("/apply/deltele/course")
    public Result applyDeleteCourse(@RequestBody CourseApplication json) {
        throw new BusinessException(400, "申请删除失败，不存在这样的审批或操作！");
    }

    //根据id查询全部申请
    @GetMapping("/all/application")
    public Result getAllApplicationByTeacherId(String teacherId) {
        ArrayList<CourseApplication> data = courseApplicationService.getAllApplicationByTeacherId(teacherId);
        return new Result().success(data);
    }

    //根据id和审批状态查询记录
    @GetMapping("/application")
    public Result getApplicationByTeacherIdAndExaminationName(String teacherId, String courseExaminationName) {
        ArrayList<CourseApplication> data = courseApplicationService.getApplicationByTeacherIdAndExaminationName(teacherId, courseExaminationName);
        return new Result().success(data);
    }

    //根据申请id查询一条申请记录
    @GetMapping("/get/application")
    public Result getCourseApplicationById(String courseApplicationId) {
        CourseApplication data = courseApplicationService.getCourseApplicationById(courseApplicationId);
        return new Result().success(data);
    }

}
