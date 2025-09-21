package com.medusa.gruul.payment.api.model.transfer;

import com.medusa.gruul.common.model.enums.PayType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 微信账转账dto
 *
 * @author 张治保
 * date 2022/11/25
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
public class WechatTransferParam extends TransferParam {

    /**
     * 转账支付类型
     */
    private final PayType payType = PayType.WECHAT;

    /**
     * 微信openid
     */
    @NotBlank
    private String openid;

    /**
     * 客户端ip地址
     */
    private String ip;

}