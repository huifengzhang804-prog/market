package com.medusa.gruul.addon.platform.service;

import com.medusa.gruul.addon.platform.model.dto.PlatformPrivacyAgreementDTO;

import java.util.Map;

/**
 * @author wudi
 */
public interface PlatformPrivacyAgreementService {


    /**
     * 编辑平台隐私协议
     * @param privacyAgreement 隐私协议
     */
    void editPlatformPrivacyAgreement(PlatformPrivacyAgreementDTO privacyAgreement);
    /**
     *  查询隐私协议
     * @return 平台隐私协议
     */
    Map<String, Object> getPlatformPrivacyAgreement();
    /**
     * 删除隐私协议
     * @param privacyAgreementId 平台隐私协议id
     */
    void deletePlatformPrivacyAgreement(Long privacyAgreementId);
}
