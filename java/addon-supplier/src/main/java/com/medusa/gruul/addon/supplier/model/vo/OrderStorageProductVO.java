package com.medusa.gruul.addon.supplier.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/27
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "productId")
@Accessors(chain = true)
public class OrderStorageProductVO implements Serializable {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String image;

    /**
     * sku列表
     */
    private Set<OrderStorageProductSkuVO> skus;

}
