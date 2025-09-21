package com.medusa.gruul.cart.service.model.vo;

import com.medusa.gruul.storage.api.enums.LimitType;
import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 *     商品限购与库存
 * </p>
 *
 * @author 张治保
 * date 2022/6/28
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SkuStockVO {
    /**
     * 限购类型
     */
    private LimitType limitType;
    /**
     * 限购数量
     */
    private Integer limitNum;
    /**
     * 库存类型
     */
    private StockType stockType;
    /**
     * 库存数量
     */
    private Long stock;
}
