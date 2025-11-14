package com.amk.pojo.vo;

import lombok.Data;

/**
 * @author 阿明楷
 * @Date 2025/11/12:15:14
 * @See:
 */
//定义参数用来接收前端传参
@Data
public class PortalVo {
    private String keyWords;

    private int type=0;

    private int pageNum=1;
    private int pageSize=10;
}
