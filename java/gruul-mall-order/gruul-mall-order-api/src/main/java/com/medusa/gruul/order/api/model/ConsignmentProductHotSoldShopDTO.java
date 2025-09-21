package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>代销商品热销店铺DTO</p>
 *
 * @author Andy.Yan
 */
@Getter
@Setter
public class ConsignmentProductHotSoldShopDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 7760014427454487603L;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 营业额
     */
    private Long businessVolume;

}
