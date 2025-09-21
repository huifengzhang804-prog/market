package com.medusa.gruul.order.api.helper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2023/9/14
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ItemDiscount implements Serializable {

    /**
     * 商品项id
     */
    private Long itemId;

    /**
     * 当前 item 对应的 商品折扣金额
     */
    private Long discountAmount;

    /**
     * 商品项数量
     */
    private Integer num;

    /**
     * 商品最终成交价
     */
    private Long dealPrice;

    /**
     * 商品修正价
     */
    private Long fixPrice;

    public static ItemDiscount init(Long itemId, Integer num) {
        return new ItemDiscount()
                .setItemId(itemId)
                .setDiscountAmount(0L)
                .setNum(num)
                .setDealPrice(0L)
                .setFixPrice(0L);

    }

    public ItemDiscount set(long discountIncr, long dealPrice, long fixPrice) {
        this.discountAmount += discountIncr;
        this.dealPrice = dealPrice;
        this.fixPrice = fixPrice;
        return this;
    }

}
