package com.medusa.gruul.order.service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ShopPackageConfirmDTO {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;


    /**
     * 商品列表
     */
    @Valid
    @NotNull
    @Size(min = 1)
    private List<ProductDTO> products;

}
