package com.medusa.gruul.shop.service.model.vo;

import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 店铺装修页面详情VO
 *
 * @author An.Yan
 */
@Data
@Accessors(chain = true)
public class ShopDecorationPageDetailsVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4974842787742049835L;

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
     * 店铺ID
     */
    private Long shopId;

    /**
     * 页面类型枚举
     */
    private PageTypeEnum type;


    /**
     * 页面对应的业务类型
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    private DecorationEndpointTypeEnum endpointType;
}
