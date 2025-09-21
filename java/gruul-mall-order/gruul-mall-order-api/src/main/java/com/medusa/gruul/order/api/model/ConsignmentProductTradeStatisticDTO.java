package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>代销商品交易统计DTO</p>
 *
 * @author Andy.Yan
 */
@Getter
@Setter
public class ConsignmentProductTradeStatisticDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6849773151514110448L;

    private String xDate;

    private Long tradeNumber = 0L;

    private Long tradeAmount = 0L;
}
