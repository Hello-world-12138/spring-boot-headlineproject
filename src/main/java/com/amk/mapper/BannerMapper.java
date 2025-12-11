package com.amk.mapper;

import com.amk.pojo.Banner;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 轮播图 Mapper
 */
@Mapper
public interface BannerMapper extends BaseMapper<Banner> {

    /**
     * 前台查询轮播列表
     */
    List<Banner> selectFrontList(@Param("category") String category);

    /**
     * 管理端分页查询轮播列表
     */
    IPage<Map> selectAdminPage(Page<Map> page, @Param("category") String category);
}

