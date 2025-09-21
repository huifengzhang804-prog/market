package com.medusa.gruul.shop.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 店铺装修模板删除DTO
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ShopDecorationTemplateDeleteDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 668200827997683569L;

    @NotNull
    private Long templateId;
}
