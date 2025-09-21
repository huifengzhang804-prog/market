package com.medusa.gruul.user.service.model.dto;


import com.medusa.gruul.user.api.enums.ConsumeGrowthValueType;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wudi
 *
 * 消费成长值DTO
 */
@Getter
@Setter
@ToString
public class ConsumeJsonDTO {


    /**
     * id
     */
    private Long id;

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
    @Min(value = 1, message = "订单数量/金额不能小于1")
    private Long orderQuantityAndAmount;


    /**
     * 赠送成长值
     */
    private Long presentedGrowthValue;



}
