package com.medusa.gruul.shop.service.model.vo;

import com.medusa.gruul.common.module.app.shop.ShopStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 店铺状态数量VO
 *
 * @author xiaoq
 * @Description ShopStatusQuantityVO.java
 * @date 2022-10-18 14:58
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ShopStatusQuantityVO {

    /**
     * 商品数量
     */
    private Long quantity;

    /**
     * 店铺状态
     */
    private ShopStatus status;
}
