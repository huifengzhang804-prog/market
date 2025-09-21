package com.medusa.gruul.shop.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: WuDi
 * @date: 2022/9/2
 */
@Getter
@Setter
@ToString
public class ShopFollowVO {

    private Long shopId;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺logo
     */
    private String logo;

    private Integer numberFollowers;

    private Boolean newProducts;
}
