package com.scuoj.config;

import com.scuoj.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    //添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录接口和注册接口不拦截
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/api/user/login","/api/user/register","/api/email/sendEmailCode",
                "/api/email/checkEmailCode","/api/user/forgetPwd","/api/user/emailLogin","/api/user/adminLogin",
                "/api/user/isUserLogin","/api/user/isAdminLogin");
    }
}
