package com.medusa.gruul.addon.ic.modules.uupt.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/11/5
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UuptShopRechargeVO {

    /**
     * h5充值二维码 base64 格式图片
     */
    private String h5Qrcode;

    /**
     * pc充值链接
     */
    private String pcUrl;

}
