package com.medusa.gruul.addon.supplier.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PublishProductGetDTO implements Serializable {

    /**
     * 店铺 id
     */
    @Null
    private Long shopId;

    /**
     * 供应商 id
     */
    @NotNull
    private Long supplierId;

    /**
     * 商品 id
     */
    @NotNull
    private Long productId;
}
