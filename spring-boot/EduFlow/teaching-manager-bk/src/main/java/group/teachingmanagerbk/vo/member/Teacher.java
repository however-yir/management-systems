package group.teachingmanagerbk.vo.member;

import lombok.Data;

@Data
public class Teacher {
    private String teacherId;            //教师id
    private String teacherNumber = "";   //教师工号
    private String name = "";            //教师姓名
    private String departmentId = "";    //学院id
    private String departmentName = "";  //学院名称
    private String dateTime;             //日期时间
}
