package group.teachingmanagerbk.service;

import group.teachingmanagerbk.dto.member.QueryStudentParam;
import group.teachingmanagerbk.dto.member.QueryTeacherParam;
import group.teachingmanagerbk.utils.ReturnResult.ResultWithTotal;
import group.teachingmanagerbk.vo.member.Department;
import group.teachingmanagerbk.vo.member.Student;
import group.teachingmanagerbk.vo.member.Teacher;

import java.util.ArrayList;

/* 人员管理 */
public interface MemberService {

    /**
     * 获取学院的信息
     * @return 学院信息
     */
    ArrayList<Department> getDepartmentInfo();

    /**
     * 获取教师信息
     * @param json 参数
     * @return 带有总计的结果
     */
    ResultWithTotal getTeachersInfo(QueryTeacherParam json);

    /**
     * 获取所有教师信息
     * @return 教师信息列表
     */
    ArrayList<Teacher> getTeachers();

    /**
     * 插入一条教师信息
     * @param json  参数
     * @return true表示插入成功，否则不成功
     */
    boolean insertTeacher(Teacher json);

    /**
     * 根据id删除教师信息
     * @param teacherIds 教师id
     */
    void deleteTeacher(String[] teacherIds);

    /**
     * 根据指定的id查询教师的信息
     * @param teacherId 学生id
     * @return 学生信息
     */
    Teacher getTeacherInfoById(String teacherId);

    /**
     * 根据指定的id更新教师的信息
     * @param teacher 学生数据
     * @return true表示更新成功，否则不成功
     */
    boolean updateTeacherInfoById(Teacher teacher);

    /**
     * 获取学生信息
     * @param json 参数
     * @return 带有总计的结果
     */
    ResultWithTotal getStudentsInfo(QueryStudentParam json);

    /**
     * 插入一条学生信息
     * @param json  参数
     * @return true表示插入成功，否则不成功
     */
    boolean insertStudent(Student json);

    /**
     * 根据id删除学生信息
     * @param studentIds 学生id
     */
    void deleteStudent(String[] studentIds);

    /**
     * 根据指定的id查询学生的信息
     * @param studentId 学生id
     * @return 学生信息
     */
    Student getStudentInfoById(String studentId);

    /**
     * 根据指定的id更新学生的信息
     * @param student 学生数据
     * @return true表示更新成功，否则不成功
     */
    boolean updateStudentInfoById(Student student);

}
