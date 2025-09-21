package com.medusa.gruul.addon.platform.model.vo;

import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.addon.platform.mp.entity.DecorationPage;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>平台装修页面查询VO</p>
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class DecorationPageVO {

    /**
     * 页面ID
     */
    private Long id;

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
     * 页面所属的模版的类型
     */
    private TemplateTypeEnum templateType;

    /**
     * 页面对应的业务类型,当{@link DecorationPage#templateType}等于{@link TemplateTypeEnum#PLATFORM}时,
     * 该字段为NULL
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    private DecorationEndpointTypeEnum endpointType;
}
