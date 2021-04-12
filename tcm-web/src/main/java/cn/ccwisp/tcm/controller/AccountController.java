package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.common.api.CommonResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("account")
public class AccountController {

    @GetMapping("/favourite/{id}")
    public CommonResult GetFavourite(@PathVariable("id") int userId) {
        return CommonResult.success(1);
    }

    @GetMapping("/userinfo/{id}")
    public CommonResult GetUserInfo(@PathVariable("id") int userId) {
        return CommonResult.success(1);
    }

    @GetMapping("/history/{id}")
    public CommonResult GetHistory(@PathVariable("id") int userId) {
        return CommonResult.success(1);
    }

    @PostMapping("/userinfo")
    public CommonResult UpdateUserInfo(@RequestParam HashMap<String, Object> map) {
        return CommonResult.success(1);
    }
}
