package com.medusa.gruul.addon.supplier.model.vo;

import com.medusa.gruul.goods.api.entity.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PublishProductGetVO implements Serializable {

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 商品 id
     */
    private Long productId;

    /**
     * 商品信息
     */
    private Product product;

}
