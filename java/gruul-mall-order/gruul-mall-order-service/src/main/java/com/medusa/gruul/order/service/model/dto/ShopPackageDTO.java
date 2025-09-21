package com.medusa.gruul.order.service.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * 店铺商品列表
 *
 * @author 张治保
 * date 2022/6/1
 */
@Getter
@Setter
@ToString
public class ShopPackageDTO {

    /**
     * 店铺id
     */
    @NotNull
    private Long id;

    /**
     * 店铺名称
     */
    @NotBlank
    private String name;

    /**
     * 店铺logo
     */
    @NotBlank
    private String logo;

    /**
     * 备注
     */
    @NotNull
    @Valid
    private Map<String, String> remark;

    /**
     * 商品列表
     */
    @NotNull
    @Valid
    @Size(min = 1)
    private List<ProductDTO> products;
}
