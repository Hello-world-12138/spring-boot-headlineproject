package com.amk.controller;

import com.amk.pojo.vo.PortalVo;
import com.amk.service.HeadlineService;
import com.amk.service.TypeService;
import com.amk.service.UserService;
import com.amk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 阿明楷
 * @Date 2025/11/12:14:56
 * @See:
 * @description:首页的控制层
 */
@RestController
@RequestMapping("portal")
@CrossOrigin
public class PortalController {
    @Autowired
    private TypeService typeService;
    @Autowired
    private HeadlineService headlineService;

    @GetMapping("findAllTypes")
    public Result findAllTypes()
    {

        Result result = typeService.findAllTypes();
        return result;

    }


    @PostMapping("findNewsPage")
    public Result findNewsPage(@RequestBody PortalVo portalVo){
        Result result=headlineService.findNewsPage(portalVo);
        return result;
    }

    @PostMapping("showHeadlineDetail")
    public Result showHeadlineDetail(Integer hid){

        Result result = headlineService.showHeadlineDetail(hid);
        return result;
    }


}
