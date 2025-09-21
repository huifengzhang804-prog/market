package com.medusa.gruul.addon.ic.modules.ic.model.vo;

import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICCourier;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICErrorHandler;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICOrderTimes;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/9/2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopOrderInfo {

    /**
     * 配送单状态
     */
    private ICShopOrderStatus status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 配送方类型
     */
    private ICDeliveryType type;

    /**
     * 各状态时间节点
     */
    private ICOrderTimes times;

    /**
     * 配送单备注
     */
    private String remark;

    /**
     * 配送员
     */
    private ICCourier courier;

    /**
     * 异常状态处理
     */
    private ICErrorHandler errorHandler;

    /**
     * 取餐码
     */
    private String pickupCode;

    /**
     * 配送时长
     */
    private Long deliverSeconds;


}
