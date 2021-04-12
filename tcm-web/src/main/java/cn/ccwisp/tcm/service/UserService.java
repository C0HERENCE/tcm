package cn.ccwisp.tcm.service;

import cn.ccwisp.tcm.generated.domain.UmsUser;
import cn.ccwisp.tcm.generated.service.impl.UmsUserServiceImpl;
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

@Service
public class UserService{
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    UmsUserServiceImpl umsUserService;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<UmsUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UmsUser umsUser = umsUserService.getOne(queryWrapper);
        if (umsUser == null)
            throw new UsernameNotFoundException("该用户不存在");
        TcmUserDetails tcmUserDetails = new TcmUserDetails();
        tcmUserDetails.setUsername(umsUser.getUsername());
        tcmUserDetails.setPassword(umsUser.getPassword());
        tcmUserDetails.setEnabled(umsUser.getEnabled());
        return tcmUserDetails;
    }

    public String Login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                Asserts.fail("密码不正确");
            }
            if(!userDetails.isEnabled()){
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            System.out.println("登录异常");
        }
        return token;
    }

    public void Register(String email, String password) {
        if (CheckUsernameExist(email))
            Asserts.fail("该邮箱已被注册");
        UmsUser user = new UmsUser();
        user.setId(0);
        user.setUsername(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setEnabled(1);
        umsUserService.save(user);
    }

    public boolean CheckUsernameExist(String username) {
        QueryWrapper<UmsUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UmsUser umsUser = umsUserService.getOne(queryWrapper);
        return umsUser != null;
    }

}
