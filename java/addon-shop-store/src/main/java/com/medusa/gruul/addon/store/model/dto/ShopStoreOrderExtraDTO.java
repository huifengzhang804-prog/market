package com.medusa.gruul.addon.store.model.dto;

import com.medusa.gruul.common.model.enums.DistributionMode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 店铺门店订单额外DTO
 *
 * @author xiaoq
 * @Description 店铺门店订单额外DTO
 * @date 2023-03-20 17:40
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopStoreOrderExtraDTO {

    /**
     * 店铺门店id
     */
    private Long shopStoreId;

    /**
     * 提货时间
     */
    private String packUpTime;

    /**
     * 发货形式
     **/
    private DistributionMode distributionMode;
}
