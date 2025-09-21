package com.medusa.gruul.addon.platform.model.vo;

import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.bo.PageReference;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>平台装修模板查询VO</p>
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class DecorationTemplateVO {

    /**
     * 模板ID
     */
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模版说明
     */
    private String description;

    /**
     * 模板类型
     */
    private TemplateTypeEnum templateType;

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
     * 组成模板的页面引用
     */
    private List<PageReference> pages;
}
