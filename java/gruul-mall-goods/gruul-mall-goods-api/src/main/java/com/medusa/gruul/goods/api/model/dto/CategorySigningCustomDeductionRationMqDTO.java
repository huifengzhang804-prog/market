package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.common.module.app.shop.ShopMode;
import lombok.Data;

/**
 * 商品分类签约自定义折扣比例
 *
 * @author jipeng
 * @since 2024/2/27
 */
@Data
public class CategorySigningCustomDeductionRationMqDTO {

    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 店铺模式
     */
    private ShopMode shopMode;
    /**
     * 二级类目id
     */
    private Long secondCategoryId;
    /**
     * 扣率 0-100
     */
    private Long deductionRation;

}
