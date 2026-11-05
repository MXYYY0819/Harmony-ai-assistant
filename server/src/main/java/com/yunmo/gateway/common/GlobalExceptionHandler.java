package com.yunmo.gateway.common;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
@RestControllerAdvice public class GlobalExceptionHandler  {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // Explicit JSON also covers preflight errors for clients accepting only SSE.
    private ResponseEntity<Result<Void>> response(int status, String message)  {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON) .body(Result.failure(status, message));
    }
    @ExceptionHandler(BusinessException.class) public ResponseEntity<Result<Void>> business(BusinessException e)  {
        return response(e.getStatus(), e.getMessage());
    }
    @ExceptionHandler( {
        MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class, ConstraintViolationException.class, HandlerMethodValidationException.class, MissingServletRequestParameterException.class, HttpRequestMethodNotSupportedException.class, HttpMediaTypeNotSupportedException.class
    }) public ResponseEntity<Result<Void>> invalid(Exception e)  {
        return response(400, "参数错误");
    }
    @ExceptionHandler(NoResourceFoundException.class) public ResponseEntity<Result<Void>> missing(Exception e)  {
        return response(404, "资源不存在");
    }
    @ExceptionHandler(Exception.class) public ResponseEntity<Result<Void>> unexpected(Exception e)  {
        log.error("Request failed", e);
        return response(500, "服务端异常");
    }
}
