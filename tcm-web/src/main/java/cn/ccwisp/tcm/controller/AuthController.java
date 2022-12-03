package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.dto.LoginParams;
import cn.ccwisp.tcm.service.AuthService;
import cn.ccwisp.tcm.service.MailService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    private final MailService mailService;

    public AuthController(AuthService authService, MailService mailService) {
        this.authService = authService;
        this.mailService = mailService;
    }

    private boolean validateEmail(String email) {
        if (null==email || "".equals(email))
            return false;
        Pattern p = Pattern.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    @PostMapping("/login")
    public CommonResult Login(@RequestBody LoginParams loginParams){
        Map result = authService.Login(loginParams.getUsername(), loginParams.getPassword());
        if (result == null)
            return CommonResult.badRequest("用户名或密码错误");
        return CommonResult.success(result);
    }

    @PostMapping("/captcha")
    public CommonResult SendCaptcha(HttpSession session, @RequestBody HashMap<String,String> map){
        Map.Entry<String,String> kv = mailService.generateCaptcha();
        session.setAttribute("k", kv.getKey());
        String email = map.get("email");

        if (!validateEmail(email))
            return CommonResult.badRequest("邮箱输入错误");
        if (authService.CheckUsernameExist(email))
            return CommonResult.badRequest("该邮箱已被注册");

        mailService.send(email, "欢迎来到中医药科普平台", "你的验证码为：" + kv.getValue());
        return CommonResult.success("ok");
    }

    @PostMapping("/register")
    public CommonResult Register(@SessionAttribute("k") String k ,@RequestBody HashMap<String, String> map) {
        if (!Objects.equals(map.get("password"), map.get("password2")))
            return CommonResult.badRequest("两次密码输入不一致");
        String email = map.get("email");
        if (!validateEmail(email))
            return CommonResult.badRequest("邮箱输入错误");
        if (!new BCryptPasswordEncoder().matches(map.get("captcha"), k))
            return CommonResult.badRequest("验证码输入错误");
        authService.Register(email, map.get("password"));
        return CommonResult.success("ok");
    }
}
