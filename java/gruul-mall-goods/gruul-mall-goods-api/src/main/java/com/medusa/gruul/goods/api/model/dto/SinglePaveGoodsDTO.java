package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author miskw
 * @date 2023/8/15
 * @describe 描述
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SinglePaveGoodsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 店铺类目
     */
    @Valid
    @NotNull(message = "店铺分类不能为空")
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
     * 供应商id与商品id
     */
    @Valid
    @NotNull(message = "商品信息不能为空")
    private ShopProductKey shopProductKey;

    /**
     * 价格设置
     */
    @Valid
    @NotNull(message = "价格设置不能为空")
    private ConsignmentPriceSettingDTO consignmentPriceSetting;
}
