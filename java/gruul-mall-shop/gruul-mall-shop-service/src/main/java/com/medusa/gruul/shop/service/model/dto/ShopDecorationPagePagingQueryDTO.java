package com.medusa.gruul.shop.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 装修页面分页查询dto
 *
 * @author An.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ShopDecorationPagePagingQueryDTO extends Page<Object> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3762448023648263959L;

    /**
     * 页面对应的业务类型
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    private DecorationEndpointTypeEnum endpointType;

    /**
     * 页面类型枚举
     */
    private PageTypeEnum type;

    /**
     * 自定义类型
     */
    private String customType;

}
