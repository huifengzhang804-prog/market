package com.medusa.gruul.goods.service.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 商品标签 dto
 *
 * @author wufuzhong
 * @Date 2023/12/02 11:28:00
 */
@Getter
@Setter
@ToString
public class ProductLabelDTO {


    private Long shopId;

    /**
     * 标签名称
     */
    @NotNull
    @Size(max = 5)
    private String name;

    /**
     * 字体颜色
     */
    @NotNull
    @Size(max = 7)
    private String fontColor;

    /**
     * 背景颜色
     */
    @NotNull
    @Size(max = 7)
    private String backgroundColor;

    /**
     * 店铺类型
     */
    @NotNull
    private List<ShopType> shopType;

    public ProductLabel coverProductLabel() {
        ProductLabel productLabel = new ProductLabel();
        BeanUtil.copyProperties(this, productLabel);
        return productLabel;
    }
}
