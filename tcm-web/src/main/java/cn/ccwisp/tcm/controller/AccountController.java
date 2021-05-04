package cn.ccwisp.tcm.controller;

import cn.ccwisp.tcm.bo.TcmUserDetails;
import cn.ccwisp.tcm.common.api.CommonResult;
import cn.ccwisp.tcm.dto.profile.PersonalInfoDto;
import cn.ccwisp.tcm.generated.domain.UmsUser;
import cn.ccwisp.tcm.generated.domain.UmsUserinfo;
import cn.ccwisp.tcm.generated.service.impl.UmsUserinfoServiceImpl;
import cn.ccwisp.tcm.service.AccountService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/favourite")
    public CommonResult<Map<Integer, Object>> GetFavourite() {
        int userId = ((TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        return CommonResult.success(accountService.getUserFavourite(userId));
    }

    @GetMapping("/history")
    public CommonResult GetHistory() {
        return CommonResult.success(1);
    }

    @GetMapping("/post")
    public CommonResult<Map<Integer, Object>> getOldPost() {
        int userId = ((TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        return CommonResult.success(accountService.getUserPublished(userId));
    }

    @GetMapping("/userinfo")
    public CommonResult<PersonalInfoDto> GetUserInfo() {
        int userId = ((TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        UmsUserinfo userinfo = accountService.getUserInfoByUserId(userId);
        if (userinfo == null)
            return CommonResult.badRequest("该用户不存在");
        return CommonResult.success(accountService.mappingUserinfoDto(userinfo));
    }

    @PostMapping("/userinfo")
    public CommonResult<PersonalInfoDto> UpdateUserInfo(@RequestBody PersonalInfoDto personalInfoDto) {
        TcmUserDetails principal = (TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = principal.getUserId();
        UmsUserinfo userinfo = accountService.getUserInfoByUserId(userId);
        if (userinfo == null)
            return CommonResult.badRequest("该用户不存在");
        userinfo.setNickname(personalInfoDto.getNickname());
        userinfo.setEmail(personalInfoDto.getEmail());
        userinfo.setRealname(personalInfoDto.getRealName());
        userinfo.setIntro(personalInfoDto.getIntro());
        userinfo.setQq(personalInfoDto.getQq());
        userinfo.setPhone(personalInfoDto.getTel());
        userinfo.setAvatar(personalInfoDto.getAvatar());
        return CommonResult.success(accountService.updateUserInfo(userinfo), "个人信息修改成功");
    }

    @GetMapping("/security")
    public CommonResult<Integer> GetUserEnabled() {
        int userId = ((TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        UmsUser user = accountService.GetUserById(userId);
        if (user == null)
            return CommonResult.badRequest("该用户不存在");
        return CommonResult.success(user.getEnabled());
    }

    @PostMapping("/newPwd")
    public CommonResult<Object> SetNewPwd(@RequestBody Map<String, String> form) {
        int userId = ((TcmUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        UmsUser user = accountService.GetUserById(userId);
        if (user == null)
            return CommonResult.badRequest("该用户不存在");
        if (!new BCryptPasswordEncoder().matches(form.get("oldPwd"), user.getPassword()))
            return CommonResult.badRequest("原密码错误");
        if (form.get("newPwd").length() <6 || form.get("newPwd").length()>18)
            return CommonResult.badRequest("新密码格式错误");
        user.setPassword(new BCryptPasswordEncoder().encode(form.get("newPwd")));
        accountService.UpdatePassword(user);
        return CommonResult.success("修改成功");
    }

    @GetMapping("/avatar")
    public CommonResult<String> GetAvatar() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == "anonymousUser")
            return CommonResult.badRequest("尚未登录");
        int userId = ((TcmUserDetails) principal).getUserId();
        UmsUser user = accountService.GetUserById(userId);
        if (user == null)
            return CommonResult.badRequest("该用户不存在");
        return CommonResult.success(accountService.GetAvatar(userId));
    }

}
