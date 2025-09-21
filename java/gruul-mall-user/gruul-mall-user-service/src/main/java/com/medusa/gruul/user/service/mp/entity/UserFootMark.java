package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 用户足迹
 *
 * @author WuDi
 * @since 2022-07-29
 */
@Getter
@Setter
@TableName("t_user_foot_mark")
@Accessors(chain = true)
public class UserFootMark extends BaseEntity {

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
     * 浏览日期
     */
    private LocalDate browsDate;

    /**
     * 三级分类id
     */
    private Long platformCategoryId;
}
