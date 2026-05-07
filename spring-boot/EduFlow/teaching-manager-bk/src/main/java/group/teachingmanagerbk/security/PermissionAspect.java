package group.teachingmanagerbk.security;

import group.teachingmanagerbk.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Around("@within(group.teachingmanagerbk.security.RequirePermission) || @annotation(group.teachingmanagerbk.security.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint point) throws Throwable {
        RequirePermission requirePermission = resolvePermission(point);
        if (requirePermission == null) {
            return point.proceed();
        }

        Map<String, Object> claims = UserContextHolder.getClaims();
        if (claims == null || claims.isEmpty()) {
            throw new BusinessException(401, "未授权");
        }

        Object roleNameObj = claims.get("roleName");
        Object roleObj = claims.get("role");
        String currentRole = RoleConstants.fromLegacyCode(String.valueOf(roleNameObj != null ? roleNameObj : roleObj));
        String[] requiredPermissions = requirePermission.value();

        boolean hasPermission = Arrays.stream(requiredPermissions)
                .anyMatch(permission -> RoleConstants.ROLE_ALL.equals(permission) || permission.equals(currentRole));

        if (!hasPermission) {
            log.warn("权限校验失败: currentRole={}, required={}", currentRole, Arrays.toString(requiredPermissions));
            throw new BusinessException(403, "权限不足");
        }

        return point.proceed();
    }

    private RequirePermission resolvePermission(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotationOnMethod = AnnotationUtils.findAnnotation(method, RequirePermission.class);
        if (annotationOnMethod != null) {
            return annotationOnMethod;
        }
        return AnnotationUtils.findAnnotation(point.getTarget().getClass(), RequirePermission.class);
    }
}
