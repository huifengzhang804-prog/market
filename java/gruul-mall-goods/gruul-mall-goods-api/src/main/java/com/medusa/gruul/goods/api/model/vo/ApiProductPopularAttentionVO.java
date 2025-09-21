package com.medusa.gruul.goods.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 热门关注
 * @author WuDi
 * date: 2022/11/7
 */
@Data
@Accessors(chain = true)
public class ApiProductPopularAttentionVO {


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
     * 已评价人数
     */
    private Long evaluated;

    /**
     * 收藏量
     */
    private Long productCollection;

    /**
     * 商品价格
     */
    private Long productPrice;
}
