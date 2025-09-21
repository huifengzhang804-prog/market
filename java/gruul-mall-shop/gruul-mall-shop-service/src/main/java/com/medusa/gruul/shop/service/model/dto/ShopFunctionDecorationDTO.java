package com.medusa.gruul.shop.service.model.dto;

import com.medusa.gruul.common.custom.aggregation.decoration.enums.AggregationPlatform;
import com.medusa.gruul.common.custom.aggregation.decoration.enums.FunctionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author xiaoq
 * @Description ShopFunctionDecorationDTO.java
 * @date 2022-08-17 14:58
 */
@Getter
@Setter
@ToString
public class ShopFunctionDecorationDTO {


    private Long id;

    /**
     * 功能详情
     */
    private FunctionType functionType;

    /**
     * 装修内容
     */
    private List<Object> properties;

    /**
     * 是否为首页
     */
    private Boolean isDef;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 聚合APP平台类型
     */
    private AggregationPlatform platforms;

}
