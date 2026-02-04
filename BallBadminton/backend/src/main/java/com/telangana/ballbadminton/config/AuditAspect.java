package com.telangana.ballbadminton.config;

import com.telangana.ballbadminton.entity.AuditLog.AuditAction;
import com.telangana.ballbadminton.entity.AuditLog.AuditSeverity;
import com.telangana.ballbadminton.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * AOP Aspect for automatic audit logging of controller operations
 * 
 * This aspect intercepts all controller methods and automatically logs:
 * - HTTP method and URL
 * - Request parameters
 * - Response status
 * - Execution time
 * - Errors and exceptions
 * 
 * Requirements: 6.2, 8.5
 * 
 * @author Telangana Ball Badminton Association
 * @version 1.0.0
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("com.telangana.ballbadminton.performance");

    private final AuditService auditService;

    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Intercept all controller methods for audit logging
     */
    @Around("execution(* com.telangana.ballbadminton.controller..*(..))")
    public Object auditControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        // Get HTTP request details
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String requestUrl = request != null ? request.getRequestURI() : "UNKNOWN";
        
        // Determine audit action based on HTTP method and annotations
        AuditAction auditAction = determineAuditAction(method, httpMethod);
        
        // Determine entity type from controller name
        String entityType = extractEntityType(className);
        
        Object result = null;
        Exception caughtException = null;
        
        try {
            // Proceed with the actual method execution
            result = joinPoint.proceed();
            
            // Log successful operation
            long executionTime = System.currentTimeMillis() - startTime;
            logSuccessfulOperation(auditAction, entityType, methodName, executionTime, result);
            
            // Log performance metrics
            if (executionTime > 1000) { // Log slow operations (> 1 second)
                performanceLogger.warn("Slow operation detected: {} {} - {}ms", 
                    httpMethod, requestUrl, executionTime);
            }
            
            return result;
            
        } catch (Exception e) {
            caughtException = e;
            
            // Log failed operation
            long executionTime = System.currentTimeMillis() - startTime;
            logFailedOperation(auditAction, entityType, methodName, executionTime, e);
            
            throw e;
        }
    }

    /**
     * Determine audit action based on HTTP method and annotations
     */
    private AuditAction determineAuditAction(Method method, String httpMethod) {
        // Check for specific annotations
        if (method.isAnnotationPresent(PostMapping.class)) {
            return AuditAction.CREATE;
        } else if (method.isAnnotationPresent(GetMapping.class)) {
            return AuditAction.READ;
        } else if (method.isAnnotationPresent(PutMapping.class) || 
                   method.isAnnotationPresent(PatchMapping.class)) {
            return AuditAction.UPDATE;
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            return AuditAction.DELETE;
        }
        
        // Fallback to HTTP method
        return switch (httpMethod.toUpperCase()) {
            case "POST" -> AuditAction.CREATE;
            case "GET" -> AuditAction.READ;
            case "PUT", "PATCH" -> AuditAction.UPDATE;
            case "DELETE" -> AuditAction.DELETE;
            default -> AuditAction.READ;
        };
    }

    /**
     * Extract entity type from controller class name
     */
    private String extractEntityType(String className) {
        // Remove "Controller" suffix
        if (className.endsWith("Controller")) {
            return className.substring(0, className.length() - 10);
        }
        return className;
    }

    /**
     * Log successful operation
     */
    private void logSuccessfulOperation(AuditAction action, String entityType, 
                                       String methodName, long executionTime, Object result) {
        try {
            String description = String.format("%s operation on %s completed successfully (method: %s)", 
                action, entityType, methodName);
            
            // Extract entity ID from result if it's a ResponseEntity
            String entityId = extractEntityId(result);
            
            com.telangana.ballbadminton.entity.AuditLog auditLog = 
                new com.telangana.ballbadminton.entity.AuditLog();
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDescription(description);
            auditLog.setExecutionTimeMs(executionTime);
            auditLog.setSeverity(AuditSeverity.INFO);
            auditLog.setStatus(com.telangana.ballbadminton.entity.AuditLog.AuditStatus.SUCCESS);
            
            // Extract status code from ResponseEntity
            if (result instanceof ResponseEntity) {
                auditLog.setStatusCode(((ResponseEntity<?>) result).getStatusCode().value());
            }
            
            auditService.logAudit(auditLog);
            
        } catch (Exception e) {
            logger.error("Failed to log successful operation: {}", e.getMessage());
        }
    }

    /**
     * Log failed operation
     */
    private void logFailedOperation(AuditAction action, String entityType, 
                                   String methodName, long executionTime, Exception exception) {
        try {
            String description = String.format("%s operation on %s failed (method: %s): %s", 
                action, entityType, methodName, exception.getMessage());
            
            auditService.logFailure(action, entityType, null, description, exception);
            
        } catch (Exception e) {
            logger.error("Failed to log failed operation: {}", e.getMessage());
        }
    }

    /**
     * Extract entity ID from result (if available)
     */
    private String extractEntityId(Object result) {
        if (result instanceof ResponseEntity) {
            Object body = ((ResponseEntity<?>) result).getBody();
            if (body != null) {
                // Try to extract ID using reflection
                try {
                    Method getIdMethod = body.getClass().getMethod("getId");
                    Object id = getIdMethod.invoke(body);
                    return id != null ? id.toString() : null;
                } catch (Exception e) {
                    // ID extraction failed, not critical
                }
            }
        }
        return null;
    }
}
