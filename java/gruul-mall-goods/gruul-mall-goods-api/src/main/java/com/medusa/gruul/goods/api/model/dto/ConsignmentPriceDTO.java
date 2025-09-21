package com.medusa.gruul.goods.api.model.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 代销计算后的价格
 *
 * @author 张治保
 * @since 2024/05/15
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConsignmentPriceDTO implements Serializable {
    /**
     * 销售价
     */
    private Long salePrice;

    /**
     * 原价 （划线价）
     */
    private Long price;


}