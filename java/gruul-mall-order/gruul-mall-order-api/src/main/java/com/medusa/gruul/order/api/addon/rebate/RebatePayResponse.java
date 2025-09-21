package com.medusa.gruul.order.api.addon.rebate;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 张治保
 * date 2023/9/13
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RebatePayResponse implements Serializable {

    /**
     * 返利支付生成的折扣
     */
    private OrderDiscount rebateDiscount;

    /**
     * 调整后的item对应的实际支付金额
     */
    private Map<ShopProductSkuKey, RebateItemPrice> itemKeyDealPriceMap;
}
