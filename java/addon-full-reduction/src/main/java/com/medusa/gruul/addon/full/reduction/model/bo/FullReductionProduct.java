package com.medusa.gruul.addon.full.reduction.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FullReductionProduct {

    /**
     * 商品id
     */
    Long id;

    /**
     * 商品名称
     */
    String name;

    /**
     * 商品图片
     */
    String image;
}