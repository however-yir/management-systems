package group.teachingmanagerbk.service.impl;

import group.teachingmanagerbk.dto.course.QueryCourseParam;
import group.teachingmanagerbk.dto.course.StudentSelectCourseData;
import group.teachingmanagerbk.exception.BusinessException;
import group.teachingmanagerbk.exception.EnrollmentConflictException;
import group.teachingmanagerbk.mapper.CourseMapper;
import group.teachingmanagerbk.mapper.MemberMapper;
import group.teachingmanagerbk.service.CourseService;
import group.teachingmanagerbk.utils.ReturnResult.Result;
import group.teachingmanagerbk.utils.ReturnResult.ResultWithTotal;
import group.teachingmanagerbk.vo.course.Course;
import group.teachingmanagerbk.vo.course.Place;
import group.teachingmanagerbk.vo.member.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private static final String LOCK_MODE_PESSIMISTIC = "PESSIMISTIC";
    private static final String LOCK_MODE_OPTIMISTIC = "OPTIMISTIC";

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    MemberMapper memberMapper;

    @Value("${eduflow.enrollment-lock-mode:PESSIMISTIC}")
    private String enrollmentLockMode;

    @Override
    public Course getCourseById(String courseId) {
        return courseMapper.getCourseInfoByCourseId(courseId);
    }

    @Override
    public Result getCourseByCondition(QueryCourseParam json) {
        Integer currentPage = json.getCurrentPage();
        Integer pageSize = json.getPageSize();
        Integer index = pageSize * (currentPage - 1);
        Course param = json.getParam();
        int total = courseMapper.getCourseCountByCondition(param);
        ArrayList<Course> courses = courseMapper.getCourseByCondition(param, index, pageSize);
        ResultWithTotal result = new ResultWithTotal();
        result.setTotal(total);
        result.setData(courses);
        return result.success();
    }

    @Override
    public ArrayList<Place> getAllPlace() {
        return courseMapper.getAllPlace();
    }

    @Override
    public void insertANewCourse(Course json) {
        courseMapper.insertCourse(json);
    }

    @Override
    public void updateCourse(Course json) {
        courseMapper.modifyCourse(json);
    }

    @Override
    public void deleteCourse(Course json) {
        String courseId = json.getCourseId();
        courseMapper.deleteCourseByIds(new String[] {courseId});
    }

    @Override
    public String getCourseSwitchStatus() {
        return courseMapper.getCourseSwitchStatus();
    }

    @Override
    public void updateCourseStatus(String courseSwitchStatus) {
        if ("1".equals(courseSwitchStatus)) {
            String oleCourseStatusId = courseMapper.getCourseStatusIdByName("待选");
            String newCourseStatusId = courseMapper.getCourseStatusIdByName("可选");
            courseMapper.updateCourseStatus(newCourseStatusId,oleCourseStatusId);
            courseMapper.updateCourseSwitchStatus(courseSwitchStatus);
        } else if ("0".equals(courseSwitchStatus)) {
            String oleCourseStatusId = courseMapper.getCourseStatusIdByName("可选");
            String newCourseStatusId = courseMapper.getCourseStatusIdByName("授课中");
            courseMapper.updateCourseStatus(newCourseStatusId,oleCourseStatusId);
            courseMapper.updateCourseSwitchStatus(courseSwitchStatus);
        } else {
            throw new BusinessException(400, "传入的切换状态码有误！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void studentSelectCourse(StudentSelectCourseData json) {
        String studentId = json.getStudentId();
        Student student = memberMapper.getStudentInfoById(studentId);
        if (student == null) {
            throw new BusinessException(404, "学生不存在！");
        }
        String courseId = json.getCourseId();
        Course course = courseMapper.getCourseInfoByCourseId(courseId);
        if (course == null) {
            throw new BusinessException(404, "课程不存在！");
        }
        if (!canSelectByStatus(course.getCourseStatusName())) {
            throw new BusinessException(400, "当前课程状态不允许选课！");
        }
        if (hasTimeConflict(studentId, course.getTime())) {
            throw new EnrollmentConflictException(studentId, courseId, "该课程与已选课程上课时间冲突！");
        }

        if (isOptimisticMode()) {
            handleEnrollmentByOptimisticLock(json, course);
            return;
        }
        handleEnrollmentByPessimisticLock(json);
    }

    @Override
    public boolean judgeCourseSelectedStatus(StudentSelectCourseData json) {
        StudentSelectCourseData data = courseMapper.selectCoursesStudents(json);
        return data != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitCourse(StudentSelectCourseData json) {
        StudentSelectCourseData data = courseMapper.selectCoursesStudents(json);
        if (data == null) {
            throw new BusinessException(404, "学生退选的课程不存在！");
        }
        courseMapper.deleteCoursesStudents(json);
        courseMapper.decreaseCurrentStudents(json.getCourseId());
    }

    @Override
    public ArrayList<Course> getAllSelectedCourse(String studentId) {
        return courseMapper.getCourseByStudentId(studentId);
    }

    @Override
    public ArrayList<Student> getSelectTheCourseStudents(String courseId) {
        return courseMapper.getSelectTheCourseStudents(courseId);
    }

    @Override
    public void updateStudentScore(StudentSelectCourseData data) {
        courseMapper.updateStudentScore(data);
    }

    @Override
    public ArrayList<Course> getTeacherCourse(String teacherId) {
        return courseMapper.getTeacherCourse(teacherId);
    }

    private boolean canSelectByStatus(String courseStatusName) {
        if (courseStatusName == null) {
            return false;
        }
        return "可选".equals(courseStatusName) || "待选".equals(courseStatusName);
    }

    private boolean isOptimisticMode() {
        return LOCK_MODE_OPTIMISTIC.equalsIgnoreCase(enrollmentLockMode);
    }

    private void handleEnrollmentByPessimisticLock(StudentSelectCourseData json) {
        String studentId = json.getStudentId();
        String courseId = json.getCourseId();
        Course lockedCourse = courseMapper.lockCourseById(courseId);
        if (lockedCourse == null) {
            throw new BusinessException(404, "课程不存在！");
        }
        if (this.judgeCourseSelectedStatus(json)) {
            throw new EnrollmentConflictException(studentId, courseId, "课程已经被该学生选择！");
        }
        ensureCourseCapacity(lockedCourse, studentId, courseId);
        try {
            courseMapper.insertCoursesStudents(json);
        } catch (DuplicateKeyException e) {
            throw new EnrollmentConflictException(studentId, courseId, "课程已经被该学生选择！");
        }
        courseMapper.increaseCurrentStudentsPessimistic(courseId);
    }

    private void handleEnrollmentByOptimisticLock(StudentSelectCourseData json, Course course) {
        String studentId = json.getStudentId();
        String courseId = json.getCourseId();
        if (this.judgeCourseSelectedStatus(json)) {
            throw new EnrollmentConflictException(studentId, courseId, "课程已经被该学生选择！");
        }
        ensureCourseCapacity(course, studentId, courseId);
        try {
            courseMapper.insertCoursesStudents(json);
        } catch (DuplicateKeyException e) {
            throw new EnrollmentConflictException(studentId, courseId, "课程已经被该学生选择！");
        }
        Integer version = Optional.ofNullable(course.getVersion()).orElse(0);
        int affectedRows = courseMapper.increaseCurrentStudentsOptimistic(courseId, version);
        if (affectedRows == 0) {
            throw new EnrollmentConflictException(studentId, courseId, "该课程已选或选课人数已满");
        }
    }

    private void ensureCourseCapacity(Course course, String studentId, String courseId) {
        Integer maxStudents = Optional.ofNullable(course.getMaxStudents()).orElse(0);
        if (maxStudents <= 0) {
            return;
        }
        Integer currentStudents = Optional.ofNullable(course.getCurrentStudents()).orElse(0);
        if (currentStudents >= maxStudents) {
            throw new EnrollmentConflictException(studentId, courseId, "该课程已选或选课人数已满");
        }
    }

    private boolean hasTimeConflict(String studentId, String targetTime) {
        if (targetTime == null || targetTime.isBlank()) {
            return false;
        }
        ArrayList<Course> selectedCourses = courseMapper.getCourseByStudentId(studentId);
        if (selectedCourses == null || selectedCourses.isEmpty()) {
            return false;
        }
        for (Course selectedCourse : selectedCourses) {
            if (Objects.equals(targetTime, selectedCourse.getTime())) {
                return true;
            }
        }
        return false;
    }

}
