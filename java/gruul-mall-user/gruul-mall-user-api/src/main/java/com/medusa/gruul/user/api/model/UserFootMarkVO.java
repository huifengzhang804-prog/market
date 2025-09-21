package com.medusa.gruul.user.api.model;


import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;


/**
 * 用户足迹
 * @author WuDi
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserFootMarkVO {

    /**
     * 日期
     */
    private LocalDate date;

    private Long id;
    /**
     * 商品id
     */
    private Long productId;

    /**
     *商品名称
     */
    private String productName;

    /**
     * 展示图片
     */
    private String productPic;

    /**
     * 商品价格
     */

    private Long productPrice;

    /**
     * 状态(默认上架，0--下架，1--上架)
     */
    private ProductStatus status;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 评论人数
     */
    private Long evaluatePerson;

    /**
     * 浏览数
     */
    private Long total;
}
