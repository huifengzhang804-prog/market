package com.medusa.gruul.storage.service.model.bo;

import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 库存和销量 更新 的订单
 *
 * @author 张治保
 * @since 2024/6/7
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UpdateStockOrder implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 是否生成库存详情
     */
    private boolean generateDetail;

    /**
     * 库存变更类型
     */
    private StockChangeType changeType;

    /**
     * sku key与库存销量映射 map
     */
    private Map<ActivityShopProductSkuKey, StSvBo> stSvMap;

}
