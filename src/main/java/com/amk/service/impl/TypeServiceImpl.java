package com.amk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.amk.pojo.Type;
import com.amk.service.TypeService;
import com.amk.mapper.TypeMapper;
import org.springframework.stereotype.Service;

/**
* @author 阿明楷
* @description 针对表【news_type】的数据库操作Service实现
* @createDate 2025-11-10 15:37:44
*/
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type>
    implements TypeService{

}




