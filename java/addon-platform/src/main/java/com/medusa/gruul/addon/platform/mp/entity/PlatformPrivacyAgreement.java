package com.medusa.gruul.addon.platform.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author wudi
 * 平台隐私协议
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_platform_privacy_agreement")
public class PlatformPrivacyAgreement extends BaseEntity {

    /**
     * 平台隐私协议
     */
    private String platformPrivacyAgreementText;
}
