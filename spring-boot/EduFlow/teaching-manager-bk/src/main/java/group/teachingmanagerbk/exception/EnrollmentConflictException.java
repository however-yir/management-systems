package group.teachingmanagerbk.exception;

import lombok.Getter;

@Getter
public class EnrollmentConflictException extends BusinessException {
    private final String studentId;
    private final String courseId;

    public EnrollmentConflictException(String studentId, String courseId, String message) {
        super(409, message);
        this.studentId = studentId;
        this.courseId = courseId;
    }
}

