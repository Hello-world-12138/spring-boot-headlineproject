package com.amk.controller;

import com.amk.pojo.Headline;
import com.amk.service.HeadlineService;
import com.amk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 阿明楷
 * @Date 2025/11/15:14:56
 * @See:
 */
@CrossOrigin
@RestController
@RequestMapping("headline")
public class HeadlineController {

    @Autowired
    private HeadlineService headlineService;


    //登录以后才可以访问
    @PostMapping("publish")
    public Result publish(
            @RequestBody Headline headline,
            @RequestHeader("token") String token) {

        return headlineService.publish(headline, token);
    }


    @PostMapping("findHeadlineByHid")
    public Result findHeadlineByHid(Integer hid){
        Headline headline = headlineService.getById(hid);
        Map data = new HashMap();
        data.put("headline",headline);
        return Result.ok(data);

    }

    @PostMapping("update")
    public Result update(@RequestBody Headline headline){
        Result result=headlineService.updateData(headline);
        return result;
    }

    @PostMapping("removeByHid")
    public Result removeByHid(Integer hid){
        headlineService.removeById(hid);
        return Result.ok(null);
    }
}
