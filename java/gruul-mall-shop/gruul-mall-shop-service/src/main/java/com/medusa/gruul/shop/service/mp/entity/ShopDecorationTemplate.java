package com.medusa.gruul.shop.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.bo.PageReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 店铺装修模板
 *
 * @author Andy.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_shop_decoration_template", autoResultMap = true)
public class ShopDecorationTemplate extends BaseEntity {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模版说明
     */
    private String description;

    /**
     * 模板业务类型
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 模板终端类型
     */
    private DecorationEndpointTypeEnum endpointType;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 组成模版的页面
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<PageReference> pages;
    

}
