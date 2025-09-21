package com.medusa.gruul.addon.matching.treasure.model.vo;


import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealType;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.enums.DistributionMode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 套餐商品详情VO
 */
@Getter
@Setter
@Accessors(chain = true)
public class SetMealDetailVO {

    /**
     * 套餐id
     */
    private Long setMealId;

    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * 套餐名称
     */
    private String setMealName;


    /**
     * 套餐主图
     */
    private String setMealMainPicture;

    /**
     * 套餐描述
     */
    private String setMealDescription;


    /**
     * 套餐类型 [0:自选商品套餐 1:固定组合套餐]
     */
    private SetMealType setMealType;

    /**
     * 最少可省
     */
    private Long saveAtLeast;


    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 套餐关联商品
     */
    private List<SetMealProductDetailVO> setMealProductDetails;


    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    private StackableDiscount stackable;



    /**
     * 套餐配送类型
     */
    private DistributionMode distributionMode;

}
