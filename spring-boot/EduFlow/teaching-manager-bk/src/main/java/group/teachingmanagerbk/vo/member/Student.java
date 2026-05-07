package group.teachingmanagerbk.vo.member;

import lombok.Data;

@Data
public class Student {
    private String studentId;            //学生id
    private String studentNumber = "";   //学生学号
    private String name = "";            //学生姓名
    private String studentClass = "";    //班级
    private String dateTime;             //日期时间
    private String score;                //成绩
}
