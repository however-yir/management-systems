package group.teachingmanagerbk.mapper;

import group.teachingmanagerbk.dto.application.CourseApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.ArrayList;

@Mapper
public interface CourseExaminationMapper {

    /* 查询所有待审批的记录 */
    @Select("select a.*, o.name as operationName, e.name as courseExaminationName, t.name as teacherName " +
            "from course_application a left join course_examination e on a.course_examination_id = e.course_examination_id " +
            "left join operation o on a.operation_id = o.operation_id left join teacher t on a.teacher_id = t.teacher_id " +
            "where e.name = #{examinationName} order by a.date_time desc")
    ArrayList<CourseApplication> getWaitExamination(String examinationName);

    /* 查询所有已审批的记录 */
    @Select("select a.*, o.name as operationName, e.name as courseExaminationName, t.name as teacherName " +
            "from course_application a left join course_examination e on a.course_examination_id = e.course_examination_id " +
            "left join operation o on a.operation_id = o.operation_id left join teacher t on a.teacher_id = t.teacher_id " +
            "where e.name != #{examinationName} order by a.date_time desc")
    ArrayList<CourseApplication> getAlreadyExamination(String examinationName);

    /* 根据名称查询检查状态的id */
    @Select("select course_examination_id from course_examination where name = #{name}")
    String getCourseExaminationIdByName(String name);

    /* 更新申请的课程id和审批状态信息 */
    @Update("update course_application set course_id = #{courseId}, course_examination_id = #{courseExaminationId} " +
            "where course_application_id = #{courseApplicationId}")
    void updateCourseApplicationCourseIdAndExamination(String courseId, String courseExaminationId, String courseApplicationId);

    /* 更新申请的审批状态信息 */
    @Update("update course_application set course_examination_id = #{courseExaminationId} " +
            "where course_application_id = #{courseApplicationId}")
    void updateCourseApplicationExamination(String courseExaminationId, String courseApplicationId);

}
