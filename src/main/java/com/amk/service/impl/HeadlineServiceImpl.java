package com.amk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.amk.pojo.Headline;
import com.amk.service.HeadlineService;
import com.amk.mapper.HeadlineMapper;
import org.springframework.stereotype.Service;

/**
* @author 阿明楷
* @description 针对表【news_headline】的数据库操作Service实现
* @createDate 2025-11-10 15:37:44
*/
@Service
public class HeadlineServiceImpl extends ServiceImpl<HeadlineMapper, Headline>
    implements HeadlineService{

}




