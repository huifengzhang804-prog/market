package com.medusa.gruul.addon.store.model.vo;

import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 店铺门店订单vo
 *
 * @author xiaoq
 * @Description 店铺门店 订单信息VO
 * @date 2023-03-24 14:15
 */
@Data
@Accessors(chain = true)
public class ShopStoreOrderInfoVO {

    /**
     * 核销码
     */
    private String code;

    private String getPickUpTime;


    private ShopStore shopStore;
}
