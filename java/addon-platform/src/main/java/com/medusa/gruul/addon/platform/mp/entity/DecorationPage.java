package com.medusa.gruul.addon.platform.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>装修页面表</p>
 *
 * @author Andy.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_decoration_page")
public class DecorationPage extends BaseEntity {

    /**
     * 页面名称
     */
    private String name;

    /**
     * 页面说明
     */
    private String remark;

    /**
     * 页面json
     */
    private String properties;

    /**
     * 页面类型枚举
     */
    private PageTypeEnum type;

    /**
     * 自定义类型，自定义页面时使用
     */
    private String customType;

    /**
     * 页面所属的模版的类型
     */
    private TemplateTypeEnum templateType;

    /**
     * 页面对应的业务类型,当{@link DecorationPage#templateType}等于{@link TemplateTypeEnum#PLATFORM}时, 该字段为NULL
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    private DecorationEndpointTypeEnum endpointType;

}
