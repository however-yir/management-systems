package group.teachingmanagerbk.dto.member;

import group.teachingmanagerbk.vo.member.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询教师信息的类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryTeacherParam {
    private Integer pageSize;
    private Integer currentPage;
    private Teacher param;
}
