package com.amk.service.impl;

import com.amk.mapper.HeadlineMapper;
import com.amk.mapper.UserMapper;
import com.amk.pojo.BrowseHistory;
import com.amk.pojo.Headline;
import com.amk.pojo.User;
import com.amk.pojo.vo.PortalVo;
import com.amk.service.BrowseHistoryService;
import com.amk.service.HeadlineService;
import com.amk.utils.JwtHelper;
import com.amk.utils.Result;
import com.amk.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ???????
 */
@Transactional
@Service
public class HeadlineServiceImpl extends ServiceImpl<HeadlineMapper, Headline>
        implements HeadlineService {

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @Autowired
    private UserMapper userMapper;

    /**
     * ??????
     */
    @Override
    public Result findNewsPage(PortalVo portalVo) {
        IPage<Map> page = new Page<>(portalVo.getPageNum(), portalVo.getPageSize());
        headlineMapper.selectMyPage(page, portalVo);

        List<Map> records = page.getRecords();
        Map<String, Object> data = new HashMap<>();
        data.put("pageData", records);
        data.put("pageNum", page.getCurrent());
        data.put("pageSize", page.getSize());
        data.put("totalPage", page.getPages());
        data.put("totalSize", page.getTotal());

        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("pageInfo", data);
        return Result.ok(pageInfo);
    }

    /**
     * ???????????
     */
    @Override
    public Result showHeadlineDetail(Integer hid, String token) {
        return showHeadlineDetailForPortal(hid, token);
    }

    /**
     * ?????????????????
     */
    @Override
    public Result publish(Headline headline, String token) {
        int userId = jwtHelper.getUserId(token).intValue();
        headline.setPublisher(userId);
        headline.setPageViews(0);
        // 管理员发布直接已发布，普通用户为待审核
        Integer status = 0;
        try {
            User user = userMapper.selectById(userId);
            if (user != null && user.getRole() != null && user.getRole() == 1) {
                status = 1;
            }
        } catch (Exception ignored) {
        }
        headline.setStatus(status);
        headline.setIsDeleted(0);
        headline.setCreateTime(new Date());
        headline.setUpdateTime(new Date());
        headline.setVersion(0);
        headlineMapper.insert(headline);
        return Result.ok(null);
    }

    /**
     * ?????????????????
     */
    @Override
    public Result updateData(Headline headline) {
        Headline db = headlineMapper.selectById(headline.getHid());
        Integer version = db != null ? db.getVersion() : 0;
        Integer status = db != null ? db.getStatus() : 0;
        Integer pageViews = db != null ? db.getPageViews() : 0;
        headline.setVersion(version);
        headline.setStatus(status);
        headline.setPageViews(pageViews);
        headline.setUpdateTime(new Date());
        headlineMapper.updateById(headline);
        return Result.ok(null);
    }

    @Override
    public Result listMyHeadlines(String token) {
        Long userId = jwtHelper.getUserId(token);
        LambdaQueryWrapper<Headline> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Headline::getPublisher, userId)
                .eq(Headline::getIsDeleted, 0)
                .eq(Headline::getStatus, 1)
                .orderByDesc(Headline::getCreateTime);
        List<Headline> list = headlineMapper.selectList(wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("myHeadlines", list);
        return Result.ok(data);
    }

    @Override
    public Result listBrowseHistory(String token) {
        Long userId = jwtHelper.getUserId(token);
        List<Map<String, Object>> history = headlineMapper.selectBrowseHistory(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("browseHistory", history);
        return Result.ok(data);
    }

    @Override
    public Result showHeadlineDetailForPortal(Integer hid, String token) {
        Map data = headlineMapper.queryDetailMap(hid);
        if (data == null) {
            // ?????????????????????
            return Result.build(null, ResultCodeEnum.HEADLINE_REVIEW);
        }

        // ??????????????
        Integer status = null;
        Object statusObj = data.get("status");
        if (statusObj instanceof Number) {
            status = ((Number) statusObj).intValue();
        }

        Integer publisherRole = null;
        Object roleObj = data.get("publisherRole");
        if (roleObj instanceof Number) {
            publisherRole = ((Number) roleObj).intValue();
        }

        // ??????????????token ?????
        boolean isAdmin = false;
        if (token != null && !token.trim().isEmpty()) {
            try {
                if (!jwtHelper.isExpiration(token)) {
                    Long userIdLong = jwtHelper.getUserId(token);
                    if (userIdLong != null) {
                        User user = userMapper.selectById(userIdLong);
                        if (user != null && user.getRole() != null && user.getRole() == 1) {
                            isAdmin = true;
                        }
                    }
                }
            } catch (Exception e) {
                // token ????????????????????
            }
        }

        // ??????????????????????
        if (!isAdmin) {
            if (status == null || status != 1 || (publisherRole != null && publisherRole == -1)) {
                return Result.build(null, ResultCodeEnum.HEADLINE_REVIEW);
            }
        }

        Map<String, Object> headlineMap = new HashMap<>();
        headlineMap.put("headline", data);

        // ????????????????????
        try {
            Headline headline = new Headline();
            Integer dbHid = data.get("hid") == null ? null : ((Number) data.get("hid")).intValue();
            Integer dbVersion = data.get("version") == null ? null : ((Number) data.get("version")).intValue();
            Integer dbPageViews = data.get("pageViews") == null ? null : ((Number) data.get("pageViews")).intValue();
            if (dbHid != null && dbVersion != null && dbPageViews != null) {
                headline.setHid(dbHid);
                headline.setVersion(dbVersion);
                headline.setPageViews(dbPageViews + 1);
                headlineMapper.updateById(headline);
            }
        } catch (Exception e) {
            // ??????????????????
        }

        // ???????????????????????
        if (token != null && !token.trim().isEmpty()) {
            try {
                if (!jwtHelper.isExpiration(token)) {
                    Long userIdLong = jwtHelper.getUserId(token);
                    int userId = userIdLong.intValue();

                    LambdaUpdateWrapper<BrowseHistory> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.eq(BrowseHistory::getUid, userId)
                            .eq(BrowseHistory::getHid, hid)
                            .set(BrowseHistory::getBrowseTime, new Date());

                    boolean updated = browseHistoryService.update(wrapper);
                    if (!updated) {
                        BrowseHistory bh = new BrowseHistory();
                        bh.setUid(userId);
                        bh.setHid(hid);
                        bh.setBrowseTime(new Date());
                        browseHistoryService.save(bh);
                    }
                }
            } catch (Exception e) {
                // ???????????????
            }
        }

        return Result.ok(headlineMap);
    }
}


