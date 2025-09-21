package com.medusa.gruul.goods.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author miskw
 * @date 2023/8/9
 * @describe 代销商品记录
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SupplierGoodsPublishDTO implements Serializable {
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 供应商id
     */
    private Long supplierId;
    /**
     * 商品id
     */
    private Long productId;

}
