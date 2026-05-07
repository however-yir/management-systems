package group.teachingmanagerbk.mapper;

import group.teachingmanagerbk.dto.application.CourseApplication;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface CourseApplicationMapper {

    /* 根据名称查询操作id */
    @Select("select operation_id from operation where name = #{name}")
    String getOperationIdByName(String name);

    /* 根据名称查询审批id */
    @Select("select course_examination_id from course_examination where name = #{name}")
    String getExaminationIdByName(String name);

    /* 插入一条课程申请记录 */
    @Insert("insert into course_application " +
            "(teacher_id, course_name, course_credit, course_hour, " +
            "course_description, operation_id, course_examination_id) values " +
            "(#{teacherId}, #{courseName}, #{courseCredit}, #{courseHour}, " +
            "#{courseDescription}, #{operationId}, #{courseExaminationId})")
    void insertANewApplication(CourseApplication application);

    /* 根据id查询所有申请记录 */
    @Select("select a.*, o.name as operationName, e.name as courseExaminationName, t.name as teacherName " +
            "from course_application a left join course_examination e on a.course_examination_id = e.course_examination_id " +
            "left join operation o on a.operation_id = o.operation_id left join teacher t on a.teacher_id = t.teacher_id " +
            "where a.teacher_id = #{teacherId} order by a.date_time desc")
    ArrayList<CourseApplication> getAllApplicationByTeacherId(String teacherId);

    /* 根据id和审批状态查询记录 */
    @Select("select a.*, o.name as operationName, e.name as courseExaminationName, t.name as teacherName " +
            "from course_application a left join course_examination e on a.course_examination_id = e.course_examination_id " +
            "left join operation o on a.operation_id = o.operation_id left join teacher t on a.teacher_id = t.teacher_id " +
            "where a.teacher_id = #{teacherId} and e.name = #{courseExaminationName} order by a.date_time desc")
    ArrayList<CourseApplication> getApplicationByTeacherIdAndExaminationName(String teacherId, String courseExaminationName);

    /* 根据申请id查询相应的记录 */
    @Select("select a.*, ce.name as courseExaminationName, o.name as operationName, " +
            "p.name as coursePlaceName, t.name as teacherName from course_application a " +
            "left join course_examination ce on a.course_examination_id = ce.course_examination_id " +
            "left join operation o on o.operation_id = a.operation_id " +
            "left join place p on p.place_id = a.course_place_id " +
            "left join teacher t on t.teacher_id = a.teacher_id " +
            "where course_application_id = #{courseApplicationId} order by a.date_time desc")
    CourseApplication getCourseApplicationById(String courseApplicationId);

}
