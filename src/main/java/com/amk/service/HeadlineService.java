package com.amk.service;

import com.amk.pojo.Headline;
import com.amk.pojo.vo.PortalVo;
import com.amk.utils.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 阿明楷
* @description 针对表【news_headline】的数据库操作Service
* @createDate 2025-11-10 15:37:44
*/
public interface HeadlineService extends IService<Headline> {

    //首页数据查询
    Result findNewsPage(PortalVo portalVo);

    //根据id查询头条详情
    Result showHeadlineDetail(Integer hid);
}
