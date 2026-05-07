package group.teachingmanagerbk.exception;

import group.teachingmanagerbk.utils.ReturnResult.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return new Result().error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(EnrollmentConflictException.class)
    public Result handleEnrollmentConflict(EnrollmentConflictException e) {
        log.warn("选课冲突: studentId={}, courseId={}, message={}", e.getStudentId(), e.getCourseId(), e.getMessage());
        return new Result().error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidation(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return new Result().error(400, errors);
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return new Result().error(500, "系统内部错误");
    }
}

