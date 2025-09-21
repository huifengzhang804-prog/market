package com.medusa.gruul.shop.service.model.vo;

import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import com.medusa.gruul.shop.api.model.bo.PageReference;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 店铺装修模板详情VO
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ShopDecorationTemplateDetailsVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -655546455329101797L;

    /**
     * ID
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
     * 店铺ID
     */
    private Long shopId;

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
    private List<PageReference> pages;
}
