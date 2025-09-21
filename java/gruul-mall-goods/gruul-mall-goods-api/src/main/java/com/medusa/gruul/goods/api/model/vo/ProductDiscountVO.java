package com.medusa.gruul.goods.api.model.vo;

import cn.hutool.json.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商品详情折扣信息
 *
 * @author wufuzhong
 * @since 2024/2/28
 */

@Data
@Accessors(chain = true)
public class ProductDiscountVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前 优惠金额
     */
    private Long discount;

    /**
     * 优惠券列表
     */
    private JSON data;
}
