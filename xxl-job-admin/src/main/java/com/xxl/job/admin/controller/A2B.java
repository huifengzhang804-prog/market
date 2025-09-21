package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 张治保
 * date 2023/3/18
 */
@RestController
@RequestMapping("/jobinfo/2b")
public class A2B {

    @Resource
    private XxlJobService xxlJobService;


    @PostMapping("/add")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> add(@RequestBody String data) {
        return xxlJobService.add(JacksonUtil.readValue(data, XxlJobInfo.class));
    }

    @PostMapping("/update")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> update(@RequestBody String data) {
        return xxlJobService.update(JacksonUtil.readValue(data, XxlJobInfo.class));
    }

    @PostMapping("/remove")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> remove(@RequestBody String data) {
        return xxlJobService.remove(JacksonUtil.readValue(data, XxlJobInfo.class).getId());
    }

    @PostMapping("/start")
    @PermissionLimit(limit = false)
    public ReturnT<String> start(@RequestBody String data) {
        return xxlJobService.start(JacksonUtil.readValue(data, XxlJobInfo.class).getId());
    }

    @PostMapping("/stop")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> stop(@RequestBody String data) {
        return xxlJobService.stop(JacksonUtil.readValue(data, XxlJobInfo.class).getId());
    }


}
