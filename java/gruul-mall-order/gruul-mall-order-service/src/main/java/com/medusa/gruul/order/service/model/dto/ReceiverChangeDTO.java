package com.medusa.gruul.order.service.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/8/2
 */
@Getter
@Setter
@ToString
public class ReceiverChangeDTO extends ReceiverDTO {
    /**
     * 店铺订单号 为空修改主订单收货地址
     */
    private String shopOrderNo;
    /**
     * 店铺 id 为空修改主订单收货地址
     */
    private Long shopId;
}
