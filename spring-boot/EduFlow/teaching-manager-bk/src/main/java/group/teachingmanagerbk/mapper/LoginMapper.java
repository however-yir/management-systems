package group.teachingmanagerbk.mapper;

import group.teachingmanagerbk.dto.login.ModifyPasswordParam;
import group.teachingmanagerbk.vo.member.Administrator;
import group.teachingmanagerbk.vo.member.Student;
import group.teachingmanagerbk.vo.member.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LoginMapper {

    /* 查询需要登录的管理员信息 */
    @Select("select administrator_id as administratorId, name " +
            "from administrator where account = #{account} and password = #{password}")
    Administrator administratorLogin(String account, String password);

    /* 查询需要登录的学生信息 */
    @Select("select student_id as studentId, student_number as studentNumber, name, " +
            "student_class as studentClass, date_time as dateTime " +
            "from student where student_number = #{account} and password = #{password}")
    Student studentLogin(String account, String password);

    /* 查询需要登录的教师信息 */
    @Select("select teacher_id as teacherId, teacher_number as teacherNumber, name, " +
            "department_id as departmentId, date_time as dateTime " +
            "from teacher where teacher_number = #{account} and password = #{password}")
    Teacher teacherLogin(String account, String password);

    /* 更新管理员的密码 */
    @Update("update administrator set password = #{password} where administrator_id = #{userId}")
    void updateAdministratorPassword(ModifyPasswordParam data);

    /* 更新学生的密码 */
    @Update("update student set password = #{password} where student_id = #{userId}")
    void updateStudentPassword(ModifyPasswordParam data);

    /* 更新教师的信息 */
    @Update("update teacher set password = #{password} where teacher_id = #{userId}")
    void updateTeacherPassword(ModifyPasswordParam data);

}
