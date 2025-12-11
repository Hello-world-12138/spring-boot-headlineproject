package com.amk.mapper;

import com.amk.pojo.Headline;
import com.amk.pojo.vo.PortalVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
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


    List<Map<String, Object>> selectBrowseHistory(Long userId);

    IPage<Map> selectAdminPage(IPage<Map> page, @Param("status") Integer status);

    int approveHeadline(@Param("hid") Integer hid);

    List<Map<String, Object>> selectTopHeadlines();

    List<Map<String, Object>> selectHeadlineTrend(@Param("days") Integer days);

    Long countHeadlinesWithinDays(@Param("days") Integer days);

    void batchUpdateStatusByPublisher(@Param("publisher") Integer publisher, @Param("status") Integer status);

    
    /*// ======== 新增：用注解方式，强制提交 ========
    @Update("INSERT INTO browse_history (uid, hid, browse_time) " +
            "VALUES (#{uid}, #{hid}, NOW()) " +
            "ON DUPLICATE KEY UPDATE browse_time = NOW()")
    void upsertBrowseHistory(@Param("uid") Integer uid, @Param("hid") Integer hid);*/
}





