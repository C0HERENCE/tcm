package cn.ccwisp.tcm.config;

import cn.ccwisp.tcm.security.config.SecurityConfig;
import cn.ccwisp.tcm.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled=true,jsr250Enabled=true)
public class TcmSecurityConfig extends SecurityConfig {

    @Autowired
    private AuthService authService;

    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> authService.loadUserByUsername(username);
    }
}
