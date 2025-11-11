package com.amk.controller;

import com.amk.mapper.UserMapper;
import com.amk.pojo.User;
import com.amk.service.UserService;
import com.amk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 阿明楷
 * @Date 2025/11/11:08:43
 * @See:
 */
@RestController//传入json数据
@RequestMapping("user")
@CrossOrigin//跨域
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result login(@RequestBody User user) {
    Result result = userService.login(user);
    return result;
    }

    @GetMapping("loginInfo")
    public Result getUserInfo(@RequestHeader String token) {
        Result result=userService.getUserInfo(token);
        return result;
    }

    @PostMapping("checkUserName")
    public Result checkUserName(String username){
    Result result=userService.checkUserName(username);
    return result;
    }

    @PostMapping("regist")
    public Result regist(@RequestBody User user){
        Result result=userService.regist(user);
        return result;

    }

}
