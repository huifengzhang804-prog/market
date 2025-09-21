package com.medusa.gruul.addon.integral.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 积分产品属性DTO
 *
 * @author xiaoq
 * @Description 积分产品属性DTO
 * @date 2023-02-02 13:18
 */
@Getter
@Setter
@ToString
public class IntegralProductAttribute {

    @Size(max = 10)
    private String attributeName;

    @Size(max = 20)
    private String attributeValue;

}
