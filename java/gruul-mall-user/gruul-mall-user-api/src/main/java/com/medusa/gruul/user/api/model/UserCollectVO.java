package com.medusa.gruul.user.api.model;

import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author: WuDi
 * @date: 2022/9/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserCollectVO {

    /**
     * 商品id
     */
    private Long productId;

    /**
     * shopId
     */
    private Long shopId;

    /**
     * 展示图片
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private Long productPrice;

    /**
     * 会员优惠价
     */
    private Long memberDiscount;

    /**
     * 活动价
     */
    private Long activityPrice;

    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 状态
     */
    private ProductStatus status;

    /**
     * 评论人数
     */
    private Long evaluatePerson;

}
