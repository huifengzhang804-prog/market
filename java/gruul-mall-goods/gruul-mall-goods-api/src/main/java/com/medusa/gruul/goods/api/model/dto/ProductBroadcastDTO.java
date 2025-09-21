package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/12/1
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductBroadcastDTO implements Serializable {

    /**
     * 商品信息
     */
    private Product product;

    /**
     * 店铺分类
     */
    private CategoryLevel shopCategory;

    /**
     * 平台分类
     */
    private CategoryLevel platformCategory;

}
