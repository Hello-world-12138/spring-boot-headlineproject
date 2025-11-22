// CommentService.java
package com.amk.service;

import com.amk.pojo.Comment;
import com.amk.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommentService extends IService<Comment> {
    Result addComment(Comment comment, String token);
    Result listComments(Integer hid);
}