package com.medusa.gruul.addon.full.reduction.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/6/17
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MaxFullReduction implements Serializable {

    /**
     * 满减活动id
     */
    private Long activityId;


    /**
     * 满减折扣金额
     */
    private Long discountAmount = 0L;

    /**
     * 满减活动描述
     */
    private String desc;

    /**
     * 作用于的商品 商品 id
     */
    private Set<Long> productIds;

    /**
     * 满减作用的商品总额
     */
    private Long totalAmount;

}
