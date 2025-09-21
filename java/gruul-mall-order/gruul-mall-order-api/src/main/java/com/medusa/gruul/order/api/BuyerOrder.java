package com.medusa.gruul.order.api;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 买家订单 父模型
 *
 * @author 张治保
 * @since 2022/11/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BuyerOrder implements Serializable {

    /**
     * 卖家id
     */
    @NotNull
    private Long buyerId;

    /**
     * 订单号
     */
    @NotNull
    private String orderNo;

    /**
     * 是否是预计算
     */
    private Boolean budget = Boolean.FALSE;

}
