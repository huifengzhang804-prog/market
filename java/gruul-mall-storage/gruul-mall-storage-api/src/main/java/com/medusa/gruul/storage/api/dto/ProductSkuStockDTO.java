package com.medusa.gruul.storage.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2022/6/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSkuStockDTO {
    /**
     * sku id
     */
    @NotNull
    private Long skuId;
    /**
     * 调整的数量
     */
    @NotNull
    private Long num;


}
