package com.medusa.gruul.goods.api.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 浏览商品DTO
 *
 * @author: WuDi
 * @date: 2022/9/14
 */
@Data
public class ShopFollowDTO {

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 店铺logo
     */
    private String shopLogo;
    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;
    /**
     * 是否关注
     */
    @NotNull
    private Boolean isFollow;
}
