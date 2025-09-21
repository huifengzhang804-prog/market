package com.medusa.gruul.addon.ic.modules.opens.judanke.model.printer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RecipientParam {
    /**
     * adderss	object	是
     * 地址信息
     */
    private AddressParam adderss;

    /**
     * mobile	string	是
     * 手机号
     */
    private String mobile;

    /**
     * name	string	是
     * 姓名
     */
    private String name;

    /**
     * privacy_phone	string	是
     * 虚拟号
     */
    private String privacyPhone;
}
