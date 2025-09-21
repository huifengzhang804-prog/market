package com.medusa.gruul.order.api.model;

import com.medusa.gruul.order.api.enums.ErrorHandlerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/9/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResetOrderStatusDTO implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 订单状态重置状态
     */
    private ErrorHandlerStatus status;

}
