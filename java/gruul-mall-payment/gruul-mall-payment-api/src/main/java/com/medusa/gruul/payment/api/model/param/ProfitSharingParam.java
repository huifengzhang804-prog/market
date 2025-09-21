package com.medusa.gruul.payment.api.model.param;

import com.medusa.gruul.common.model.base.ShopOrderKey;
import com.medusa.gruul.global.model.enums.ReceiverType;
import com.medusa.gruul.global.model.o.MessageKey;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2023/6/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProfitSharingParam implements Serializable {

    /**
     * 分账单号
     */
    @NotBlank
    private String sharingNo;

    /**
     * 店铺id
     */
    @NotNull
    private ShopOrderKey shopOrderKey;

    /**
     * 是否解冻剩余资金 如果确定是最后一笔分账 则需设置为true 解冻剩余资金
     */
    @NotNull
    private Boolean unfreeze;

    /**
     * 分账接收方列表
     */
    @NotNull
    @Size(min = 1)
    @Valid
    private List<Receiver> receivers;

    /**
     * 分账结果回调通知key
     */
    @NotNull
    private MessageKey notifyKey;

    /**
     * 分账接收方
     */
    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class Receiver {

        /**
         * 分账接收方类型
         */
        @NotNull
        private ReceiverType type;

        /**
         * 分账接收方帐号
         * 分账接收方类型为PERSONAL_OPENID时，分账接收方账号为个人openid
         */
        private Long accountId;

        /**
         * 分账金额
         */
        @NotNull
        private Long amount;

        /**
         * 分账描述 如分销佣金分成
         */
        @NotBlank
        private String description;


    }
}
