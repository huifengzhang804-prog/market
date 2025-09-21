package com.medusa.gruul.storage.api.dto.activity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/3/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ActivitySkuDTO implements Serializable {

    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * 商品skuId
     */
    @NotNull
    private Long skuId;

    /**
     * 使用的库存
     */
    @NotNull
    @Min(1)
    private Integer stock;

    /**
     * 售价 已这个值为准，不传已原有售价为标准
     */
    private Long salePrice;

}
