package com.niit.industrialgasalarmcorporate.infrastructure.config;

import com.niit.industrialgasalarmcorporate.common.base.Result;
import com.niit.industrialgasalarmcorporate.common.enums.ErrorCode;
import com.niit.industrialgasalarmcorporate.common.exception.BusinessException;
import com.niit.industrialgasalarmcorporate.common.exception.ForbiddenException;
import com.niit.industrialgasalarmcorporate.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleUnauthorized(UnauthorizedException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleForbidden(ForbiddenException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(), message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        return Result.fail(ErrorCode.FORBIDDEN.getCode(), ErrorCode.FORBIDDEN.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleAuthentication(AuthenticationException e) {
        return Result.fail(ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(),
                "请求格式不正确，上传文件请使用 multipart/form-data，而非 JSON");
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingPart(MissingServletRequestPartException e) {
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(),
                "缺少上传文件参数: " + e.getRequestPartName());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(),
                "缺少请求参数: " + e.getParameterName());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMultipart(MultipartException e) {
        log.warn("文件上传请求解析失败", e);
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(),
                "文件上传请求解析失败，请检查文件格式和大小");
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleDateTimeParse(DateTimeParseException e) {
        log.warn("日期格式解析失败: {}", e.getParsedString());
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(),
                "日期格式不正确，请使用标准日期格式");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数值不合法", e);
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(), "参数值不合法");
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalState(IllegalStateException e) {
        log.warn("业务状态异常", e);
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(), "操作状态不合法，请刷新后重试");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return Result.fail(ErrorCode.VALIDATION_ERROR.getCode(), "文件大小超过限制（最大5MB）");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统内部错误", e);
        return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }
}
