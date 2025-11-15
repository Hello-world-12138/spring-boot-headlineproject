package com.amk.interceptors;

import com.amk.utils.JwtHelper;
import com.amk.utils.Result;
import com.amk.utils.ResultCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author 阿明楷
 * @Date 2025/11/15:15:28
 * @See:
 * 登录包含拦截器，检查请求头是否包含有效的token
 * 有or有效，放行
 * 没有or无效，返回504
 */
@Component
public class LoginProtectedInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {

        //从请求头中获取token
        String token = request.getHeader("token");
        //检查是否有效
        boolean expiration = jwtHelper.isExpiration(token);//判断是否过期

        //有效放行
        if (!expiration) {

            return true;
        }

        //无效返回504的状态json
        Result result =Result.build(null, ResultCodeEnum.NOTLOGIN);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(result);
        response.getWriter().print(json);
        return  false ;

    }

}
