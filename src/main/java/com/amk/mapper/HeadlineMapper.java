package com.amk.mapper;

import com.amk.pojo.Headline;
import com.amk.pojo.vo.PortalVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @author 阿明楷
* @description 针对表【news_headline】的数据库操作Mapper
* @createDate 2025-11-10 15:37:44
* @Entity com.amk.pojo.Headline
*/
@Mapper
public interface HeadlineMapper extends BaseMapper<Headline> {

    IPage<Map> selectMyPage(IPage<Map> page, @Param("portalVo") PortalVo portalVo);

    Map queryDetailMap(Integer hid);
}




