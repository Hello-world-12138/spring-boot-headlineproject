package com.amk.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * @TableName news_headline
 */
/*@TableName(value ="news_headline")*/
@Data
//table-prefix: news_ # 设置表的前缀  news+headline
public class Headline {
    @TableId
    private Integer hid;

    private String title;

    private String article;

    private Integer type;

    private Integer publisher;

    private Integer pageViews;

    private Date createTime;

    private Date updateTime;

    @Version
    private Integer version;

    private Integer isDeleted;
}