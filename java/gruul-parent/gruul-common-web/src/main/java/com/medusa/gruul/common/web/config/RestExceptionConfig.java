package com.medusa.gruul.common.web.config;

import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.RangeTemporal;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;

/**
 * 全局异常处理，处理可预见的异常，Order 排序优先级高
 * <p>
 * <p>
 * Bean-Violation 异常处理
 *
 * @author L.cm
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1000)
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RestExceptionConfig {


    /**
     * 全局异常
     */
    @ExceptionHandler(GlobalException.class)
    public Result<Object> handlerGlobalException(GlobalException exception) {
        log.debug("global exception", exception);
        Result<Object> failed = Result.failed(exception.code(), exception.msg());
        failed.setData(exception.getData());
        return failed;

    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Object> handleError(MissingServletRequestParameterException exception) {
        log.debug("param valid error", exception);
        String message = String.format("缺少必要的请求参数: %s", exception.getParameterName());
        return Result.failed(SystemCode.PARAM_MISS, message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Object> handleError(MethodArgumentTypeMismatchException e) {
        log.debug("wrong param type", e);
        String message = String.format("请求参数格式错误: %s", e.getName());
        return Result.failed(SystemCode.PARAM_TYPE_ERROR, message);
    }

    /**
     * 参数未通过的异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleError(MethodArgumentNotValidException e) {
        log.debug("param valid error", e);
        return handleError(e.getBindingResult());
//        return Result.failed(SystemCode.PARAM_BIND_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public Result<Object> handleError(BindException e) {
        log.debug("param bind error", e);
        return handleError(e.getBindingResult());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Object> handleError(ConstraintViolationException e) {
        log.debug("param valid error", e);
        return handleError(e.getConstraintViolations());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Object> handleError(HttpMessageNotReadableException e) {
        log.debug("param read error", e);
        return Result.failed(SystemCode.MSG_NOT_READABLE);
    }

    @ExceptionHandler(RangeTemporal.TimeRangeException.class)
    public Result<Object> handError(RangeTemporal.TimeRangeException e) {
        log.debug("time range  error", e);
        return Result.failed(SystemCode.TIME_RANGE_ERROR);
    }


//####################################抽取共性 通用部分代码################################################

    /**
     * 处理 BindingResult
     *
     * @param result BindingResult
     * @return Result
     */
    protected Result<Object> handleError(BindingResult result) {
        FieldError error = result.getFieldError();
        if (error == null) {
            return Result.failed(SystemCode.PARAM_BIND_ERROR);
        }
        String message = String.format("%s:%s", error.getField(), SystemCode.PARAM_BIND_ERROR.getMsg());
        return Result.failed(SystemCode.PARAM_BIND_ERROR, message);
    }

    /**
     * 处理 ConstraintViolation
     *
     * @param violations 校验结果
     * @return Result
     */
    protected Result<Object> handleError(Set<ConstraintViolation<?>> violations) {
        ConstraintViolation<?> violation = violations.iterator().next();
        String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
        String message = String.format("%s:%s", path, violation.getMessage());
        return Result.failed(SystemCode.PARAM_VALID_ERROR, message);
    }

}
