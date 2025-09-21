package com.medusa.gruul.storage.service.model.dto;

import com.medusa.gruul.storage.api.enums.LimitType;
import jakarta.validation.constraints.Min;
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
public class ProductSkuLimitDTO {
    /**
     * 商品sku id
     */
    @NotNull
    private Long skuId;

    /**
     * 限购类型
     */
    @NotNull
    private LimitType limitType;

    /**
     * 限购数量
     */
    @NotNull
    @Min(0)
    private Integer limitNum;
}
