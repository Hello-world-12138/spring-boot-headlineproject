package com.amk.controller;

import com.amk.mapper.CommentMapper;
import com.amk.mapper.HeadlineMapper;
import com.amk.mapper.UserMapper;
import com.amk.pojo.Comment;
import com.amk.pojo.Headline;
import com.amk.pojo.User;
import com.amk.utils.JwtHelper;
import com.amk.utils.Result;
import com.amk.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private JwtHelper jwtHelper;

    /**
     * 头条列表（支持状态筛选）
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
     * 删除头条
     */
    @PostMapping("headline/remove")
    public Result removeHeadline(@RequestBody Map<String, Integer> body) {
        Integer hid = body.get("hid");
        if (hid == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("参数缺失");
        }
        headlineMapper.deleteById(hid);
        return Result.ok(null);
    }

    /**
     * 审核通过
     */
    @PostMapping("headline/approve")
    public Result approve(@RequestBody Map<String, Integer> body) {
        Integer hid = body.get("hid");
        if (hid == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("参数缺失");
        }
        int updated = headlineMapper.approveHeadline(hid);
        if (updated == 0) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("审核失败");
        }
        return Result.ok(null);
    }

    /**
     * 用户列表
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
     * 封禁/解禁
     */
    @PostMapping("user/ban")
    public Result banUser(@RequestBody Map<String, Object> body) {

        Integer uid = body.get("uid") == null ? null : Integer.valueOf(body.get("uid").toString());
        Object bannedObj = body.get("banned");

        if (uid == null || bannedObj == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("参数缺失");
        }

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

        // 封禁=-1，解禁=0（管理员不变）
        int targetRole = banned ? -1 : ((user.getRole() != null && user.getRole() == 1) ? 1 : 0);
        user.setRole(targetRole);
        userMapper.updateById(user);

        // 封禁则头条设为待审核，解禁则设为已发布
        if (banned) {
            headlineMapper.batchUpdateStatusByPublisher(uid, 0);
        } else {
            headlineMapper.batchUpdateStatusByPublisher(uid, 1);
        }

        return Result.ok(null);
    }

    /**
     * 登录信息
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
     * 数据面板：概览
     */
    @GetMapping("dashboard/overview")
    public Result overview() {
        long totalUsers = userMapper.selectCount(new QueryWrapper<User>().eq("is_deleted", 0));
        long totalHeadlines = headlineMapper.selectCount(new QueryWrapper<Headline>().eq("is_deleted", 0));

        Long recentUsers = userMapper.countUsersWithinDays(7);
        Long recentHeadlines = headlineMapper.countHeadlinesWithinDays(7);

        // 封禁用户总数
        QueryWrapper<User> bannedWrapper = new QueryWrapper<>();
        bannedWrapper.eq("role", -1)
                .and(w -> w.eq("is_deleted", 0).or().isNull("is_deleted"));
        long bannedUsers = userMapper.selectCount(bannedWrapper);

        // 近七日评论数
        QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("is_deleted", 0)
                .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL {0} DAY)", 7);
        long recentComments = commentMapper.selectCount(commentWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("recent7Users", recentUsers == null ? 0 : recentUsers);
        data.put("recent7Headlines", recentHeadlines == null ? 0 : recentHeadlines);
        data.put("recent7Comments", recentComments);
        data.put("bannedUsers", bannedUsers);
        data.put("totalUsers", totalUsers);
        data.put("totalHeadlines", totalHeadlines);
        return Result.ok(data);
    }

    /**
     * 数据面板：趋势（默认7天）
     */
    @GetMapping("dashboard/trend")
    public Result trend(@RequestParam(value = "days", required = false, defaultValue = "7") Integer days) {
        List<Map<String, Object>> userTrend = userMapper.selectUserTrend(days);
        List<Map<String, Object>> headlineTrend = headlineMapper.selectHeadlineTrend(days);

        // 评论趋势
        QueryWrapper<Comment> commentWrapper = new QueryWrapper<>();
        commentWrapper.eq("is_deleted", 0)
                .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL {0} DAY)", days)
                .select("DATE(create_time) as dt", "COUNT(*) as cnt")
                .groupBy("DATE(create_time)")
                .orderByAsc("dt");
        List<Map<String, Object>> commentTrend = commentMapper.selectMaps(commentWrapper);

        // 封禁用户趋势
        QueryWrapper<User> bannedWrapper = new QueryWrapper<>();
        bannedWrapper.eq("role", -1)
                .and(w -> w.eq("is_deleted", 0).or().isNull("is_deleted"))
                .apply("create_time >= DATE_SUB(CURDATE(), INTERVAL {0} DAY)", days)
                .select("DATE(create_time) as dt", "COUNT(*) as cnt")
                .groupBy("DATE(create_time)")
                .orderByAsc("dt");
        List<Map<String, Object>> bannedTrend = userMapper.selectMaps(bannedWrapper);

        List<String> dates = new ArrayList<>();
        List<Long> newUsers = new ArrayList<>();
        List<Long> newHeadlines = new ArrayList<>();
        List<Long> newComments = new ArrayList<>();
        List<Long> newBannedUsers = new ArrayList<>();

        Map<String, Long> userMap = new HashMap<>();
        for (Map<String, Object> m : userTrend) {
            Object dt = m.get("dt");
            Object cnt = m.get("cnt");
            userMap.put(String.valueOf(dt), cnt == null ? 0L : ((Number) cnt).longValue());
        }
        Map<String, Long> headlineMap = new HashMap<>();
        for (Map<String, Object> m : headlineTrend) {
            Object dt = m.get("dt");
            Object cnt = m.get("cnt");
            headlineMap.put(String.valueOf(dt), cnt == null ? 0L : ((Number) cnt).longValue());
        }
        Map<String, Long> commentMap = new HashMap<>();
        for (Map<String, Object> m : commentTrend) {
            Object dt = m.get("dt");
            Object cnt = m.get("cnt");
            commentMap.put(String.valueOf(dt), cnt == null ? 0L : ((Number) cnt).longValue());
        }
        Map<String, Long> bannedMap = new HashMap<>();
        for (Map<String, Object> m : bannedTrend) {
            Object dt = m.get("dt");
            Object cnt = m.get("cnt");
            bannedMap.put(String.valueOf(dt), cnt == null ? 0L : ((Number) cnt).longValue());
        }

        for (int i = days - 1; i >= 0; i--) {
            String dateStr = LocalDate.now().minusDays(i).toString();
            dates.add(dateStr);
            newUsers.add(userMap.getOrDefault(dateStr, 0L));
            newHeadlines.add(headlineMap.getOrDefault(dateStr, 0L));
            newComments.add(commentMap.getOrDefault(dateStr, 0L));
            newBannedUsers.add(bannedMap.getOrDefault(dateStr, 0L));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("newUsers", newUsers);
        data.put("newHeadlines", newHeadlines);
        data.put("newComments", newComments);
        data.put("newBannedUsers", newBannedUsers);
        return Result.ok(data);
    }

    /**
     * 数据面板：阅读量 TOP10
     */
    @GetMapping("dashboard/topHeadlines")
    public Result topHeadlines() {
        List<Map<String, Object>> list = headlineMapper.selectTopHeadlines();
        return Result.ok(list);
    }
}
