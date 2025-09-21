package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 * 浏览的上新商品
 * @author WuDi
 * @since 2022-09-02
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("t_shop_follow_new_products")
public class ShopFollowNewProducts extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商品id
     */
    private Long productId;


}
