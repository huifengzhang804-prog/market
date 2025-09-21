package com.medusa.gruul.goods.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.goods.api.entity.Product;
import lombok.Data;

/**
 * @author WuDi
 * date 2022/9/30
 */
@Data
public class ProductRandomParam extends Page<Product> {

    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 排除的商品ID
     */
    private Long excludeProductId;
}
