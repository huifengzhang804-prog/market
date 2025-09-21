package com.medusa.gruul.goods.service.model.dto;

import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 代销商品修改DTO
 *
 * @author xiaoq
 * @Description ConsignmentProductDTO.java
 * @date 2023-08-16 17:45
 */
@Getter
@Setter
@ToString
public class ConsignmentProductDTO {

    @NotNull
    private Long id;

    /**
     * 店铺类目
     */
    @NotNull
    @Valid
    private CategoryLevel shopCategory;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不可为空")
    private String name;


    /**
     * 卖点描述
     */
    private String saleDescribe;

    /**
     * 商品标签ID
     */
    private Long labelId;

    /**
     * 价格设置
     */
    @NotNull
    private ConsignmentPriceSettingDTO consignmentPriceSetting;

}
