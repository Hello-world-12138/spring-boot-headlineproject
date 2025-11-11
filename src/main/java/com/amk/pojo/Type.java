package com.amk.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * @TableName news_type
 */

@Data
public class Type {
    @TableId
    private Integer tid;

    private String tname;

    @Version
    private Integer version;

    private Integer isDeleted;
}