package com.test.springboot.activiti.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 登陆校验
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //去掉验证
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //解决post请求问题
        http.csrf().disable();

        http.authorizeRequests()
                .anyRequest().permitAll().and().logout().permitAll();//配置不需要登录验证
    }


}
