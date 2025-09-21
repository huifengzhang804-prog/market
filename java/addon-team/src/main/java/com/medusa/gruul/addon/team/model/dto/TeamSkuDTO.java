package com.medusa.gruul.addon.team.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 团购活动商品dto
 *
 * @author 张治保
 * date 2023/3/2
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TeamSkuDTO {
    /**
     * 商品sku id
     */
    @NotNull
    private Long skuId;

    /**
     * sku库存
     */
    @NotNull
    @Min(1)
    private Long stock;

    /**
     * 活动价格 普通团长度为1 阶梯团长度为1-3 和拼团人数阶梯下标 一一对应
     * 且递减
     */
    @NotNull
    @Size(min = 1, max = 3)
    private List<Long> prices;

}
