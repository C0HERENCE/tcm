package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.dto.LoginParams;
import cn.ccwisp.tcm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public CommonResult Login(@RequestBody LoginParams loginParams){
        String token = userService.Login(loginParams.getUsername(), loginParams.getPassword());
        if (token == null)
            return CommonResult.badRequest("用户名或密码错误");
        return CommonResult.success(token);
    }
}
