package com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
public class BindMerchantResp implements Serializable {

    /**
     * 聚单客商家ID
     */
    private Long relativeUserId;

    /**
     * 手机号 账户
     */
    private String phone;

}
