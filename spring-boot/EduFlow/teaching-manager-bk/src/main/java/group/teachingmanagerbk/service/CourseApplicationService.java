package group.teachingmanagerbk.service;

import group.teachingmanagerbk.dto.application.CourseApplication;

import java.util.ArrayList;

public interface CourseApplicationService {

    /* 申请新增一门课程 */
    boolean applyANewCourse(CourseApplication json);


    /* 根据id查询全部申请 */
    ArrayList<CourseApplication> getAllApplicationByTeacherId(String teacherId);

    /* 根据id和审批状态查询记录 */
    ArrayList<CourseApplication> getApplicationByTeacherIdAndExaminationName(String teacherId, String courseExaminationName);

    /* 根据申请id查询一条数据 */
    CourseApplication getCourseApplicationById(String courseApplicationId);

}
