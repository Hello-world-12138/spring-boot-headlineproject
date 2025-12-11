package com.amk.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 轮播图实体，对应数据库表 banner
 */
@Data
@TableName("banner")
public class Banner {

    @TableId
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题/简介
     */
    private String subtitle;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 点击后跳转链接（通常为头条详情地址）
     */
    private String linkUrl;

    /**
     * 排序值，值越大越靠前
     */
    private Integer sort;

    /**
     * 状态：1=上线，0=下线
     */
    private Integer status;

    /**
     * 分类：默认/精选等
     */
    private String category;

    /**
     * 生效时间
     */
    private Date startTime;

    /**
     * 失效时间
     */
    private Date endTime;

    private Date createTime;

    private Date updateTime;
}

