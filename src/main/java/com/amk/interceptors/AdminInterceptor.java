package com.amk.interceptors;

import com.amk.mapper.UserMapper;
import com.amk.pojo.User;
import com.amk.utils.JwtHelper;
import com.amk.utils.Result;
import com.amk.utils.ResultCodeEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员权限拦截器：校验 token 并校验 role=1，非管理员直接拒绝
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token) || jwtHelper.isExpiration(token)) {
            writeResult(response, Result.build(null, ResultCodeEnum.NOTLOGIN));
            return false;
        }
        Long userId = jwtHelper.getUserId(token);
        User user = userMapper.selectById(userId);
        if (user == null || user.getRole() == null || user.getRole() != 1) {
            writeResult(response, Result.build(null, ResultCodeEnum.NO_PERMISSION));
            return false;
        }
        request.setAttribute("adminUser", user);
        return true;
    }

    private void writeResult(HttpServletResponse response, Result<?> result) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().print(objectMapper.writeValueAsString(result));
    }
}
