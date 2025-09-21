package com.medusa.gruul.user.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.service.model.dto.UserGrowthValueSettingsDTO;
import com.medusa.gruul.user.service.mp.entity.UserGrowthValueSettings;
import com.medusa.gruul.user.service.service.UserGrowthValueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 会员成长值设置 前端控制器
 *
 * @author WuDi
 * @since 2023-05-15
 */
@RestController
@PreAuthorize("@S.platformPerm('vip:setting')")
@RequiredArgsConstructor
@RequestMapping("/user/growthValue/settings")
public class UserGrowthValueSettingsController {


    private final UserGrowthValueService userGrowthValueService;

    /**
     * 获取会员成长值设置
     */
    @Log("获取会员成长值设置")
    @GetMapping
    public Result<UserGrowthValueSettings> getUserGrowthValueSettings() {
        return Result.ok(
                userGrowthValueService.getUserGrowthValueSettings()
        );
    }

    /**
     * 编辑会员成长值设置
     *
     * @param userGrowthValueSettings 会员成长值设置
     */
    @Log("编辑会员成长值设置")
    @PostMapping
    public Result<Void> updateUserGrowthValueSettings(@RequestBody @Valid UserGrowthValueSettingsDTO userGrowthValueSettings) {
        userGrowthValueService.updateUserGrowthValueSettings(userGrowthValueSettings);
        return Result.ok();
    }



}
