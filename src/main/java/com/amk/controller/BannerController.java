package com.amk.controller;

import com.amk.mapper.BannerMapper;
import com.amk.pojo.Banner;
import com.amk.utils.Result;
import com.amk.utils.ResultCodeEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轮播图相关接口
 */
@RestController
@CrossOrigin
@RequestMapping("banner")
public class BannerController {

    @Autowired
    private BannerMapper bannerMapper;

    /**
     * 前台轮播图列表
     */
    @GetMapping("list")
    public Result listFront(@RequestParam(value = "category", required = false) String category) {
        List<Banner> list = bannerMapper.selectFrontList(category);
        return Result.ok(list);
    }

    /**
     * 管理端分页查询轮播图
     */
    @PostMapping("admin/list")
    public Result listAdmin(@RequestBody(required = false) Map<String, Object> params) {
        long pageNum = params != null && params.get("pageNum") != null ? Long.parseLong(params.get("pageNum").toString()) : 1L;
        long pageSize = params != null && params.get("pageSize") != null ? Long.parseLong(params.get("pageSize").toString()) : 10L;
        String category = params != null ? (String) params.get("category") : null;

        Page<Map> page = new Page<>(pageNum, pageSize);
        IPage<Map> pageResult = bannerMapper.selectAdminPage(page, category);

        Map<String, Object> pageData = new HashMap<>();
        pageData.put("pageData", pageResult.getRecords());
        pageData.put("pageNum", pageResult.getCurrent());
        pageData.put("pageSize", pageResult.getSize());
        pageData.put("totalPage", pageResult.getPages());
        pageData.put("totalSize", pageResult.getTotal());
        return Result.ok(pageData);
    }

    /**
     * 新增/编辑轮播图
     */
    @PostMapping("admin/save")
    public Result save(@RequestBody Banner banner) {
        Date now = new Date();
        if (banner.getId() == null) {
            banner.setCreateTime(now);
            banner.setUpdateTime(now);
            if (banner.getStatus() == null) {
                banner.setStatus(1);
            }
            bannerMapper.insert(banner);
        } else {
            banner.setUpdateTime(now);
            bannerMapper.updateById(banner);
        }
        return Result.ok(null);
    }

    /**
     * 修改轮播图状态（上线/下线）
     */
    @PostMapping("admin/status")
    public Result changeStatus(@RequestBody Map<String, Object> body) {
        Object idObj = body.get("id");
        Object statusObj = body.get("status");
        if (idObj == null || statusObj == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("参数缺失");
        }
        Integer id = Integer.valueOf(idObj.toString());
        Integer status = Integer.valueOf(statusObj.toString());
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            return Result.build(null, ResultCodeEnum.SYSTEM_ERROR).message("轮播图不存在");
        }
        banner.setStatus(status);
        banner.setUpdateTime(new Date());
        bannerMapper.updateById(banner);
        return Result.ok(null);
    }
}

