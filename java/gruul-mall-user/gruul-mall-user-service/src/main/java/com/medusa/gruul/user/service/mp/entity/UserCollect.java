package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * 用户收藏
 * 
 *
 * @author WuDi
 * @since 2022-08-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_user_collect")
public class UserCollect extends BaseEntity {

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
     * 商品id
     */
    private Long productId;

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
     * 供应商id
     */
    private Long supplierId;

    /**
     * 商品收藏量
     */
    @TableField(exist = false)
    private Long productCollect;

}
