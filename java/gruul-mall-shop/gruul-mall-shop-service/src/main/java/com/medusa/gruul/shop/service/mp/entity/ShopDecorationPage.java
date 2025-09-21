package com.medusa.gruul.shop.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import com.medusa.gruul.shop.api.enums.PageTypeEnum;
import com.medusa.gruul.shop.api.enums.TemplateBusinessTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 店铺装修页面
 *
 * @author Andy.Yan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_shop_decoration_page", autoResultMap = true)
public class ShopDecorationPage extends BaseEntity {

    /**
     * 店铺ID
     */
    private Long shopId;

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
     * 页面对应的业务类型
     */
    private TemplateBusinessTypeEnum businessType;

    /**
     * 页面终端类型
     */
    private DecorationEndpointTypeEnum endpointType;

}
