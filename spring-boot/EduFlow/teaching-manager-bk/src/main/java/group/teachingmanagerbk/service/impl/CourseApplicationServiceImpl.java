package group.teachingmanagerbk.service.impl;

import group.teachingmanagerbk.dto.application.CourseApplication;
import group.teachingmanagerbk.mapper.CourseApplicationMapper;
import group.teachingmanagerbk.service.CourseApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CourseApplicationServiceImpl implements CourseApplicationService {

    @Autowired
    CourseApplicationMapper courseApplicationMapper;

    @Override
    public boolean applyANewCourse(CourseApplication json) {
        String operationName = json.getOperationName();
        String operationId = courseApplicationMapper.getOperationIdByName(operationName);
        String courseExaminationName = json.getCourseExaminationName();
        String examinationId = courseApplicationMapper.getExaminationIdByName(courseExaminationName);
        if (operationId == null || examinationId == null) {
            return false;
        }
        json.setOperationId(operationId);
        json.setCourseExaminationId(examinationId);
        courseApplicationMapper.insertANewApplication(json);
        return true;
    }

    @Override
    public ArrayList<CourseApplication> getAllApplicationByTeacherId(String teacherId) {
        return courseApplicationMapper.getAllApplicationByTeacherId(teacherId);
    }

    @Override
    public ArrayList<CourseApplication> getApplicationByTeacherIdAndExaminationName(String teacherId, String courseExaminationName) {
        return courseApplicationMapper.getApplicationByTeacherIdAndExaminationName(teacherId, courseExaminationName);
    }

    @Override
    public CourseApplication getCourseApplicationById(String courseApplicationId) {
        return courseApplicationMapper.getCourseApplicationById(courseApplicationId);
    }

}
