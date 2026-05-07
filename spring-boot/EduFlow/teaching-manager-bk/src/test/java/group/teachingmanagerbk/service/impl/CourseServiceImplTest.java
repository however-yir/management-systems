package group.teachingmanagerbk.service.impl;

import group.teachingmanagerbk.dto.course.StudentSelectCourseData;
import group.teachingmanagerbk.exception.BusinessException;
import group.teachingmanagerbk.exception.EnrollmentConflictException;
import group.teachingmanagerbk.mapper.CourseMapper;
import group.teachingmanagerbk.mapper.MemberMapper;
import group.teachingmanagerbk.vo.course.Course;
import group.teachingmanagerbk.vo.member.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseMapper courseMapper;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void shouldRejectSelectionWhenTimeConflicts() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        Student student = buildStudent("s1");
        Course targetCourse = buildSelectableCourse("c2", "Mon-1");
        Course selectedCourse = buildSelectableCourse("c1", "Mon-1");

        when(memberMapper.getStudentInfoById("s1")).thenReturn(student);
        when(courseMapper.getCourseInfoByCourseId("c2")).thenReturn(targetCourse);
        ArrayList<Course> selectedCourses = new ArrayList<>();
        selectedCourses.add(selectedCourse);
        when(courseMapper.getCourseByStudentId("s1")).thenReturn(selectedCourses);

        EnrollmentConflictException ex = Assertions.assertThrows(EnrollmentConflictException.class, () -> courseService.studentSelectCourse(data));
        Assertions.assertEquals("该课程与已选课程上课时间冲突！", ex.getMessage());
        verify(courseMapper, never()).insertCoursesStudents(any(StudentSelectCourseData.class));
    }

    @Test
    void shouldRejectSelectionWhenCourseNotSelectable() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        Student student = buildStudent("s1");
        Course targetCourse = buildSelectableCourse("c2", "Mon-3");
        targetCourse.setCourseStatusName("授课中");

        when(memberMapper.getStudentInfoById("s1")).thenReturn(student);
        when(courseMapper.getCourseInfoByCourseId("c2")).thenReturn(targetCourse);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> courseService.studentSelectCourse(data));
        Assertions.assertEquals("当前课程状态不允许选课！", ex.getMessage());
        verify(courseMapper, never()).insertCoursesStudents(any(StudentSelectCourseData.class));
    }

    @Test
    void shouldRejectSelectionWhenCourseIsFull() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        Student student = buildStudent("s1");
        Course targetCourse = buildSelectableCourse("c2", "Mon-3");
        targetCourse.setMaxStudents(1);
        targetCourse.setCurrentStudents(1);

        Course lockedCourse = buildSelectableCourse("c2", "Mon-3");
        lockedCourse.setMaxStudents(1);
        lockedCourse.setCurrentStudents(1);

        usePessimisticMode();
        when(memberMapper.getStudentInfoById("s1")).thenReturn(student);
        when(courseMapper.getCourseInfoByCourseId("c2")).thenReturn(targetCourse);
        when(courseMapper.getCourseByStudentId("s1")).thenReturn(new ArrayList<>());
        when(courseMapper.lockCourseById("c2")).thenReturn(lockedCourse);
        when(courseMapper.selectCoursesStudents(data)).thenReturn(null);

        EnrollmentConflictException ex = Assertions.assertThrows(EnrollmentConflictException.class, () -> courseService.studentSelectCourse(data));
        Assertions.assertEquals("该课程已选或选课人数已满", ex.getMessage());
        verify(courseMapper, never()).insertCoursesStudents(any(StudentSelectCourseData.class));
    }

    @Test
    void shouldRejectSelectionWhenOptimisticVersionConflicts() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        Student student = buildStudent("s1");
        Course targetCourse = buildSelectableCourse("c2", "Mon-3");
        targetCourse.setMaxStudents(60);
        targetCourse.setCurrentStudents(10);
        targetCourse.setVersion(3);

        useOptimisticMode();
        when(memberMapper.getStudentInfoById("s1")).thenReturn(student);
        when(courseMapper.getCourseInfoByCourseId("c2")).thenReturn(targetCourse);
        when(courseMapper.getCourseByStudentId("s1")).thenReturn(new ArrayList<>());
        when(courseMapper.selectCoursesStudents(data)).thenReturn(null);
        when(courseMapper.increaseCurrentStudentsOptimistic("c2", 3)).thenReturn(0);

        EnrollmentConflictException ex = Assertions.assertThrows(EnrollmentConflictException.class, () -> courseService.studentSelectCourse(data));
        Assertions.assertEquals("该课程已选或选课人数已满", ex.getMessage());
        verify(courseMapper, times(1)).insertCoursesStudents(data);
    }

    @Test
    void shouldRejectSelectionWhenStudentNotFound() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        when(memberMapper.getStudentInfoById("s1")).thenReturn(null);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> courseService.studentSelectCourse(data));
        Assertions.assertEquals(404, ex.getCode());
        Assertions.assertEquals("学生不存在！", ex.getMessage());
        verifyNoInteractions(courseMapper);
    }

    @Test
    void shouldRejectSelectionWhenCourseNotFound() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        Student student = buildStudent("s1");
        when(memberMapper.getStudentInfoById("s1")).thenReturn(student);
        when(courseMapper.getCourseInfoByCourseId("c2")).thenReturn(null);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> courseService.studentSelectCourse(data));
        Assertions.assertEquals(404, ex.getCode());
        Assertions.assertEquals("课程不存在！", ex.getMessage());
        verify(courseMapper, never()).insertCoursesStudents(any(StudentSelectCourseData.class));
    }

    @Test
    void shouldRejectSelectionWhenAlreadySelectedWithPessimisticLock() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        Student student = buildStudent("s1");
        Course targetCourse = buildSelectableCourse("c2", "Mon-3");
        Course lockedCourse = buildSelectableCourse("c2", "Mon-3");
        lockedCourse.setMaxStudents(60);
        lockedCourse.setCurrentStudents(1);
        usePessimisticMode();

        when(memberMapper.getStudentInfoById("s1")).thenReturn(student);
        when(courseMapper.getCourseInfoByCourseId("c2")).thenReturn(targetCourse);
        when(courseMapper.getCourseByStudentId("s1")).thenReturn(new ArrayList<>());
        when(courseMapper.lockCourseById("c2")).thenReturn(lockedCourse);
        when(courseMapper.selectCoursesStudents(data)).thenReturn(new StudentSelectCourseData());

        EnrollmentConflictException ex = Assertions.assertThrows(EnrollmentConflictException.class, () -> courseService.studentSelectCourse(data));
        Assertions.assertEquals("课程已经被该学生选择！", ex.getMessage());
        verify(courseMapper, never()).insertCoursesStudents(any(StudentSelectCourseData.class));
    }

    @Test
    void shouldSelectCourseSuccessfullyWithPessimisticLock() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        Student student = buildStudent("s1");
        Course targetCourse = buildSelectableCourse("c2", "Mon-3");
        Course lockedCourse = buildSelectableCourse("c2", "Mon-3");
        lockedCourse.setMaxStudents(60);
        lockedCourse.setCurrentStudents(10);
        usePessimisticMode();

        when(memberMapper.getStudentInfoById("s1")).thenReturn(student);
        when(courseMapper.getCourseInfoByCourseId("c2")).thenReturn(targetCourse);
        when(courseMapper.getCourseByStudentId("s1")).thenReturn(new ArrayList<>());
        when(courseMapper.lockCourseById("c2")).thenReturn(lockedCourse);
        when(courseMapper.selectCoursesStudents(data)).thenReturn(null);

        courseService.studentSelectCourse(data);

        verify(courseMapper).insertCoursesStudents(data);
        verify(courseMapper).increaseCurrentStudentsPessimistic("c2");
        verify(courseMapper, never()).increaseCurrentStudentsOptimistic(anyString(), anyInt());
    }

    @Test
    void shouldSelectCourseSuccessfullyWithOptimisticLock() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        Student student = buildStudent("s1");
        Course targetCourse = buildSelectableCourse("c2", "Mon-3");
        targetCourse.setMaxStudents(60);
        targetCourse.setCurrentStudents(10);
        targetCourse.setVersion(4);
        useOptimisticMode();

        when(memberMapper.getStudentInfoById("s1")).thenReturn(student);
        when(courseMapper.getCourseInfoByCourseId("c2")).thenReturn(targetCourse);
        when(courseMapper.getCourseByStudentId("s1")).thenReturn(new ArrayList<>());
        when(courseMapper.selectCoursesStudents(data)).thenReturn(null);
        when(courseMapper.increaseCurrentStudentsOptimistic("c2", 4)).thenReturn(1);

        courseService.studentSelectCourse(data);

        verify(courseMapper).insertCoursesStudents(data);
        verify(courseMapper).increaseCurrentStudentsOptimistic("c2", 4);
        verify(courseMapper, never()).lockCourseById(anyString());
    }

    @Test
    void shouldRejectExitCourseWhenSelectionNotFound() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        when(courseMapper.selectCoursesStudents(data)).thenReturn(null);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> courseService.exitCourse(data));
        Assertions.assertEquals(404, ex.getCode());
        Assertions.assertEquals("学生退选的课程不存在！", ex.getMessage());
        verify(courseMapper, never()).deleteCoursesStudents(any(StudentSelectCourseData.class));
        verify(courseMapper, never()).decreaseCurrentStudents(anyString());
    }

    @Test
    void shouldExitCourseSuccessfully() {
        StudentSelectCourseData data = buildSelection("s1", "c2");
        when(courseMapper.selectCoursesStudents(data)).thenReturn(new StudentSelectCourseData());

        courseService.exitCourse(data);

        verify(courseMapper).deleteCoursesStudents(data);
        verify(courseMapper).decreaseCurrentStudents("c2");
    }

    private StudentSelectCourseData buildSelection(String studentId, String courseId) {
        StudentSelectCourseData data = new StudentSelectCourseData();
        data.setStudentId(studentId);
        data.setCourseId(courseId);
        return data;
    }

    private Student buildStudent(String studentId) {
        Student student = new Student();
        student.setStudentId(studentId);
        return student;
    }

    private Course buildSelectableCourse(String courseId, String time) {
        Course course = new Course();
        course.setCourseId(courseId);
        course.setCourseStatusName("可选");
        course.setTime(time);
        return course;
    }

    private void usePessimisticMode() {
        ReflectionTestUtils.setField(courseService, "enrollmentLockMode", "PESSIMISTIC");
    }

    private void useOptimisticMode() {
        ReflectionTestUtils.setField(courseService, "enrollmentLockMode", "OPTIMISTIC");
    }
}
