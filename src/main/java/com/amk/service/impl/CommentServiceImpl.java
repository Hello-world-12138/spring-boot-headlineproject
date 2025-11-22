// CommentServiceImpl.java
package com.amk.service.impl;

import com.amk.pojo.Comment;
import com.amk.service.CommentService;
import com.amk.mapper.CommentMapper;
import com.amk.utils.JwtHelper;
import com.amk.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public Result addComment(Comment comment, String token) {
        Long userId = jwtHelper.getUserId(token);
        comment.setUid(userId.intValue());
        this.save(comment);
        return Result.ok(null);
    }

    @Override
    public Result listComments(Integer hid) {
        List<Map<String, Object>> list = this.baseMapper.selectMaps(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getHid, hid)
                .eq(Comment::getIsDeleted, 0)
                .orderByDesc(Comment::getCreateTime)
        );

        // 关联查询用户昵称（如果你想显示用户名）
        // 这里简单返回，你可以后续 join news_user
        Map<String, Object> data = new HashMap<>();
        data.put("commentList", list);
        return Result.ok(data);
    }
}