// src/main/java/com/amk/controller/CommentController.java
package com.amk.controller;

import com.amk.pojo.Comment;
import com.amk.service.CommentService;
import com.amk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 发表评论（需要登录）
    @PostMapping("add")
    public Result add(@RequestBody Comment comment, @RequestHeader("token") String token) {
        return commentService.addComment(comment, token);
    }

    // 获取某头条下的所有评论
    @GetMapping("list")
    public Result list(Integer hid) {
        return commentService.listComments(hid);
    }
}