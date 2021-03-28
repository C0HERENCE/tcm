package cn.ccwisp.tcm.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {
    private long status;
    private String message;
    private T data;

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(StatusCode.SUCCESS.getCode(), message, data);
    }

    public static <T> CommonResult<T> failed(IStatusCode errorCode) {
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> CommonResult<T> failed(IStatusCode errorCode, String message) {
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(StatusCode.FAILED.getCode(), message, null);
    }

    public static <T> CommonResult<T> failed() {
        return failed(StatusCode.FAILED);
    }

    public static <T> CommonResult<T> badRequest() {
        return failed(StatusCode.BAD_REQUEST);
    }

    public static <T> CommonResult<T> badRequest(String message) {
        return new CommonResult<T>(StatusCode.BAD_REQUEST.getCode(), message, null);
    }

    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(StatusCode.UNAUTHORIZED.getCode(), StatusCode.UNAUTHORIZED.getMessage(), data);
    }

    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(StatusCode.FORBIDDEN.getCode(), StatusCode.FORBIDDEN.getMessage(), data);
    }
}