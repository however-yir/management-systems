package group.teachingmanagerbk.service;

import group.teachingmanagerbk.dto.application.CourseApplication;

import java.util.ArrayList;

public interface CourseExaminationService {

    /**
     * 根据审批名称查询待审批的申请记录
     * @param examinationName 审批名称
     * @return 申请记录
     */
    ArrayList<CourseApplication> getWaitExamination(String examinationName);

    /**
     * 根据审批名称查询已审批的申请记录
     * @param examinationName 审批名称
     * @return 申请记录
     */
    ArrayList<CourseApplication> getAlreadyExamination(String examinationName);

    /**
     * 审批一条申请记录
     * @param json 申请记录
     */
    void examineACourse(CourseApplication json);

}
