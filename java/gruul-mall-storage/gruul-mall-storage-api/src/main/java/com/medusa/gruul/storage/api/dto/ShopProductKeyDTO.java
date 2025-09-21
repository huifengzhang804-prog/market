package com.medusa.gruul.storage.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 
 *
 * @author 张治保
 * date 2022/7/19
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopProductKeyDTO implements Serializable {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

}
