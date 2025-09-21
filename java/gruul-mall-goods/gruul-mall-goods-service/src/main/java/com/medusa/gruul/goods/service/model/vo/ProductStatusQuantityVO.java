package com.medusa.gruul.goods.service.model.vo;

import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 商品数量VO
 *
 * @author xiaoq
 * @Description ProductQuantityVO.java
 * @date 2022-10-17 19:14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
public class ProductStatusQuantityVO {

    /**
     * 商品数量
     */
    private Long quantity;


    /**
     * 商品状态
     */
    private ProductStatus status;
}
