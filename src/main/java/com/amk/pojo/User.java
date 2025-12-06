package com.amk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

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

    @Version
    private Integer version;

    private Integer isDeleted;
}
