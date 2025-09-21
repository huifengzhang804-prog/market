package com.medusa.gruul.addon.integral.model.dto;

import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


/**
 * @author miskw
 * @date 2023/7/17
 * @describe 修改商品信息DTO
 */
@Getter
@Setter
public class IntegralProductFixDTO implements BaseDTO {
    /**
     * 积分商品id
     */
    @NotNull(message = "积分商品id不能为空")
    private Long id;
    /**
     * 积分商品名称
     */
    private String name;
    /**
     * 积分商品库存
     */
    private Integer stock;
    /**
     * 积分价
     */
    @Min(value = 0)
    private Long integralPrice;
    /**
     * 销售价
     */
    @Min(value = 0)
    private Long salePrice;


}
