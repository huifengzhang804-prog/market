package com.medusa.gruul.addon.platform.controller;


import com.medusa.gruul.addon.platform.model.dto.PlatformPrivacyAgreementDTO;
import com.medusa.gruul.addon.platform.service.PlatformPrivacyAgreementService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author wudi
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("platform/privacyAgreement")
public class PlatformPrivacyAgreementController {

    private final PlatformPrivacyAgreementService platformPrivacyAgreementService;


    /**
     * 编辑平台隐私协议
     *
     * @param privacyAgreement 隐私协议
     */
    @PostMapping
    @Log("平台隐私协议")
    public Result<Void> editPlatformPrivacyAgreement(@RequestBody PlatformPrivacyAgreementDTO privacyAgreement) {
        platformPrivacyAgreementService.editPlatformPrivacyAgreement(privacyAgreement);
        return Result.ok();
    }

    /**
     * 查询隐私协议
     *
     * @return 平台隐私协议
     */
    @GetMapping
    @Log("查询隐私协议")
    public Result<Map<String, Object>> getPlatformPrivacyAgreement() {
        return Result.ok(
                platformPrivacyAgreementService.getPlatformPrivacyAgreement()
        );
    }

    /**
     * 删除隐私协议
     *
     * @param privacyAgreementId 平台隐私协议id
     */
    @DeleteMapping("/{privacyAgreementId}")
    @Log("删除隐私协议")
    public Result<Void> deletePlatformPrivacyAgreement(@PathVariable Long privacyAgreementId) {
        platformPrivacyAgreementService.deletePlatformPrivacyAgreement(privacyAgreementId);
        return Result.ok();
    }

}
