package com.medusa.gruul.addon.supplier.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2023/7/27
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode(of = "skuId")
public class OrderStorageProductSkuVO implements Serializable {

    /**
     * sku key
     */
    private Long skuId;

    /**
     * 规格
     */
    private List<String> specs;

    /**
     * 采购数
     */
    private Integer num;

    /**
     * 入库数
     */
    private Integer used;

    /**
     * 商品名称
     */
    private String productName;
}
