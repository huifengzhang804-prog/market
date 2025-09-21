package com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/7
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class GetRechargeQrcodeResp {

    /**
     * base64_qrcode	string	是
     * img/png（base64 图片流）
     */
    private String base64Qrcode;
}
