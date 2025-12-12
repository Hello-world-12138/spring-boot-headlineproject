package com.amk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * @TableName news_user
 */

@Data
public class User {
    @TableId
    private Integer uid;

    private String username;

    private String userPwd;

    private String nickName;

    private Integer role;

    @TableField(value = "create_time")
    private Date createTime;

    @Version
    private Integer version;

    private Integer isDeleted;
}
