    // src/main/java/com/amk/pojo/Comment.java
package com.amk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("news_comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer hid;
    private Integer uid;
    private String content;
    private Date createTime;
    @TableLogic
    private Integer isDeleted;
}