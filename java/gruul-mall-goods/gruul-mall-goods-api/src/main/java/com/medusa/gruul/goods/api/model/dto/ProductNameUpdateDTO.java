package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.common.model.base.ShopProductKey;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/4/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductNameUpdateDTO implements Serializable {
    /**
     * 商品 key
     */
    private ShopProductKey key;

    /**
     * 商品名称
     */
    private String name;
}
