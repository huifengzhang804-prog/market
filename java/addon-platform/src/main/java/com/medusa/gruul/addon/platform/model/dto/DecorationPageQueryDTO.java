package com.medusa.gruul.addon.platform.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.platform.model.enums.TemplateTypeEnum;
import com.medusa.gruul.addon.platform.mp.entity.DecorationPage;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>装修页面modify dto</p>
 *
 * @author An.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DecorationPageQueryDTO extends Page<Object> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3762448023648263959L;

    /**
     * 页面名称
     */
    private String name;

    /**
     * 页面类型枚举
     */
    private PageTypeEnum type;

    /**
     * 页面所属的模版的类型
     */
    @NotNull
    private TemplateTypeEnum templateType;

    /**
     * 页面对应的业务类型,当{@link DecorationPage#templateType}等于{@link TemplateTypeEnum#PLATFORM}时, 该字段为NULL
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    @NotNull
    private DecorationEndpointTypeEnum endpointType;

    /**
     * 自定义类型
     */
    private String customType;
}
