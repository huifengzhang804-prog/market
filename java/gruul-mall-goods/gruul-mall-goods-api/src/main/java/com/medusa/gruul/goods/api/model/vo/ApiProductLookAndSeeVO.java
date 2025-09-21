package com.medusa.gruul.goods.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品详情-看了又看
 *
 * @author WuDi
 * date: 2022/11/1
 */
@Data
@Accessors(chain = true)
public class ApiProductLookAndSeeVO {


    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 展示图片
     */
    private String pic;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品价格
     */
    private Long productPrice;

    /**
     * 已评价人数
     */
    private Long evaluated;
}
