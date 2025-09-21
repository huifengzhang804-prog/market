package com.medusa.gruul.storage.api.bo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 库存和销量更新
 *
 * @author 张治保
 * date 2023/3/8
 */
@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StSvBo implements Serializable {

    /**
     * 库存 减库存传负数 增库存传正数
     */
    private long stock = 0;

    /**
     * 销量 减销量传负数 增销量传正数
     */
    private long sales = 0;


    public final StSvBo incrementStock(long stock) {
        this.stock += stock;
        return this;
    }

    public final StSvBo incrementSales(long sales) {
        this.sales += sales;
        return this;
    }

    public final StSvBo merge(StSvBo other) {
        return this.incrementStock(other.getStock())
                .incrementSales(other.getSales());
    }

    public final boolean invalid() {
        return stock == 0 && sales == 0;
    }
}
