package group.teachingmanagerbk.dto.course;

import lombok.Data;

@Data
public class StudentSelectCourseData {
    private String coursesStudentsId;       //学生选课的id
    private String courseId;                //课程id
    private String studentId;               //学生id
    private Float score;                    //学生成绩
}
