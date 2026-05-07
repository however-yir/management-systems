package group.teachingmanagerbk.service.impl;

import group.teachingmanagerbk.dto.application.CourseApplication;
import group.teachingmanagerbk.exception.BusinessException;
import group.teachingmanagerbk.mapper.CourseExaminationMapper;
import group.teachingmanagerbk.mapper.CourseMapper;
import group.teachingmanagerbk.vo.course.Course;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseExaminationServiceImplTest {

    @Mock
    private CourseExaminationMapper courseExaminationMapper;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseExaminationServiceImpl courseExaminationService;

    @Test
    void shouldMarkApplicationRejectedWhenExaminationIsNotPass() {
        CourseApplication application = buildApplication("app-1", "未通过", "新增");
        when(courseExaminationMapper.getCourseExaminationIdByName("未通过")).thenReturn("3");

        courseExaminationService.examineACourse(application);

        verify(courseExaminationMapper).updateCourseApplicationExamination("3", "app-1");
        verifyNoInteractions(courseMapper);
    }

    @Test
    void shouldRejectWhenExaminationNameIsInvalid() {
        CourseApplication application = buildApplication("app-2", "非法状态", "新增");
        when(courseExaminationMapper.getCourseExaminationIdByName("非法状态")).thenReturn(null);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> courseExaminationService.examineACourse(application));
        Assertions.assertEquals(400, ex.getCode());
        Assertions.assertTrue(ex.getMessage().contains("查询检查状态id错误"));
        verify(courseMapper, never()).insertCourse(any(Course.class));
    }

    @Test
    void shouldApproveAddOperationAndPersistCourseAndApplicationLink() {
        CourseApplication application = buildApplication("app-3", "通过", "新增");
        when(courseExaminationMapper.getCourseExaminationIdByName("通过")).thenReturn("2");
        when(courseMapper.getCourseStatusIdByName("等待课程安排")).thenReturn("5");
        doAnswer(invocation -> {
            Course course = invocation.getArgument(0);
            course.setCourseId("99");
            return null;
        }).when(courseMapper).insertCourse(any(Course.class));

        courseExaminationService.examineACourse(application);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseMapper).insertCourse(captor.capture());
        Course createdCourse = captor.getValue();
        Assertions.assertEquals("5", createdCourse.getCourseStatusId());
        Assertions.assertEquals("高级软件测试", createdCourse.getName());
        Assertions.assertEquals("t1", createdCourse.getTeacherId());
        Assertions.assertEquals("周三 3-4 节", createdCourse.getTime());
        verify(courseExaminationMapper).updateCourseApplicationCourseIdAndExamination("99", "2", "app-3");
    }

    @Test
    void shouldRejectAddOperationWhenCourseStatusMissing() {
        CourseApplication application = buildApplication("app-4", "通过", "新增");
        when(courseExaminationMapper.getCourseExaminationIdByName("通过")).thenReturn("2");
        when(courseMapper.getCourseStatusIdByName("等待课程安排")).thenReturn(null);

        BusinessException ex = Assertions.assertThrows(BusinessException.class, () -> courseExaminationService.examineACourse(application));
        Assertions.assertEquals(500, ex.getCode());
        Assertions.assertTrue(ex.getMessage().contains("课程状态"));
        verify(courseMapper, never()).insertCourse(any(Course.class));
        verify(courseExaminationMapper, never()).updateCourseApplicationCourseIdAndExamination(anyString(), anyString(), anyString());
    }

    @Test
    void shouldApproveModifyOperationAndUpdateExaminationStatus() {
        CourseApplication application = buildApplication("app-5", "通过", "修改");
        application.setCourseId("c2");
        when(courseExaminationMapper.getCourseExaminationIdByName("通过")).thenReturn("2");

        courseExaminationService.examineACourse(application);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseMapper).modifyCourse(captor.capture());
        Course modifiedCourse = captor.getValue();
        Assertions.assertEquals("c2", modifiedCourse.getCourseId());
        Assertions.assertEquals("高级软件测试", modifiedCourse.getName());
        verify(courseExaminationMapper).updateCourseApplicationExamination("2", "app-5");
    }

    @Test
    void shouldApproveDeleteOperationAndDeleteCourse() {
        CourseApplication application = buildApplication("app-6", "通过", "删除");
        application.setCourseId("c7");
        when(courseExaminationMapper.getCourseExaminationIdByName("通过")).thenReturn("2");

        courseExaminationService.examineACourse(application);

        ArgumentCaptor<String[]> captor = ArgumentCaptor.forClass(String[].class);
        verify(courseMapper).deleteCourseByIds(captor.capture());
        Assertions.assertArrayEquals(new String[] {"c7"}, captor.getValue());
        verify(courseExaminationMapper, never()).updateCourseApplicationCourseIdAndExamination(anyString(), anyString(), anyString());
        verify(courseExaminationMapper, never()).updateCourseApplicationExamination(anyString(), anyString());
    }

    private CourseApplication buildApplication(String applicationId, String examinationName, String operationName) {
        CourseApplication application = new CourseApplication();
        application.setCourseApplicationId(applicationId);
        application.setCourseExaminationName(examinationName);
        application.setOperationName(operationName);
        application.setTeacherId("t1");
        application.setCourseName("高级软件测试");
        application.setCourseCredit("2");
        application.setCourseHour("32");
        application.setCourseTime("周三 3-4 节");
        application.setCoursePlaceId("1");
        application.setCourseDescription("课程审核单元测试");
        return application;
    }
}
