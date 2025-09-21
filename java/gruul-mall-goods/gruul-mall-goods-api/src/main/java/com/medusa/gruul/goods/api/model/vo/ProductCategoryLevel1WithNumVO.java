package com.medusa.gruul.goods.api.model.vo;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 一级分类和分类下的商品数量
 * @author: WuDi
 * @date: 2022/9/23
 */
@Data
@Accessors(chain = true)
public class ProductCategoryLevel1WithNumVO {

    /**
     * 一级分类id
     */
    private Long id;

    /**
     * 一级分类名称
     */
    private String name;

    /**
     * 商品数量
     */
    private Long productNum;


}
