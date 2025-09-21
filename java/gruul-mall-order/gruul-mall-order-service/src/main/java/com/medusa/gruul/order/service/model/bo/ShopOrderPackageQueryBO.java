package com.medusa.gruul.order.service.model.bo;

import com.medusa.gruul.common.model.enums.SellType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2022/8/11
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopOrderPackageQueryBO {

    /**
     * 店铺订单号
     */
    private String shopOrderNo;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 用户
     */
    private Long buyerId;

    /**
     * 是否只取一条
     */
    private Boolean limitOne;

    /**
     * 包裹id
     */
    private Long packageId;

    /**
     * 商品销售类型
     */
    private SellType sellType;

    /**
     * 供应商 id
     */
    private Long supplierId;
}
