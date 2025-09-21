package com.medusa.gruul.goods.api.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品详情套餐基本信息
 */
@Getter
@Setter
@Accessors(chain = true)
public class SetMealBasicInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private LocalDateTime endTime;

    /**
     * 最少可省
     */
    private Long saveAtLeast;

    /**
     * 套餐描述
     */
    private String setMealDescription;


}
