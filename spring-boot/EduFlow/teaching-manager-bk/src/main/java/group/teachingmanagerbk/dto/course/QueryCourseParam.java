package group.teachingmanagerbk.dto.course;

import group.teachingmanagerbk.vo.course.Course;
import lombok.Data;

@Data
public class QueryCourseParam {
    private Integer pageSize;       //页大小
    private Integer currentPage;    //第几页
    private Course param;           //课程的查询参数
}
