package com.medusa.gruul.goods.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/6/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductPriceVO {

    /**
     * 划线价
     */
    private Long underlined;

    /**
     * 原始 sku 的售价（非活动价）
     */
    private Long skuPrice;

    /**
     * 预估价
     */
    private Long estimate;

    /**
     * 价格计算逻辑 渲染后中间使用减号
     */
    private List<ProductPriceItemVO> items;


}
