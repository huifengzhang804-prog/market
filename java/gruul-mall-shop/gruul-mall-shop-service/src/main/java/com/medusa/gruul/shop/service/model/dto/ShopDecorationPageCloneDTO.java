package com.medusa.gruul.shop.service.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 店铺装修页面Clone DTO
 *
 * @author Andy.Yan
 */
@Data
@Accessors(chain = true)
public class ShopDecorationPageCloneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5976145358758172718L;

    /**
     * 页面ID
     */
    @NotNull
    private Long id;
}
