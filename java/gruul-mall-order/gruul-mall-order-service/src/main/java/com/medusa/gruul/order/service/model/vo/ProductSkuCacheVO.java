package com.medusa.gruul.order.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 *
 * @author 张治保
 * date 2022/6/9
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSkuCacheVO {
    /**
     * 商品主图
     */
    private String productImage;
    /**
     * 商品名称
     */
    private String productName;

    /**
     * sku 规格信息
     */
    private List<String> specs;

    /**
     * sku图片
     */
    private String skuImage;

    /**
     * 销售价
     */
    private BigDecimal salePrice;

    /**
     * 重量
     */
    private BigDecimal  weight;


}
