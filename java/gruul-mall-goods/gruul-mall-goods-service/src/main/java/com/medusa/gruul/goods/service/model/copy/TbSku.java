package com.medusa.gruul.goods.service.model.copy;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author miskw
 * @date 2023/2/1
 */
@Data
public class TbSku {
    /**
     * 库存
     */
    private BigInteger quantity;
    /**
     * image
     */
    private String image;
    /**
     * 价钱
     */
    private BigDecimal price;
    /**
     * 名称
     * 颜色分类--驼色;尺码--S
     */
    private String skuName;

}
