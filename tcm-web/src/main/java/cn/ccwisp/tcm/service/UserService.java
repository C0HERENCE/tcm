package cn.ccwisp.tcm.service;

import cn.ccwisp.tcm.security.util.JwtTokenUtil;
import cn.ccwisp.tcm.bo.TcmUserDetails;
import cn.ccwisp.tcm.common.exception.Asserts;
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

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TcmUserDetails tcmUserDetails = new TcmUserDetails();
        tcmUserDetails.setUsername("123");
        tcmUserDetails.setPassword(new BCryptPasswordEncoder().encode("4576"));
        if (tcmUserDetails == null)
            throw new UsernameNotFoundException("该用户不存在");
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
}
