package com.medusa.gruul.carrier.pigeon.service.modules.sms.controller;

import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsConfigDto;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsCurrentConfigDTO;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.enums.SmsPlatformType;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsSign;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.SmsSignService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * sms配置
 *
 * @author xiaoq
 * Date : 2022-03-29 18:16
 */
@Slf4j
@RestController
@RequestMapping("/sms")
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('generalSet')")
public class SmsConfController {

    private final SmsSignService smsSignService;

    /**
     * 根据type 获取短信配置信息
     *
     * @param type 查询类型
     */
    @GetMapping("current/config/{type}")
    public Result<SmsConfigDto> smsConfig(@PathVariable(name = "type") SmsPlatformType type) {
        return Result.ok(smsSignService.smsConfig(type));
    }

    @Log("获取短信配置列表信息")
    @GetMapping("list")
    public Result<List<SmsSign>> list(){
        return Result.ok(smsSignService.smsList());
    }
    @Log("删除指定服务商的短信配置")
    @DeleteMapping("/remove/{type}")
    public Result<Void> remove(@PathVariable("type") SmsPlatformType type){
        smsSignService.removeBySmsPlatformType(type);
        return Result.ok();
    }

    @Log("使用指定供应商平台短信配置")
    @PutMapping("/using/{type}")
    public Result<Void> using(@PathVariable("type") SmsPlatformType type){
        smsSignService.using(type);
        return Result.ok();
    }
    /**
     * @param smsCurrentConfigDto
     * 保存/更新短信配置
     */
    @PostMapping("/saveAndEdit/current/config/{enableCaptcha}")
    public Result<Void> saveAndEditSmsConfig(@PathVariable("enableCaptcha")Boolean enableCaptcha,
                                             @RequestBody @Valid SmsCurrentConfigDTO smsCurrentConfigDto) {
        smsSignService.saveOrUpdateConf(smsCurrentConfigDto,enableCaptcha);
        return Result.ok();
    }


}
