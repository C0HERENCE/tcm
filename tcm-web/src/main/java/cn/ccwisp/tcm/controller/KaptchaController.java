package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.common.api.CommonResult;
import com.baomidou.kaptcha.Kaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kaptcha")
public class KaptchaController {
    private final Kaptcha kaptcha;

    public KaptchaController(Kaptcha kaptcha) {
        this.kaptcha = kaptcha;
    }

    @GetMapping("/render")
    public void render() {
        kaptcha.render();
    }

    @PostMapping("/valid")
    public CommonResult<String> validDefaultTime(@RequestParam String code) {
        //default timeout 900 seconds
        try {
            kaptcha.validate(code);
            return CommonResult.success("验证码输入正确");
        } catch (Exception e){
            return CommonResult.badRequest("验证码输入错误");
        }
    }

    @PostMapping("/validTime")
    public void validCustomTime(@RequestParam String code) {
        kaptcha.validate(code, 60);
    }

}
