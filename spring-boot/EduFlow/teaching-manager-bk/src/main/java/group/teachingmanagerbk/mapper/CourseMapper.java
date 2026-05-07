package group.teachingmanagerbk.mapper;

import group.teachingmanagerbk.dto.course.StudentSelectCourseData;
import group.teachingmanagerbk.vo.course.Course;
import group.teachingmanagerbk.vo.course.Place;
import group.teachingmanagerbk.vo.member.Student;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;

@Mapper
public interface CourseMapper {

    /* 通过课程状态名称获取课程状态id */
    @Select("select course_status_id from course_status where name = #{name}")
    String getCourseStatusIdByName(String name);

    /* 新增一条课程的记录,把课程id返回去 */
    void insertCourse(Course course);

    /* 通过课程id删除课程记录 */
    void deleteCourseByIds(String[] courseIds);

    /* 修改课程的信息 */
    void modifyCourse(Course course);

    /* 通过课程id查询课程信息 */
    @Select("select c.*, p.name as placeName, cs.name as courseStatusName, t.name as teacherName from course c " +
            "left join place p on c.place_id = p.place_id " +
            "left join course_status cs on cs.course_status_id = c.course_status_id " +
            "left join teacher t on c.teacher_id = t.teacher_id " +
            "where c.course_id = #{courseId}")
    Course getCourseInfoByCourseId(String courseId);

    /* 课程行级锁，供悲观锁选课使用 */
    @Select("select c.*, p.name as placeName, cs.name as courseStatusName, t.name as teacherName from course c " +
            "left join place p on c.place_id = p.place_id " +
            "left join course_status cs on cs.course_status_id = c.course_status_id " +
            "left join teacher t on c.teacher_id = t.teacher_id " +
            "where c.course_id = #{courseId} for update")
    Course lockCourseById(String courseId);

    /* 根据条件查询出来的总数量 */
    int getCourseCountByCondition(Course param);

    /* 通过条件查询课程的信息(分页） 分页查询还是有点问题的 */
    ArrayList<Course> getCourseByCondition(Course param, Integer index, Integer size);

    /* 获取所有地点 */
    @Select("select place_id as placeId, name as placeName from place")
    ArrayList<Place> getAllPlace();

    /* 获取选课开启或关闭的状态码 */
    @Select("select status from course_switch where course_switch_id = 1")
    String getCourseSwitchStatus();

    /* 更新选课开启或关闭的状态码 */
    @Update("update course_switch set status = #{newStatus} where  course_switch_id = 1")
    void updateCourseSwitchStatus(String newStatus);

    /* 批量更新课程的状态信息 */
    @Update("update course set course_status_id = #{newCourseStatusId} " +
            "where course_status_id = #{oldCourseStatusId}")
    void updateCourseStatus(String newCourseStatusId, String oldCourseStatusId);

    /* 插入学生选课数据 */
    @Insert("insert into courses_students (course_id, student_id) " +
            "values (#{courseId},#{studentId})")
    void insertCoursesStudents(StudentSelectCourseData data);

    /* 悲观锁策略下递增已选人数 */
    @Update("update course set current_students = current_students + 1 where course_id = #{courseId}")
    void increaseCurrentStudentsPessimistic(String courseId);

    /* 乐观锁策略下递增已选人数并推进版本号 */
    @Update("update course set current_students = current_students + 1, version = version + 1 " +
            "where course_id = #{courseId} and version = #{version} and current_students < max_students")
    int increaseCurrentStudentsOptimistic(@Param("courseId") String courseId, @Param("version") Integer version);

    /* 根据课程id和学生id查询选课信息 */
    @Select("select courses_students_id as coursesStudentsId, course_id as courseId, " +
            "student_id as studentId, score " +
            "from courses_students " +
            "where course_id = #{courseId} and student_id = #{studentId}")
    StudentSelectCourseData selectCoursesStudents(StudentSelectCourseData json);

    /* 根据课程id和学生id删除选课信息 */
    @Delete("delete from courses_students where course_id = #{courseId} and student_id = #{studentId} ")
    void deleteCoursesStudents(StudentSelectCourseData json);

    /* 退课后回收已选人数 */
    @Update("update course set current_students = case when current_students > 0 then current_students - 1 else 0 end " +
            "where course_id = #{courseId}")
    void decreaseCurrentStudents(String courseId);

    /* 根据学生id查询选课信息以及课程成绩 */
    @Select("select c.*, cs.score, p.name as placeName, " +
            "cs2.name as courseStatusName, t.name as teacherName from course  c " +
            "left join courses_students cs on c.course_id = cs.course_id " +
            "left join student s on cs.student_id = s.student_id " +
            "left join place p on p.place_id = c.place_id " +
            "left join course_status cs2 on c.course_status_id = cs2.course_status_id " +
            "left join teacher t on t.teacher_id = c.teacher_id " +
            "where s.student_id =#{studentId}")
    ArrayList<Course> getCourseByStudentId(String studentId);

    /* 根据课程id查询所有选择了该课程的学生信息 */
    @Select("select s.*, cs.score from courses_students cs " +
            "left join student s on cs.student_id = s.student_id " +
            "where cs.course_id = #{courseId}")
    ArrayList<Student> getSelectTheCourseStudents(String courseId);

    /* 根据条件更新学生选课的成绩 */
    @Update("update courses_students set score = #{score} " +
            "where course_id = #{courseId} and student_id = #{studentId}")
    void updateStudentScore(StudentSelectCourseData data);

    /* 根据教师id查询课程信息 */
    @Select("select c.*, cs.name as courseStatusName from course c " +
            "left join teacher t on c.teacher_id = t.teacher_id " +
            "left join course_status cs on c.course_status_id = cs.course_status_id " +
            "where c.teacher_id = #{teacherId}")
    ArrayList<Course> getTeacherCourse(String teacherId);

}
