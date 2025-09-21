package com.medusa.gruul.addon.ic.modules.opens.judanke.model.merchant;

import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.DeliveryFeeFrom;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums.Switch;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BindMerchantParam implements Serializable {

    /**
     * 手机号 账户
     */
    private String phone;

    /**
     * 是否允许登录 1 允许 0 禁止 虚拟号不支持登录
     */
    private Switch allowLogin = Switch.ON;

    /**
     * 是否是虚拟号 1 虚拟号 0 非虚拟号 非虚拟号需要填写验证码
     */
    private Switch isVirtual = Switch.OFF;

    /**
     * 验证码  示例：444332（测试环境：123456 或 999999）
     */
    private String code;

    /**
     * 扣费账户（0=开放平台 ，1=商家） 默认为 0
     */
    private DeliveryFeeFrom deliveryFeeFrom = DeliveryFeeFrom.MERCHANT;

    /**
     * 是否关闭发单失败后自动重发 1是 0 否 默认为 0
     */
    private Switch closeRetrySend = Switch.ON;

}
