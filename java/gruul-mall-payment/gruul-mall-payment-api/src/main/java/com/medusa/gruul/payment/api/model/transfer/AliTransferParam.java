package com.medusa.gruul.payment.api.model.transfer;

import com.medusa.gruul.common.model.enums.PayType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 支付宝转账转账dto
 *
 * @author 张治保
 * date 2022/11/25
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
public class AliTransferParam extends TransferParam {

    /**
     * 转账支付类型
     */
    private final PayType payType = PayType.ALIPAY;

    /**
     * 转账标题 用于在支付宝用户的账单里显示
     */
    private String title;

    /**
     * 支付宝账号实名认证姓名
     */
    @NotBlank
    private String name;

    /**
     * 支付宝账号 手机号/邮箱
     */
    @NotBlank
    private String account;
}