package com.amk.mapper;

import com.amk.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 阿明楷
* @description 针对表【news_user】的数据库操作Mapper
* @createDate 2025-11-10 15:37:44
* @Entity com.amk.pojo.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




