package com.medusa.gruul.addon.ic.modules.opens.uupt.model.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
public class RechargeResp {
    /**
     * H5充值URL
     */
    private String h5Url;

    /**
     * PC充值URL
     */
    private String webUrl;
}
