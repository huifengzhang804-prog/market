package com.medusa.gruul.common.custom.aggregation.decoration.entity;

import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.custom.aggregation.decoration.enums.FunctionType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 * @author xiaoq
 * @description 装修详情通用信息
 * @date 2022-10-31 19:11
 */
@Getter
@Setter
@Accessors(chain = true)
public class DecorateDetailsEntity extends BaseEntity {



    /**
     * 装修功能类型
     */
    private FunctionType functionType;


    /**
     * 是否是首页
     */
    private Boolean isDef;

    /**
     * 页面名称
     */
    private String pageName;


    /**
     * 装修数据
     */
    private String properties;
}
