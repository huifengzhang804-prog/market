package com.medusa.gruul.addon.platform.model.dto;


import lombok.Data;

/**
 * @author  wudi
 */
@Data
public class PlatformPrivacyAgreementDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 平台隐私协议
     */
    private String platformPrivacyAgreementText;
}
