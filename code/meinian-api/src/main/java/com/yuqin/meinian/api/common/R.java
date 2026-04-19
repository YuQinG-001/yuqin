package com.yuqin.meinian.api.common;

import lombok.Data;
import org.apache.http.HttpStatus;

import java.io.Serializable;

/**
 * 统一API响应结果封装类
 */
@Data
public class R<T> implements Serializable {

    private Integer code;
    private String msg;
    private T result;

    private R() {
        this.code = HttpStatus.SC_OK;
        this.msg = "success";
    }

    private R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private R(Integer code, String msg, T result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    // ========== 成功响应 ==========

    public static <T> R<T> ok() {
        return new R<>();
    }

    public static <T> R<T> ok(T data) {
        return new R<>(HttpStatus.SC_OK, "success", data);
    }

    public static <T> R<T> ok(String msg, T data) {
        return new R<>(HttpStatus.SC_OK, msg, data);
    }
    public static <T> R<T> ok(String msg) {
        return new R<>(HttpStatus.SC_OK, msg);
    }

    // ========== 失败响应 ==========

    public static <T> R<T> error() {
        return new R<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    public static <T> R<T> error(String msg) {
        return new R<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static <T> R<T> error(Integer code, String msg) {
        return new R<>(code, msg);
    }

    // ========== 常用业务状态 ==========

    public static <T> R<T> unauthorized(String msg) {
        return new R<>(HttpStatus.SC_UNAUTHORIZED, msg);
    }

    public static <T> R<T> forbidden(String msg) {
        return new R<>(HttpStatus.SC_FORBIDDEN, msg);
    }

    public static <T> R<T> notFound(String msg) {
        return new R<>(HttpStatus.SC_NOT_FOUND, msg);
    }
}