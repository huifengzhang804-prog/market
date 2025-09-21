package com.medusa.gruul.addon.matching.treasure.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 商品详情套餐基本信息
 */
@Getter
@Setter
@Accessors(chain = true)
public class SetMealBasicInfoVO {

    /**
     * 套餐id
     */
    private Long setMealId;


    /**
     * 套餐主图
     */
    private String setMealMainPicture;

    /**
     * 套餐名称
     */
    private String setMealName;

    /**
     * 结束时间
     */
    private LocalDate endTime;

    /**
     * 最少可省
     */
    private Long saveAtLeast;
}
