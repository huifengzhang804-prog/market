package com.medusa.gruul.shop.service.model.dto;

import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 店铺装修页面CreateDTO
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ShopDecorationPageModifyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6584993515522146115L;

    /**
     * 页面ID
     */
    @NotNull
    private Long id;

    /**
     * 页面名称
     */
    @NotNull
    private String name;

    /**
     * 页面说明
     */
    private String remark;

    /**
     * 页面json
     */
    @NotNull
    private String properties;

    /**
     * 页面类型枚举
     */
    @NotNull
    private PageTypeEnum type;

    /**
     * 页面对应的业务类型
     */
    @NotNull
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    @NotNull
    private DecorationEndpointTypeEnum endpointType;

}
