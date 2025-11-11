package com.amk.service;

import com.amk.pojo.User;
import com.amk.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 阿明楷
* @description 登录业务
* @createDate 2025-11-10 15:37:44
*/
public interface UserService extends IService<User> {

    Result login(User user);

    //根据token获取用户数据
    Result getUserInfo(String token);

    //检查账号是否可用
    //username账号
    Result checkUserName(String username);

    //注册业务

    Result regist(User user);
}
