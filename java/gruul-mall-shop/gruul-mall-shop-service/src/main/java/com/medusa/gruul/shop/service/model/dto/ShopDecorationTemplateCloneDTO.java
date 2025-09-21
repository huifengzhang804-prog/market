package com.medusa.gruul.shop.service.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ShopDecorationTemplateCloneDTO {

    /**
     * 模板ID
     */
    private Long templateId;
}
