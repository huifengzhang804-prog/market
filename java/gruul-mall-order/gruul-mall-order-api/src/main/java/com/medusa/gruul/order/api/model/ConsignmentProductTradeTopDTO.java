package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>代销商品交易Top DTO</p>
 *
 * @author Andy.Yan
 */
@Getter
@Setter
public class ConsignmentProductTradeTopDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6849773151514110448L;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;


    /**
     * 交易金额
     */
    private Long tradeAmount;

    /**
     * 交易数量
     */
    private Long tradeNumber;
}
