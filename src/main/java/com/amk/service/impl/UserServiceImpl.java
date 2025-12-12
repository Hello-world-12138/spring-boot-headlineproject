package com.amk.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.amk.utils.JwtHelper;
import com.amk.utils.MD5Util;
import com.amk.utils.Result;
import com.amk.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.amk.pojo.User;
import com.amk.service.UserService;
import com.amk.mapper.UserMapper;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Date;
import java.util.Map;

/**
* @author 阿明楷
* @description 针对表【news_user】的数据库操作Service实现
* @createDate 2025-11-10 15:37:44
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Autowired
   private UserMapper userMapper;

    @Autowired
    private JwtHelper jwtHelper;

    //登录业务
    //1.根据账户查询用户对象 -loginUser
    //2.如果对象为空，查询失败，账号错误，501
    //3.对比密码，失败返回503
    //4.根据用户id生成一个token，token->result返回
    @Override
    public Result login(User user) {
        //根据账号查询数据
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>();
        lambdaQueryWrapper.eq(User::getUsername, user.getUsername());
        User loginUser = userMapper.selectOne(lambdaQueryWrapper);
        if(loginUser==null){
            return Result.build(null, ResultCodeEnum.USERNAME_ERROR);
        }
        //对比密码
        if(!StringUtils.isEmpty(user.getUserPwd())
            && MD5Util.encrypt(user.getUserPwd()).equals(loginUser.getUserPwd())){

            if (loginUser.getRole() != null && loginUser.getRole() == -1) {
                return Result.build(null, ResultCodeEnum.USER_BANNED);
            }
            //登录成功
            //根据用户id生成token
            String token = jwtHelper.createToken(Long.valueOf(loginUser.getUid()));
            //把token封装到result返回
            Map data = new HashMap();
            data.put("token", token);
            data.put("role", loginUser.getRole());
            return Result.ok(data);

        }

        //密码错误
        return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
    }


    //根据token获取用户数据
    //1.token是否在有效期
    //2.根据token解析userid
    //3.根据用户id查询数据
    //4.去掉密码，封装result结果返回即可
    @Override
    public Result getUserInfo(String token) {
        //是否过期，true过期
        boolean expiration = jwtHelper.isExpiration(token);
        if(expiration){
            //失效，未登录看待
            return Result.build(null,ResultCodeEnum.NOTLOGIN);

        }
        int userId = jwtHelper.getUserId(token).intValue();
        User user = userMapper.selectById(userId);
        user.setUsername("");
        user.setUserPwd("");
        Map data = new HashMap();
        data.put("loginUser", user);

        return Result.ok(data);
    }

    //查询账号是否可用
    //1.根据账号进行count查询
    //2.count==0可用
    //3.count>0不可用
    @Override
    public Result checkUserName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        Long count = userMapper.selectCount(queryWrapper);
        if(count==0){
            return Result.ok(null);
        }
        return Result.build(null,ResultCodeEnum.USERNAME_USED);
    }

    //1.依然检查账号是否已经被注册
    //2.密码加密处理
    //3.账号数据保存
    //4.保存结果
    @Override
    public Result regist(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        Long count = userMapper.selectCount(queryWrapper);
        if(count>0){
            return Result.build(null,ResultCodeEnum.USERNAME_USED);
        }
        user.setUserPwd(MD5Util.encrypt(user.getUserPwd()));
        user.setRole(0);
        user.setCreateTime(new Date());
        userMapper.insert(user);

        return Result.ok(null);
    }


}


