package com.amk.config;

import com.amk.mapper.UserMapper;
import com.amk.pojo.User;
import com.amk.utils.MD5Util;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 启动时自动插入管理员账号，保证 admin/admin 可用
 */
@Component
public class AdminDataInitializer {

    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    public void initAdmin() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, "admin");
        User admin = userMapper.selectOne(wrapper);
        if (admin == null) {
            User user = new User();
            user.setUsername("admin");
            user.setUserPwd(MD5Util.encrypt("admin123"));
            user.setNickName("超级管理员");
            user.setRole(1);
            user.setIsDeleted(0);
            userMapper.insert(user);
        } else {
            admin.setUserPwd(MD5Util.encrypt("admin123"));
            admin.setNickName("超级管理员");
            admin.setRole(1);
            admin.setIsDeleted(0);
            userMapper.updateById(admin);
        }
    }
}
