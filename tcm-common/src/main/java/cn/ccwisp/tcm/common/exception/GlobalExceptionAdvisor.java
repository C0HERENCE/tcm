package cn.ccwisp.tcm.common.exception;

import cn.ccwisp.tcm.common.api.CommonResult;
import com.baomidou.kaptcha.exception.KaptchaException;
import com.baomidou.kaptcha.exception.KaptchaIncorrectException;
import com.baomidou.kaptcha.exception.KaptchaNotFoundException;
import com.baomidou.kaptcha.exception.KaptchaTimeoutException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionAdvisor {
    @ExceptionHandler(value = ApiException.class)
    public CommonResult<Object> handle(ApiException e) {
        if (e.getStatusCode() != null) {
            return CommonResult.failed(e.getStatusCode());
        }
        return CommonResult.failed(e.getMessage());
    }
    @ExceptionHandler(value = KaptchaException.class)
    public CommonResult<Object> kaptchaExceptionHandler(KaptchaException kaptchaException) {
        if (kaptchaException instanceof KaptchaIncorrectException) {
            return CommonResult.badRequest("验证码不正确");
        } else if (kaptchaException instanceof KaptchaNotFoundException) {
            return CommonResult.badRequest("验证码未找到");
        } else if (kaptchaException instanceof KaptchaTimeoutException) {
            return CommonResult.badRequest("验证码过期");
        } else {
            return CommonResult.badRequest("验证码渲染失败");
        }

    }
}
