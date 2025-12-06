package com.amk.controller;

import com.amk.mapper.HeadlineMapper;
import com.amk.mapper.UserMapper;
import com.amk.pojo.Headline;
import com.amk.pojo.User;
import com.amk.utils.JwtHelper;
import com.amk.utils.Result;
import com.amk.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ????????????????????
 */
@RestController
@CrossOrigin
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtHelper jwtHelper;

    /**
     * ????????????
     */
    @PostMapping("headline/list")
    public Result listHeadlines(@RequestBody(required = false) Map<String, Object> params) {
        long pageNum = params != null && params.get("pageNum") != null ? Long.parseLong(params.get("pageNum").toString()) : 1L;
        long pageSize = params != null && params.get("pageSize") != null ? Long.parseLong(params.get("pageSize").toString()) : 10L;
        Integer status = null;
        if (params != null && params.get("status") != null) {
            status = Integer.parseInt(params.get("status").toString());
        }
        IPage<Map> page = new Page<>(pageNum, pageSize);
        headlineMapper.selectAdminPage(page, status);
        Map<String, Object> pageData = new HashMap<>();
        pageData.put("pageData", page.getRecords());
        pageData.put("pageNum", page.getCurrent());
        pageData.put("pageSize", page.getSize());
        pageData.put("totalPage", page.getPages());
        pageData.put("totalSize", page.getTotal());
        return Result.ok(pageData);
    }

    /**
     * ????
     */
    @PostMapping("headline/remove")
    public Result removeHeadline(@RequestBody Map<String, Integer> body) {
        Integer hid = body.get("hid");
        if (hid == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("????");
        }
        headlineMapper.deleteById(hid);
        return Result.ok(null);
    }

    /**
     * ????
     */
    @PostMapping("headline/approve")
    public Result approve(@RequestBody Map<String, Integer> body) {
        Integer hid = body.get("hid");
        if (hid == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("????");
        }
        int updated = headlineMapper.approveHeadline(hid);
        if (updated == 0) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("????");
        }
        return Result.ok(null);
    }

    /**
     * 用户列表（分页）
     */
    @PostMapping("user/list")
    public Result listUsers(@RequestBody(required = false) Map<String, Object> params) {
        long pageNum = params != null && params.get("pageNum") != null ? Long.parseLong(params.get("pageNum").toString()) : 1L;
        long pageSize = params != null && params.get("pageSize") != null ? Long.parseLong(params.get("pageSize").toString()) : 10L;

        Page<Map> page = new Page<>(pageNum, pageSize);
        IPage<Map> pageResult = userMapper.selectAdminUserPage(page);

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("pageData", pageResult.getRecords());
        pageData.put("pageNum", pageResult.getCurrent());
        pageData.put("pageSize", pageResult.getSize());
        pageData.put("totalPage", pageResult.getPages());
        pageData.put("totalSize", pageResult.getTotal());
        return Result.ok(pageData);
    }

    /**
     * 封禁 / 解禁用户
     */
    @PostMapping("user/ban")
    public Result banUser(@RequestBody Map<String, Object> body) {

        Integer uid = body.get("uid") == null ? null : Integer.valueOf(body.get("uid").toString());
        Object bannedObj = body.get("banned");

        if (uid == null || bannedObj == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("参数缺失");
        }

        // 支持 true/false 或 "true"/"false" 或 1/0
        boolean banned;
        if (bannedObj instanceof Boolean) {
            banned = (Boolean) bannedObj;
        } else {
            String val = bannedObj.toString().trim().toLowerCase();
            banned = "true".equals(val) || "1".equals(val);
        }

        User user = userMapper.selectById(uid);
        if (user == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("用户不存在");
        }

        // 统一逻辑：封禁 = -1，解禁 = 0
        int targetRole = banned ? -1 : 0;

        user.setRole(targetRole);
        userMapper.updateById(user);

        // 禁用则头条改为待审核，解禁则改为发布
        headlineMapper.batchUpdateStatusByPublisher(uid, banned ? 0 : 1);

        return Result.ok(null);
    }

    /**
     * ????
     */
    @GetMapping("loginInfo")
    public Result adminInfo(@RequestHeader("token") String token) {
        if (jwtHelper.isExpiration(token)) {
            return Result.build(null, ResultCodeEnum.NOTLOGIN);
        }
        Long userId = jwtHelper.getUserId(token);
        User user = userMapper.selectById(userId);
        if (user == null || user.getRole() == null || user.getRole() != 1) {
            return Result.build(null, ResultCodeEnum.NO_PERMISSION);
        }
        user.setUserPwd("");
        Map<String, Object> data = new HashMap<>();
        data.put("loginUser", user);
        return Result.ok(data);
    }

    /**
     * ???????
     */
    @GetMapping("dashboard/overview")
    public Result overview() {
        long totalUsers = userMapper.selectCount(new QueryWrapper<User>().eq("is_deleted", 0));
        long totalHeadlines = headlineMapper.selectCount(new QueryWrapper<Headline>().eq("is_deleted", 0));

        Long todayNewUsers = userMapper.countTodayUsers();
        Long todayNewHeadlines = headlineMapper.selectCount(new QueryWrapper<Headline>()
                .eq("is_deleted", 0)
                .eq("status", 1)
                .apply("DATE(create_time) = CURDATE()"));

        Map<String, Object> data = new HashMap<>();
        data.put("todayNewUsers", todayNewUsers == null ? 0 : todayNewUsers);
        data.put("todayNewHeadlines", todayNewHeadlines);
        data.put("totalUsers", totalUsers);
        data.put("totalHeadlines", totalHeadlines);
        return Result.ok(data);
    }

    /**
     * ??????????7??
     */
    @GetMapping("dashboard/trend")
    public Result trend(@RequestParam(value = "days", required = false, defaultValue = "7") Integer days) {
        List<Map<String, Object>> userTrend = userMapper.selectUserTrend(days);
        List<Map<String, Object>> headlineTrend = headlineMapper.selectHeadlineTrend(days);

        List<String> dates = new ArrayList<>();
        List<Long> newUsers = new ArrayList<>();
        List<Long> newHeadlines = new ArrayList<>();

        // ?????? map?????????
        Map<String, Long> userMap = new HashMap<>();
        for (Map<String, Object> m : userTrend) {
            userMap.put(String.valueOf(m.get("dt")), ((Number) m.get("cnt")).longValue());
        }
        Map<String, Long> headlineMap = new HashMap<>();
        for (Map<String, Object> m : headlineTrend) {
            headlineMap.put(String.valueOf(m.get("dt")), ((Number) m.get("cnt")).longValue());
        }

        for (int i = days - 1; i >= 0; i--) {
            String dateStr = java.time.LocalDate.now().minusDays(i).toString();
            dates.add(dateStr);
            newUsers.add(userMap.getOrDefault(dateStr, 0L));
            newHeadlines.add(headlineMap.getOrDefault(dateStr, 0L));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("newUsers", newUsers);
        data.put("newHeadlines", newHeadlines);
        return Result.ok(data);
    }

    /**
     * ???????? TOP10
     */
    @GetMapping("dashboard/topHeadlines")
    public Result topHeadlines() {
        List<Map<String, Object>> list = headlineMapper.selectTopHeadlines();
        return Result.ok(list);
    }
}


