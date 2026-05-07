package group.teachingmanagerbk.service.impl;

import group.teachingmanagerbk.dto.member.QueryStudentParam;
import group.teachingmanagerbk.dto.member.QueryTeacherParam;
import group.teachingmanagerbk.mapper.MemberMapper;
import group.teachingmanagerbk.service.MemberService;
import group.teachingmanagerbk.utils.ReturnResult.ResultWithTotal;
import group.teachingmanagerbk.vo.member.Department;
import group.teachingmanagerbk.vo.member.Student;
import group.teachingmanagerbk.vo.member.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberMapper memberMapper;

    @Override
    public ArrayList<Department> getDepartmentInfo() {
        return memberMapper.getDepartmentInfo();
    }

    @Override
    public ResultWithTotal getTeachersInfo(QueryTeacherParam json) {
        Teacher param = json.getParam();
        Integer pageSize = json.getPageSize();
        Integer currentPage = json.getCurrentPage();
        Integer index = pageSize * (currentPage - 1);
        int total = memberMapper.getTeacherCount(param);
        ArrayList<Teacher> teacherInfo = memberMapper.getTeacherInfo(param, index, pageSize);
        ResultWithTotal result = new ResultWithTotal();
        result.setTotal(total);
        result.setData(teacherInfo);
        return result;
    }

    @Override
    public ArrayList<Teacher> getTeachers() {
        return memberMapper.getTeachers();
    }

    @Override
    public boolean insertTeacher(Teacher json) {
        //需要先判断json中的数据是否为空
        if (checkTeacherBlank(json)) return false;
        memberMapper.insertTeacherInfo(json);
        return true;
    }

    @Override
    public void deleteTeacher(String[] teacherIds) {
        memberMapper.deleteTeachersInfo(teacherIds);
    }

    @Override
    public Teacher getTeacherInfoById(String teacherId) {
        return memberMapper.getTeacherInfoById(teacherId);
    }

    @Override
    public boolean updateTeacherInfoById(Teacher teacher) {
        //需要先判断student中的数据是否为空
        if (checkTeacherBlank(teacher)) return false;
        memberMapper.updateTeacherInfoById(teacher);
        return true;
    }

    @Override
    public ResultWithTotal getStudentsInfo(QueryStudentParam json) {
        Student param = json.getParam();
        Integer pageSize = json.getPageSize();
        Integer currentPage = json.getCurrentPage();
        Integer index = pageSize * (currentPage - 1);
        int total = memberMapper.getStudentCount(param);
        ArrayList<Student> studentInfo = memberMapper.getStudentInfo(param, index, pageSize);
        ResultWithTotal result = new ResultWithTotal();
        result.setTotal(total);
        result.setData(studentInfo);
        return result;
    }

    @Override
    public boolean insertStudent(Student json) {
        //需要先判断json中的数据是否为空
        if (checkStudentBlank(json)) return false;
        memberMapper.insertStudentInfo(json);
        return true;
    }

    @Override
    public void deleteStudent(String[] studentIds) {
        memberMapper.deleteStudentsInfo(studentIds);
    }

    @Override
    public Student getStudentInfoById(String studentId) {
        return memberMapper.getStudentInfoById(studentId);
    }

    @Override
    public boolean updateStudentInfoById(Student student) {
        //需要先判断student中的数据是否为空
        if (checkStudentBlank(student)) return false;
        memberMapper.updateStudentInfoById(student);
        return true;
    }

    private boolean checkStudentBlank(Student student) {
        String studentNumber = student.getStudentNumber();
        String str1 = studentNumber.replaceAll(" ", "");
        String name = student.getName();
        String str2 = name.replaceAll(" ", "");
        String studentClass = student.getStudentClass();
        String str3 = studentClass.replaceAll(" ", "");
        return "".equals(str1) || "".equals(str2) || "".equals(str3);
    }

    private  boolean checkTeacherBlank(Teacher teacher) {
        String teacherNumber = teacher.getTeacherNumber();
        String str1 = teacherNumber.replaceAll(" ","");
        String name = teacher.getName();
        String str2 = name.replaceAll(" ","");
        String departmentName = teacher.getDepartmentName();
        String str3 = departmentName.replaceAll(" ","");
        String departmentId = teacher.getDepartmentId();
        String str4 = departmentId.replaceAll(" ", "");
        return "".equals(str1) || "".equals(str2) || "".equals(str3) || "".equals(str4);
    }

}
