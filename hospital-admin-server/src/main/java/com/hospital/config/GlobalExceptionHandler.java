package com.hospital.config;

import com.hospital.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<?>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(500, e.getMessage() != null ? e.getMessage() : "系统异常"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "系统异常，请联系管理员"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<?>> handleAuthenticationException(AuthenticationException e) {
        log.error("认证异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(401, "未登录或登录已过期"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<?>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(403, "权限不足"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.error("参数校验异常: {}", message);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(400, message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<?>> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数绑定失败";
        log.error("参数绑定异常: {}", message);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(400, message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数异常: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Result.error(400, e.getMessage() != null ? e.getMessage() : "参数错误"));
    }
}
