package com.yuqin.meinian.api.config;

import cn.dev33.satoken.exception.NotLoginException;
import com.yuqin.meinian.api.common.R;
import com.yuqin.meinian.api.exception.HisException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Optional;

/**
 * @author YuQin
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * 统一构建错误响应 - 返回 R 对象
     */
    private R<Void> buildErrorResponse(int code, String message) {
        return R.error(code, message);
    }


    /**
     * 当HTTP请求体（Body）的数据格式错误或无法解析时，就会发生此异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求未提交数据或者数据有误");
    }

    /**
     * 当请求中缺少预期的文件（@RequestPart）或表单字段时，就会发生此异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MissingServletRequestPartException.class)
    public R<Void> handleMissingServletRequestPart(MissingServletRequestPartException ex) {
        log.error("error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求提交数据错误");
    }

    /**
     * 当客户端使用了一个该URL不支持的HTTP方法（如用GET访问只支持POST的接口）时，就会发生此异常。
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error("error", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "HTTP请求方法类型错误");
    }

    /**
     * 当请求参数绑定到Java对象（如使用@RequestBody或@ModelAttribute）失败时，就会发生此异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException ex) {
        String errorMsg = Optional.ofNullable(ex.getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("请求参数绑定失败");
        log.error(errorMsg, ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMsg);
    }

    /**
     * 处理数据库唯一约束冲突异常
     * <p>
     * 主要用于捕获用户名、手机号等字段重复时的数据库报错，
     * 并将其转换为用户能看懂的友好提示。
     * </p>
     *
     * @param ex DuplicateKeyException 异常对象
     * @return 统一错误响应
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 参数校验失败，返回400
    public R<Void> handleDuplicateKeyException(DuplicateKeyException ex) {
        log.error("数据唯一性冲突: {}", ex.getMessage(), ex);

        // 解析异常信息，判断是哪个字段重复了
        String friendlyMsg = "数据已存在，请检查输入";
        String causeMsg = ex.getCause() != null ? ex.getCause().getMessage() : "";

        if (causeMsg.contains("uk_username") || causeMsg.contains("username")) {
            friendlyMsg = "用户名已存在，请重新输入";
        } else if (causeMsg.contains("mobile")) {
            friendlyMsg = "手机号已被注册";
        } else if (causeMsg.contains("email")) {
            friendlyMsg = "邮箱已被使用";
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), friendlyMsg);
    }

    /**
     * 没有通过后端验证产生的异常
     * 当使用 @Valid 注解校验请求体参数失败时，就会发生此异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMsg = Optional.ofNullable(ex.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("参数校验失败");
        log.error("参数校验异常: {}", errorMsg, ex);
        return buildErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMsg);
    }

    /**
     * 处理 MyBatis 查询结果不唯一的异常
     * <p>
     * 当使用 getOneOpt(..., true) 或 selectOne 等方法，查询结果超过1条时抛出此异常。
     * 通常表示数据库主键约束失效或存在脏数据，属于严重的数据完整性问题。
     * </p>
     *
     * @param ex TooManyResultsException 异常对象
     * @return R<Void> 统一错误响应，包含友好提示信息
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(TooManyResultsException.class)
    public R<Void> handleTooManyResultsException(TooManyResultsException ex) {
        String errorMsg = Optional.ofNullable(ex.getMessage())
                .orElse("数据完整性问题，查询返回了多条记录！");
        log.error("返回值异常: {}", errorMsg, ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMsg);
    }


    /**
     * 处理业务异常
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(HisException.class)
    public R<Void> handleHisException(HisException ex) {
        log.error("业务执行异常: {}", ex.getMsg(), ex);
        return buildErrorResponse(ex.getCode(), ex.getMsg());
    }

    /**
     * 处理其余的异常（兜底）
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public R<Void> handleGenericException(Exception ex) {
        log.error("执行异常", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "执行异常");
    }

    /**
     * 捕获异常，并且返回401状态码
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotLoginException.class)
    public R<Void> unLoginHandler(NotLoginException e) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
    }
}