package group.teachingmanagerbk.dto.member;

import group.teachingmanagerbk.vo.member.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询学生信息的类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryStudentParam {
    private Integer pageSize;
    private Integer currentPage;
    private Student param;
}
