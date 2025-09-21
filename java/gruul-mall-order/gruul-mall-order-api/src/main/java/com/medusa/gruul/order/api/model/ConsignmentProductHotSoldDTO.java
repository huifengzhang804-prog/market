package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>代销商品热销DTO</p>
 *
 * @author Andy.Yan
 */
@Getter
@Setter
public class ConsignmentProductHotSoldDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 7760014427454487603L;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 实付金额
     */
    private Long dealPrice;

}
