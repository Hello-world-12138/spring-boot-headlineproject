package com.amk.mapper;

import com.amk.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
* @author 阿明楷
* @description 针对表【news_user】的数据操作Mapper
* @createDate 2025-11-10 15:37:44
* @Entity com.amk.pojo.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 管理端分页查询用户列表（使用 MyBatis-Plus 自动分页）
     */
    IPage<Map> selectAdminUserPage(Page<Map> page);

    Long countTodayUsers();

    Long countUsersWithinDays(@Param("days") Integer days);

    List<Map<String, Object>> selectUserTrend(@Param("days") Integer days);
}
