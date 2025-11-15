package com.amk.service.impl;

import com.amk.pojo.vo.PortalVo;
import com.amk.utils.JwtHelper;
import com.amk.utils.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.amk.pojo.Headline;
import com.amk.service.HeadlineService;
import com.amk.mapper.HeadlineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author 阿明楷
* @description 针对表【news_headline】的数据库操作Service实现
* @createDate 2025-11-10 15:37:44
*/
@Service
public class HeadlineServiceImpl extends ServiceImpl<HeadlineMapper, Headline>
    implements HeadlineService{

    @Autowired
    private HeadlineMapper headlineMapper;

    @Autowired
    private JwtHelper jwtHelper;



    /**
     *
     * @param portalVo
     * @return
     * 首页数据查询
     * 1.进行分页数据查询
     * 2.分页数据，拼接到result即可
     * 注意1.查询需要自定义语句，自定义mapper方法，携带分页
     * 注意2。返回结果list<Map>
     */
    @Override
    public Result findNewsPage(PortalVo portalVo) {

        IPage<Map> page =new Page<>(portalVo.getPageNum(),portalVo.getPageSize());
        headlineMapper.selectMyPage(page,portalVo);

        List<Map> records = page.getRecords();
        Map data = new HashMap();
        data.put("pageData",records);
        data.put("pageNum",page.getCurrent());
        data.put("pageSize",page.getSize());
        data.put("totalPage",page.getPages());
        data.put("totalSize",page.getTotal());

        Map pageInfo = new HashMap();
        pageInfo.put("pageInfo",data);

        return Result.ok(pageInfo);
    }

    /**
     *
     * 1.查询对应的数据【多表查询，头条和用户表，方法需要自定义，返回map即可】
     * 2.修改阅读量【version乐观锁 当前数据对应的版本】
     * @param hid
     * @return
     */
    @Override
    public Result showHeadlineDetail(Integer hid) {

        Map data=headlineMapper.queryDetailMap(hid);
        Map headlineMap=new HashMap();
        headlineMap.put("headline",data);

        //修改阅读量+1
        Headline headline=new Headline();
        headline.setHid((Integer)data.get("hid"));
        headline.setVersion((Integer)data.get("version"));
        //阅读量+1
        headline.setPageViews((Integer)data.get("pageViews")+1);
        headlineMapper.updateById(headline);

        return Result.ok(headlineMap);

    }

    /**
     * 头条发布方法
     * 1.补全数据
     * @param headline
     * @param token
     * @return
     */

    @Override
    public Result publish(Headline headline,String token) {
        //token查询用户id
        int userId =jwtHelper.getUserId(token).intValue();
        //数据装配
        headline.setPublisher(userId);
        headline.setPageViews(0);
        headline.setCreateTime(new Date());
        headline.setUpdateTime(new Date());

        headlineMapper.insert(headline);
        return Result.ok(null);
    }

    /**
     * 修改头条数据
     * 1.hid查询数据的最新version
     * 2，修改数据的修改时间为当前节点
     * @param headline
     * @return
     */
    @Override
    public Result updateData(Headline headline) {
        Integer version=headlineMapper.selectById(headline.getHid()).getVersion();
        headline.setVersion(version);//乐观锁
        headline.setUpdateTime(new Date());
        headlineMapper.updateById(headline);
        return Result.ok(null);
    }
}




