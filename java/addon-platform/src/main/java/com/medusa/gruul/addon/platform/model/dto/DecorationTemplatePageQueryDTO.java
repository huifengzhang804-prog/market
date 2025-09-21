package com.medusa.gruul.addon.platform.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>装修模板分页查询DTO</p>
 *
 * @author Andy.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DecorationTemplatePageQueryDTO extends Page<Object> implements Serializable {

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
}
