package com.medusa.gruul.addon.team.model.dto;

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
public class TeamProductDTO {

    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * 商品sku
     */
    @NotNull
    @Size(min = 1)
    private List<TeamSkuDTO> skus;

}
