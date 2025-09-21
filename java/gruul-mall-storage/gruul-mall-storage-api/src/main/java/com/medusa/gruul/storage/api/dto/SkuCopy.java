package com.medusa.gruul.storage.api.dto;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * sku copy dto
 *
 * @author 张治保
 * @since 2023/7/27
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "sourceKey")
@Accessors(chain = true)
public class SkuCopy implements Serializable {
    /**
     * sku key
     */
    private ShopProductSkuKey sourceKey;

    /**
     * 目标 sku 库存数量
     */
    private Integer num;

    /**
     * 商品名称
     */
    private String productName;

}
