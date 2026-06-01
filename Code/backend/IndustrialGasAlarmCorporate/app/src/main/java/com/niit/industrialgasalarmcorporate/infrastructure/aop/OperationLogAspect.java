package com.niit.industrialgasalarmcorporate.infrastructure.aop;

import com.niit.industrialgasalarmcorporate.application.operationlog.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final HttpServletRequest request;

    @Around("@annotation(logAnno)")
    public Object around(ProceedingJoinPoint joinPoint, LogOperation logAnno) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String operatorUuid = auth != null ? (String) auth.getPrincipal() : "anonymous";
            String username = (String) request.getAttribute("username");
            if (username == null) {
                username = operatorUuid;
            }
            String ip = request.getRemoteAddr();

            operationLogService.record(
                    operatorUuid, username, logAnno.operation(), logAnno.targetType(),
                    extractTargetId(joinPoint), null, null, ip);
        } catch (Exception e) {
            log.warn("操作日志记录失败: {}", e.getMessage());
        }

        return result;
    }

    private String extractTargetId(ProceedingJoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof String s && s.length() == 36 && s.contains("-")) {
                return s;
            }
        }
        return "-";
    }
}
