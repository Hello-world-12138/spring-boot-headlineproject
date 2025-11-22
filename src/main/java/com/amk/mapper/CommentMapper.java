// src/main/java/com/amk/mapper/CommentMapper.java
package com.amk.mapper;

import com.amk.pojo.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}