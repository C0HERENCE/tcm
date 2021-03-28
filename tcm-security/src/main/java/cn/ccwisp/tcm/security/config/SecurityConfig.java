package cn.ccwisp.tcm.security.config;

import cn.ccwisp.tcm.security.component.JwtAuthenticationTokenFilter;
import cn.ccwisp.tcm.security.util.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public IgnoreUrlsPropertiesConfig ignoreUrlsConfig() {
        return new IgnoreUrlsPropertiesConfig();
    }

    @Bean
    public JwtPropertiesConfig jwtPropertiesConfig() {
        return new JwtPropertiesConfig();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 配置不需要身份认证的路径
        for (String u : ignoreUrlsConfig().getUrls()) {
            http.authorizeRequests().antMatchers(u).permitAll();
        }
        http
                .authorizeRequests()
                // 允许所有OPTIONS请求
                .antMatchers(HttpMethod.OPTIONS)
                .permitAll()
                .and()
                .authorizeRequests()
                // 所有请求都需要身份认证
                .anyRequest()
                .authenticated()
                .and()
                // 关闭CSRF, 不适用Session
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 自定义权限拒绝处理类
//                .exceptionHandling()
//                .accessDeniedHandler()
//                .authenticationEntryPoint(LoginUrlAuthenticationEntryPoint.class)
//                .and()
                // 配置JWT过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

}
