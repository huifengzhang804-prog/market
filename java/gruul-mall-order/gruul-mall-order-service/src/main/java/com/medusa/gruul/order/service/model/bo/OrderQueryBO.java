package com.medusa.gruul.order.service.model.bo;

import com.medusa.gruul.common.model.enums.SellType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/6/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderQueryBO {
    /**
     * 订单订单号
     */
    private String orderNo;
    /**
     * 买家id
     * 限制买家id
     */
    private Long buyerId;
    /**
     * 指定店铺的商品
     */
    private Long shopOrderShopId;

    /**
     * 商品销售方式
     */
    private SellType sellType;
    /**
     * 指定供应商的商品
     */
    private Long shopOrderSupplierId;

    /**
     * 是否根据包裹查询数据
     */
    private Boolean usePackage;

    /**
     * 包裹id
     */
    private Long packageId;
}
