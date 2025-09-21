package com.medusa.gruul.addon.platform.service.impl;

import com.medusa.gruul.addon.platform.model.dto.PlatformPrivacyAgreementDTO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformPrivacyAgreement;
import com.medusa.gruul.addon.platform.mp.service.IPlatformPrivacyAgreementService;
import com.medusa.gruul.addon.platform.service.PlatformPrivacyAgreementService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wudi
 */
@Service("platformPrivacyAgreementService")
@RequiredArgsConstructor
public class PlatformPrivacyAgreementServiceImpl implements PlatformPrivacyAgreementService {

    private final IPlatformPrivacyAgreementService platformPrivacyAgreementService;

    /**
     * 编辑平台隐私协议
     *
     * @param privacyAgreement 隐私协议
     */
    @Override
    public void editPlatformPrivacyAgreement(PlatformPrivacyAgreementDTO privacyAgreement) {
        PlatformPrivacyAgreement platformPrivacyAgreement = new PlatformPrivacyAgreement();
        platformPrivacyAgreement.setPlatformPrivacyAgreementText(privacyAgreement.getPlatformPrivacyAgreementText());
        platformPrivacyAgreement.setId(privacyAgreement.getId());
        boolean status = platformPrivacyAgreementService.saveOrUpdate(platformPrivacyAgreement);
        if (!status) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "新增或编辑失败");
        }
    }

    /**
     * 查询隐私协议
     *
     * @return 平台隐私协议
     */
    @Override
    public Map<String, Object> getPlatformPrivacyAgreement() {
        PlatformPrivacyAgreement platformPrivacyAgreement =
                platformPrivacyAgreementService.lambdaQuery().one();
        if (platformPrivacyAgreement == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>(CommonPool.NUMBER_TWO);
        map.put("id", platformPrivacyAgreement.getId());
        map.put("platformPrivacyAgreementText", platformPrivacyAgreement.getPlatformPrivacyAgreementText());
        return map;
    }

    /**
     * 删除隐私协议
     *
     * @param privacyAgreementId 平台隐私协议id
     */
    @Override
    public void deletePlatformPrivacyAgreement(Long privacyAgreementId) {
        boolean remove = platformPrivacyAgreementService.lambdaUpdate()
                .eq(PlatformPrivacyAgreement::getId, privacyAgreementId)
                .remove();
        if (!remove) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, "删除平台隐私协议失败");
        }
    }
}
