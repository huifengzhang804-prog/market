package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * @author miskw
 * @date 2023/8/9
 * @describe 批量一键铺货DTO
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PaveGoodsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺类目
     */
    private CategoryLevel shopCategory;
    /**
     * 供应商id与商品id
     */
    @Valid
    @Size(min = 1)
    @NotNull(message = "商品信息不能为空")
    private Set<ShopProductKey> shopProductKeys;

    /**
     * 价格设置
     */
    @Valid
    @NotNull(message = "价格设置不能为空")
    private ConsignmentPriceSettingDTO consignmentPriceSetting;


}
