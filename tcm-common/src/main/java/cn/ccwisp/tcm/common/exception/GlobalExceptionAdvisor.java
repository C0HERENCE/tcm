package cn.ccwisp.tcm.common.exception;

import cn.ccwisp.tcm.common.api.CommonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvisor {
    @ExceptionHandler(value = ApiException.class)
    public CommonResult handle(ApiException e) {
        if (e.getStatusCode() != null) {
            return CommonResult.failed(e.getStatusCode());
        }
        return CommonResult.failed(e.getMessage());
    }
}
