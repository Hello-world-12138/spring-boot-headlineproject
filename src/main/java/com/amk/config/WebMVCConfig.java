package com.amk.config;

import com.amk.interceptors.AdminInterceptor;
import com.amk.interceptors.LoginProtectedInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 阿明楷
 * @Date 2025/11/15:16:03
 * @See:
 */

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LoginProtectedInterceptor loginProtectedInterceptor;
    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(loginProtectedInterceptor).addPathPatterns("/headline/**");
        registry.addInterceptor(adminInterceptor).addPathPatterns("/admin/**");
    }

}
