package com.medusa.gruul.shop.service.model.dto;

import com.medusa.gruul.shop.api.model.bo.PageReference;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 店铺装修模板修改DTO
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ShopDecorationTemplateModifyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1971726463361781928L;

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
     * 组成模版的页面
     */
    private List<PageReference> pages;
}
