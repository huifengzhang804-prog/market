package com.medusa.gruul.search.service.es.entity;


import com.medusa.gruul.common.module.app.shop.ShopType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * ES 商品标签
 *
 * @author wufuzhong
 * @since 2023/12/07
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class EsProductLabel {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 字体颜色
     */
    private String fontColor;

    /**
     * 背景颜色
     */
    private String backgroundColor;

    /**
     * 商家类型
     */
    private List<ShopType> shopType;

}
