package com.medusa.gruul.shop.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 装修页面modify dto
 *
 * @author An.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ShopDecorationPageQueryDTO extends Page<Object> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3762448023648263959L;

    /**
     * 模板业务类型
     */
    @NotNull
    private TemplateBusinessTypeEnum businessType;

    /**
     * 模板终端类型
     */
    @NotNull
    private DecorationEndpointTypeEnum endpointType;
}
