package com.medusa.gruul.addon.seckill.model.dto;

import com.medusa.gruul.common.web.valid.annotation.Price;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class SeckillActivitySkuDTO implements Serializable {

    /**
     * sku Id
     */
    @NotNull
    private Long id;

    /**
     * 活动库存
     */
    @NotNull
    @Min(1)
    private Long stock;

    /**
     * sku 规格
     */
    private String specs;

    /**
     * 活动价格
     */
    @NotNull
    @Price
    @Min(100)
    private Long price;
}
