package com.medusa.gruul.user.api.model;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 猜你喜欢
 * @author WuDi
 */
@Data
@Accessors(chain = true)
public class GuessYouLikeVO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;


    /**
     * 展示图片
     */
    private String productAlbumPics;

    /**
     * 商品名称
     */
    private String productName;


    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 最低价
     */
    private Long lowestPrice;

    /**
     * 已评价人数
     */
    private Long evaluated;
}
