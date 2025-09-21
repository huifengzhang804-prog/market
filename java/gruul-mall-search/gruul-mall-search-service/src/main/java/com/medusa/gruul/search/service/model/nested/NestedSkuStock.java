package com.medusa.gruul.search.service.model.nested;

import com.medusa.gruul.storage.api.enums.StockType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2022/12/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class NestedSkuStock implements Serializable {

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 规格
     */
    private List<String> specs;

    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 售价
     */
    private Long salePrice;

    /**
     * 库存类型
     */
    private StockType stockType;

    /**
     * 库存数
     */
    private Long stock;
}
