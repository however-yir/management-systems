package group.teachingmanagerbk.service.impl;

import group.teachingmanagerbk.dto.application.CourseApplication;
import group.teachingmanagerbk.exception.BusinessException;
import group.teachingmanagerbk.mapper.CourseExaminationMapper;
import group.teachingmanagerbk.mapper.CourseMapper;
import group.teachingmanagerbk.service.CourseExaminationService;
import group.teachingmanagerbk.vo.course.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class CourseExaminationServiceImpl  implements CourseExaminationService {

    @Autowired
    CourseExaminationMapper courseExaminationMapper;
    @Autowired
    CourseMapper courseMapper;

    @Override
    public ArrayList<CourseApplication> getWaitExamination(String examinationName) {
        return courseExaminationMapper.getWaitExamination(examinationName);
    }

    @Override
    public ArrayList<CourseApplication> getAlreadyExamination(String examinationName) {
        return courseExaminationMapper.getAlreadyExamination(examinationName);
    }

    @Override
    public void examineACourse(CourseApplication json) {
        //首先判断通过还是不通过
        String courseExaminationName = json.getCourseExaminationName();
        String courseApplicationId = json.getCourseApplicationId();
        String courseExaminationId = courseExaminationMapper.getCourseExaminationIdByName(courseExaminationName);
        if ("未通过".equals(courseExaminationName)) {
            //更新检查状态
            courseExaminationMapper.updateCourseApplicationExamination(courseExaminationId, courseApplicationId);
            return;
        }
        //判断是否是合法的检查名称
        if (courseExaminationId == null) {
            throw new BusinessException(400, "查询检查状态id错误，请检查前端传过来的检查名称是否正确！");
        }
        //接下来是通过之后的操作
        String operationName = json.getOperationName();
        switch (operationName) {
            case "新增" -> {
                Course newCourse = buildANewCourseInfo(json);
                courseMapper.insertCourse(newCourse);
                log.info(newCourse.getCourseId());
                courseExaminationMapper.updateCourseApplicationCourseIdAndExamination(newCourse.getCourseId(), courseExaminationId, courseApplicationId);
            }
            case "修改" -> {
                Course modifiedCourse = buildModifiedCourseInfo(json);
                courseMapper.modifyCourse(modifiedCourse);
                courseExaminationMapper.updateCourseApplicationExamination(courseExaminationId, courseApplicationId);
            }
            case "删除" -> courseMapper.deleteCourseByIds(new String[]{json.getCourseId()});
        }
    }

    //根据申请构建一个新的课程信息
    private Course buildANewCourseInfo(CourseApplication courseApplication) {
        Course course = new Course();
        //根据名称获取课程状态id
        String courseStatusId = courseMapper.getCourseStatusIdByName("等待课程安排");
        if (courseStatusId == null) {
            throw new BusinessException(500, "后端错误：所确定的课程状态在数据库中不存在，后端请修改！");
        }
        buildCommonData(course, courseApplication);
        course.setCourseStatusId(courseStatusId);
        return course;
    }

    //根据申请修改一个课程信息
    private Course buildModifiedCourseInfo(CourseApplication courseApplication) {
        Course course = new Course();
        buildCommonData(course, courseApplication);
        String courseId = courseApplication.getCourseId();
        course.setCourseId(courseId);
        return course;
    }

    //构建新建课程与修改课程之间相同部分的数据
    private void buildCommonData(Course course, CourseApplication courseApplication) {
        //读取申请里面的信息
        String name = courseApplication.getCourseName();
        String teacherId = courseApplication.getTeacherId();
        String credit = courseApplication.getCourseCredit();
        String hour = courseApplication.getCourseHour();
        String time = courseApplication.getCourseTime();
        String placeId = courseApplication.getCoursePlaceId();
        String description = courseApplication.getCourseDescription();
        //设置课程信息
        course.setName(name);
        course.setTeacherId(teacherId);
        course.setCredit(credit);
        course.setHour(hour);
        course.setTime(time);
        course.setPlaceId(placeId);
        course.setDescription(description);
    }

}
