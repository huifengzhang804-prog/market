package com.medusa.gruul.order.api.model;

import com.medusa.gruul.common.module.app.afs.AfsStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/8/9
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ChangeAfsStatusDTO implements Serializable {
    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;
    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;
    /**
     * 订单商品项id
     */
    @NotNull
    private Long itemId;
    /**
     * 售后工单号
     */
    @NotBlank
    private String afsNo;
    /**
     * 售后状态
     */
    @NotNull
    private AfsStatus status;

    /**
     * 售后成功退款的交易流水号
     */
    private String afsTradeNo;

    /**
     * 售后退款金额
     */
    private Long refundAmount;
}
