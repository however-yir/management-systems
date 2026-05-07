package group.teachingmanagerbk.service;

import group.teachingmanagerbk.dto.course.QueryCourseParam;
import group.teachingmanagerbk.dto.course.StudentSelectCourseData;
import group.teachingmanagerbk.utils.ReturnResult.Result;
import group.teachingmanagerbk.vo.course.Course;
import group.teachingmanagerbk.vo.course.Place;
import group.teachingmanagerbk.vo.member.Student;

import java.util.ArrayList;

public interface CourseService {
    /**
     * 根据课程id获取一个课程的信息
     * @param courseId  课程id
     * @return  课程信息
     */
    Course getCourseById(String courseId);

    /**
     * 根据条件查询课程信息
     * @param json 查询参数
     * @return  返回课程的列表信息
     */
    Result getCourseByCondition(QueryCourseParam json);

    /**
     * 获取所有地点
     * @return 地点信息列表
     */
    ArrayList<Place> getAllPlace();

    /**
     * 插入一条课程数据
     * @throws Exception 异常
     */
    void insertANewCourse(Course json);

    /**
     * 更新课程信息
     * @param json 课程数据
     */
    void updateCourse(Course json);

    /**
     * 删除课程信息
     * @param json 课程数据
     */
    void deleteCourse(Course json);

    /**
     * 获取课选课的开启与关闭的状态
     * @return 返回状态码
     */
    String getCourseSwitchStatus();

    /**
     * 批量更新课程的状态
     * @param courseSwitchStatus 课程状态
     */
    void updateCourseStatus(String courseSwitchStatus);

    /**
     * 学生选择一门课程
     * @param json 学生选课的数据
     */
    void studentSelectCourse(StudentSelectCourseData json);

    /**
     * 判断用户是否选择了某一课程
     * @param json 选课
     * @return true代表选择了，否则代表没有选择
     */
    boolean judgeCourseSelectedStatus(StudentSelectCourseData json);

    /**
     * 退选某一课程
     * @param json 选课数据
     */
    void exitCourse(StudentSelectCourseData json);

    /**
     * 根据学生id查询
     * @param studentId 学生id
     * @return  课程的信息
     */
    ArrayList<Course> getAllSelectedCourse(String studentId);

    /**
     * 根据课程id获取选择了这门课的学生
     * @return 学生列表信息
     */
    ArrayList<Student> getSelectTheCourseStudents(String courseId);

    /**
     * 根据学生选课信息给更新学生成绩
     * @param data  学生选课信息
     */
    void updateStudentScore(StudentSelectCourseData data);

    /**
     * 根据教师id获取教师所选的课程
     * @param teacherId 教师id
     * @return 课程列表信息
     */
    ArrayList<Course> getTeacherCourse(String teacherId);

}
