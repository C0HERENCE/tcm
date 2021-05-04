package cn.ccwisp.tcm.service;

import cn.ccwisp.tcm.generated.domain.UmsRoles;
import cn.ccwisp.tcm.generated.domain.UmsUser;
import cn.ccwisp.tcm.generated.domain.UmsUserinfo;
import cn.ccwisp.tcm.generated.service.impl.UmsRolesServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.UmsUserServiceImpl;
import cn.ccwisp.tcm.generated.service.impl.UmsUserinfoServiceImpl;
import cn.ccwisp.tcm.security.util.JwtTokenUtil;
import cn.ccwisp.tcm.bo.TcmUserDetails;
import cn.ccwisp.tcm.common.exception.Asserts;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final UmsUserServiceImpl umsUserService;
    private final UmsUserinfoServiceImpl umsUserinfoService;
    private final UmsRolesServiceImpl umsRolesService;

    public AuthService(PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, UmsUserServiceImpl umsUserService, UmsUserinfoServiceImpl userinfoService, UmsRolesServiceImpl umsRolesService, AdminService adminService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.umsUserService = umsUserService;
        this.umsUserinfoService = userinfoService;
        this.umsRolesService = umsRolesService;
        this.adminService = adminService;
    }

    public TcmUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<UmsUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UmsUser umsUser = umsUserService.getOne(queryWrapper);
        if (umsUser == null)
            throw new UsernameNotFoundException("该用户不存在");
        // 格式化权限
        List<UmsRoles> umsRolesList = umsRolesService.list(new QueryWrapper<UmsRoles>()
                .eq("userId", umsUser.getId()));
        StringBuilder roles = new StringBuilder();
        for (UmsRoles umsRoles : umsRolesList) {
            roles.append(umsRoles.getRolename()).append(",");
        }
        return new TcmUserDetails()
                .setUserId(umsUser.getId())
                .setUserRoles(roles.toString())
                .setEnabled(umsUser.getEnabled())
                .setPassword(umsUser.getPassword())
                .setUsername(umsUser.getUsername())
                .setAdmin(roles.toString().contains("ROLE_isAdmin"));
    }

    public Map<String, Object> Login(String username, String password) {
        Map<String, Object> result = null;
        try {
            TcmUserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                Asserts.fail("密码不正确");
            }
            if(!userDetails.isEnabled()){
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtil.generateToken(userDetails);
            result = new HashMap<>();
            result.put("token", token);
            result.put("isAdmin", userDetails.isAdmin());
            if (userDetails.isAdmin())
                result.put("roles", userDetails.getUserRoles().split(","));
            result.put("username", username.toLowerCase().trim());
        } catch (AuthenticationException e) {
            System.out.println("登录异常");
        }
        return result;
    }
private final AdminService adminService;
    public void Register(String email, String password) {
        if (CheckUsernameExist(email))
            Asserts.fail("该邮箱已被注册");
        UmsUser user = new UmsUser();
        user.setId(0);
        user.setUsername(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setEnabled(1);
        umsUserService.save(user);
        UmsUserinfo userinfo = new UmsUserinfo();
        userinfo.setId(0);
        userinfo.setUserid(user.getId());
        userinfo.setNickname(email.substring(0, email.indexOf('@')));
        userinfo.setRealname("");
        userinfo.setQq(email.toLowerCase().endsWith("@qq.com")?userinfo.getNickname():"");
        userinfo.setEmail(email);
        userinfo.setJob("");
        userinfo.setHobby("");
        userinfo.setIntro("");
        userinfo.setPhone("");
        userinfo.setAvatar("");
        umsUserinfoService.save(userinfo);
        adminService.addRole(user.getId(), "1");
        adminService.addRole(user.getId(), "2");
        adminService.addRole(user.getId(), "3");
    }

    public boolean CheckUsernameExist(String username) {
        QueryWrapper<UmsUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UmsUser umsUser = umsUserService.getOne(queryWrapper);
        return umsUser != null;
    }

}
