package group.teachingmanagerbk.controller;

import group.teachingmanagerbk.dto.course.QueryCourseParam;
import group.teachingmanagerbk.dto.course.StudentSelectCourseData;
import group.teachingmanagerbk.security.RequirePermission;
import group.teachingmanagerbk.security.RoleConstants;
import group.teachingmanagerbk.service.CourseService;
import group.teachingmanagerbk.utils.ReturnResult.Result;
import group.teachingmanagerbk.vo.course.Course;
import group.teachingmanagerbk.vo.course.Place;
import group.teachingmanagerbk.vo.member.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class CourseController {

    @Autowired
    CourseService courseService;

    //根据课程id获取一个课程的信息
    @GetMapping("/get/course")
    @RequirePermission({RoleConstants.ROLE_ALL})
    public Result getCourseById(String courseId) {
        Course data = courseService.getCourseById(courseId);
        if (data != null) {
            return new Result().success(data);
        }
        return new Result().error("根据id获取课程信息失败");
    }

    //条件查询课程
    @PostMapping("/get/condition/course")
    @RequirePermission({RoleConstants.ROLE_ALL})
    public Result getCourseByCondition(@RequestBody QueryCourseParam json) {
        return courseService.getCourseByCondition(json);
    }

    //查询所有地点
    @GetMapping("/get/all/place")
    @RequirePermission({RoleConstants.ROLE_ALL})
    public Result getAllPlace() {
        ArrayList<Place> allPlace = courseService.getAllPlace();
        return new Result().success(allPlace);
    }

    //新增一门课程
    @PostMapping("/insert/course")
    @RequirePermission({RoleConstants.ROLE_ADMIN, RoleConstants.ROLE_TEACHER})
    public Result insertANewCourse(@RequestBody Course json) {
        courseService.insertANewCourse(json);
        return new Result().success();
    }

    //更新课程信息
    @PostMapping("/update/course")
    @RequirePermission({RoleConstants.ROLE_ADMIN, RoleConstants.ROLE_TEACHER})
    public Result updateCourse(@RequestBody Course json) {
        courseService.updateCourse(json);
        return new Result().success();
    }

    //删除课程信息
    @PostMapping("/delete/course")
    @RequirePermission({RoleConstants.ROLE_ADMIN, RoleConstants.ROLE_TEACHER})
    public Result deleteCourse(@RequestBody Course json) {
        courseService.deleteCourse(json);
        return new Result().success();
    }

    //获取课选课的开启与关闭的状态
    @GetMapping("/course/switch/status")
    @RequirePermission({RoleConstants.ROLE_ADMIN})
    public Result getCourseSwitchStatus() {
        String courseSwitchStatus = courseService.getCourseSwitchStatus();
        return new Result().success(courseSwitchStatus);
    }

    //批量更新课程的状态(待选->可选，可选->授课中)
    @PutMapping("/update/course/status")
    @RequirePermission({RoleConstants.ROLE_ADMIN})
    public Result updateCourseStatus(String courseSwitchStatus) {
        courseService.updateCourseStatus(courseSwitchStatus);
        return new Result().success();
    }

    //学生选择某一课程
    @PostMapping("/student/select/course")
    @RequirePermission({RoleConstants.ROLE_STUDENT, RoleConstants.ROLE_ADMIN})
    public Result studentSelectCourse(@RequestBody StudentSelectCourseData json) {
        courseService.studentSelectCourse(json);
        return new Result().success();
    }

    //判断某一课程是否被某一学生所选择
    @PostMapping("/select/course/status")
    @RequirePermission({RoleConstants.ROLE_STUDENT, RoleConstants.ROLE_ADMIN})
    public Result judgeCourseSelectedStatus(@RequestBody StudentSelectCourseData json) {
        if (courseService.judgeCourseSelectedStatus(json)) {
            return new Result().success();
        }
        return new Result().error();
    }

    //学生退选某一课程
    @PostMapping("/exit/course")
    @RequirePermission({RoleConstants.ROLE_STUDENT, RoleConstants.ROLE_ADMIN})
    public Result exitCourse(@RequestBody StudentSelectCourseData json) {
        courseService.exitCourse(json);
        return new Result().success();
    }

    //根据学生id查询所有选课信息
    @GetMapping("/student/select/course")
    @RequirePermission({RoleConstants.ROLE_STUDENT, RoleConstants.ROLE_ADMIN})
    public Result getAllSelectedCourse(String studentId) {
        ArrayList<Course> data = courseService.getAllSelectedCourse(studentId);
        return new Result().success(data);
    }

    //根据课程id查询所有选择了该课程的学生
    @GetMapping("/get/select/the/course/students")
    @RequirePermission({RoleConstants.ROLE_TEACHER, RoleConstants.ROLE_ADMIN})
    public Result getSelectTheCourseStudents(String courseId) {
        ArrayList<Student> data = courseService.getSelectTheCourseStudents(courseId);
        return new Result().success(data);
    }

    //给选择了某门课的学生打分
    @PostMapping("/update/student/score")
    @RequirePermission({RoleConstants.ROLE_TEACHER, RoleConstants.ROLE_ADMIN})
    public Result updateStudentScore(@RequestBody StudentSelectCourseData data) {
        courseService.updateStudentScore(data);
        return new Result().success();
    }

    /* 根据教师id查询教师的课程 */
    @GetMapping("/teacher/course/by/id")
    @RequirePermission({RoleConstants.ROLE_TEACHER, RoleConstants.ROLE_ADMIN})
    public Result getTeacherCourse(String teacherId) {
        ArrayList<Course> data = courseService.getTeacherCourse(teacherId);
        return new Result().success(data);
    }

}
