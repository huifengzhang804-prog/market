package com.medusa.gruul.common.custom.aggregation.decoration.dto;

import com.medusa.gruul.common.custom.aggregation.decoration.enums.AggregationPlatform;
import com.medusa.gruul.common.custom.aggregation.decoration.enums.FunctionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author xiaoq
 * @description FunctionDecorationDTO.java
 * @date 2022-11-02 09:39
 */
@Getter
@Setter
@ToString
public class FunctionDecorationDTO {


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
     * APP平台类型
     */
    private AggregationPlatform platforms;
}
