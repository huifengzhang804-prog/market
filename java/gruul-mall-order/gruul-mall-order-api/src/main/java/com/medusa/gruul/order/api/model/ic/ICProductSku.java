package com.medusa.gruul.order.api.model.ic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ICProductSku implements Serializable {

    /**
     * 商品名称
     */
    private String productName;

    /**
     * sku 规格
     */
    private List<String> specs;

    /**
     * sku 商品数量
     */
    private Integer num;
}
