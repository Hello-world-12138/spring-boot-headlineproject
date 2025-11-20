// src/main/java/com/amk/service/impl/BrowseHistoryServiceImpl.java
package com.amk.service.impl;

import com.amk.pojo.BrowseHistory;
import com.amk.mapper.BrowseHistoryMapper;
import com.amk.service.BrowseHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BrowseHistoryServiceImpl extends ServiceImpl<BrowseHistoryMapper, BrowseHistory> 
    implements BrowseHistoryService {
}