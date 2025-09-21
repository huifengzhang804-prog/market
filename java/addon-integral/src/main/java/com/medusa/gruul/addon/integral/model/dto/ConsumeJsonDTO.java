package com.medusa.gruul.addon.integral.model.dto;


import com.medusa.gruul.user.api.enums.ConsumeGrowthValueType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumeJsonDTO {

    /**
     * 是否选中
     */
    private Boolean isSelected;


    /**
     * 消费成长值类型
     */
    private ConsumeGrowthValueType consumeGrowthValueType;

    /**
     * 订单数量/金额
     */
    private Long orderQuantityAndAmount;


    /**
     * 赠送成长值
     */
    private Long presentedGrowthValue;



}
