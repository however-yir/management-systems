package group.teachingmanagerbk.controller;

import group.teachingmanagerbk.dto.member.QueryStudentParam;
import group.teachingmanagerbk.dto.member.QueryTeacherParam;
import group.teachingmanagerbk.exception.BusinessException;
import group.teachingmanagerbk.security.RequirePermission;
import group.teachingmanagerbk.security.RoleConstants;
import group.teachingmanagerbk.service.MemberService;
import group.teachingmanagerbk.utils.ReturnResult.Result;
import group.teachingmanagerbk.utils.ReturnResult.ResultWithTotal;
import group.teachingmanagerbk.vo.member.Student;
import group.teachingmanagerbk.vo.member.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequirePermission({RoleConstants.ROLE_ADMIN})
public class MemberController {

    @Autowired
    MemberService memberService;

    //获取学院的信息
    @GetMapping("/departments")
    public Result getDepartmentsInfo() {
        return new Result().success(memberService.getDepartmentInfo());
    }

    //获取教师信息
    @PostMapping("/teachers")
    public Result getTeachersInfo(@RequestBody QueryTeacherParam json) {
        log.info(json.toString());
        ResultWithTotal teachersInfo = memberService.getTeachersInfo(json);
        return teachersInfo.success();
    }

    //获取教师信息
    @GetMapping("/get/teachers")
    public Result getTeachers() {
        ArrayList<Teacher> teachers = memberService.getTeachers();
        return new Result().success(teachers);
    }

    //新增教师信息
    @PostMapping("/teacher")
    public Result addTeacherInfo(@RequestBody Teacher json) {
        if (memberService.insertTeacher(json)) {
            return new Result().success();
        }
        throw new BusinessException(400, "插入教师数据失败！");
    }

    //根据id删除教师的信息
    @DeleteMapping("/teachers/{teacherIds}")
    public Result deleteTeachersInfo(@PathVariable String[] teacherIds) {
        log.info("根据id{}删除教师的信息", (Object) teacherIds);
        memberService.deleteTeacher(teacherIds);
        return new Result().success();
    }

    //根据教师id查询对应的数据
    @GetMapping("/get/teacher")
    public Result getTeacherInfoById(@RequestParam String teacherId) {
        Teacher info = memberService.getTeacherInfoById(teacherId);
        return new Result().success(info);
    }

    //根据学生id修改教师信息
    @PutMapping("/modify/teacher")
    public Result modifyStudentInfo(@RequestBody Teacher teacher) {
        log.info(teacher.toString());
        if (memberService.updateTeacherInfoById(teacher)) {
            return new Result().success();
        }
        throw new BusinessException(400, "更新教师信息失败！");
    }

    //获取学生信息
    @PostMapping("/students")
    public Result getStudentsInfo(@RequestBody QueryStudentParam json) {
        log.info(json.toString());
        ResultWithTotal studentsInfo = memberService.getStudentsInfo(json);
        return studentsInfo.success();
    }

    //新增学生信息
    @PostMapping("/student")
    public Result addStudentInfo(@RequestBody Student json) {
        if (memberService.insertStudent(json)) {
            return new Result().success();
        }
        throw new BusinessException(400, "插入学生数据失败！");
    }

    //根据id删除学生的信息
    @DeleteMapping("/students/{studentIds}")
    public Result deleteStudentsInfo(@PathVariable String[] studentIds) {
        log.info("根据id{}删除学生的信息", (Object) studentIds);
        memberService.deleteStudent(studentIds);
        return new Result().success();
    }

    //根据学生id查询对应的数据
    @GetMapping("/get/student")
    public Result getStudentInfoById(@RequestParam String studentId) {
        Student info = memberService.getStudentInfoById(studentId);
        return new Result().success(info);
    }

    //根据学生id修改学生信息
    @PutMapping("/modify/student")
    public Result modifyStudentInfo(@RequestBody Student student) {
        log.info(student.toString());
        if (memberService.updateStudentInfoById(student)) {
            return new Result().success();
        }
        throw new BusinessException(400, "更新学生信息失败！");
    }

}
