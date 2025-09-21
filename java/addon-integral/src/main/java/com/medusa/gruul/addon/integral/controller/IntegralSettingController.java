package com.medusa.gruul.addon.integral.controller;

import com.medusa.gruul.addon.integral.model.dto.IntegralSettingDTO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralSetting;
import com.medusa.gruul.addon.integral.mp.service.IIntegralSettingService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 积分设置控制层
 *
 * @author shishuqian
 * date 2023/2/9
 * time 11:18
 **/

@RestController
@RequestMapping("/integral/setting")
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('fatetrue:integral')")
public class IntegralSettingController {

    private final IIntegralSettingService iIntegralSettingService;

    /**
     * 新增或修改积分设置
     *
     * @param integralSettingDTO 积分设置信息dto
     * @return true成功    false失败
     * @author shishuqian
     */
    @PostMapping("/add")
    @Log("新增积分设置")
    @PreAuthorize("@S.platformPerm({'generalSet','fatetrue:integral'})")
    @Idem(1000)
    public Result<Boolean> add(@RequestBody @Valid IntegralSettingDTO integralSettingDTO) {
        return Result.ok(
                this.iIntegralSettingService.add(integralSettingDTO)
        );
    }

    /**
     * 获取积分设置信息
     *
     * @return 积分设置信息
     * @author shishuqian
     */
    @GetMapping("/get")
    @Log("获取积分设置")
    @PreAuthorize("permitAll()")
    public Result<IntegralSetting> get() {
        return Result.ok(
                this.iIntegralSettingService.getSetting()
        );
    }

}
