package group.teachingmanagerbk.dto.application;

import lombok.Data;

@Data
public class CourseApplication {
    private String courseApplicationId;     //课程申请id
    private String teacherId;               //教师id
    private String teacherName;             //教师名称
    private String courseId;                //课程id
    private String courseName;              //课程名称
    private String courseCredit;            //课程学分
    private String courseHour;              //课程学时
    private String courseTime;              //授课时间
    private String coursePlaceId;           //授课地点id
    private String coursePlaceName;         //授课地点名称
    private String courseDescription;       //课程描述
    private String courseExaminationId;     //审批状态id
    private String courseExaminationName;   //审批状态名称
    private String operationId;             //操作id
    private String operationName;           //操作名称
    private String dateTime;                //申请时间
}
