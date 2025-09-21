package com.medusa.gruul.order.service.model.bo;

import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.shop.api.enums.ShopDeliverModeSettingsEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 发货查询bo
 *
 * @author 张治保
 * date 2023/8/22
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class DeliveryQueryBO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺订单号
     */
    private String shopOrderNo;

    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * 商品销售类型
     */
    private SellType sellType;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 订单平台
     */
    private Platform platform;

    /**
     * 自营店铺发货方式
     */
    private ShopDeliverModeSettingsEnum shopDeliverModeSettings;


    /**
     * 获取发货店铺id 有供应商 id 代表代销商品返回供应商 id  否则是普通商品 返回店铺 id
     *
     * @return 发货发店铺id
     */
    public Long deliverShopId() {
        return supplierId == null ? shopId : supplierId;
    }

}