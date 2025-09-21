package com.medusa.gruul.shop.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 店铺访客
 *
 * @author xiaoq
 * @Description ShopVisitor.java
 * @date 2022-10-25 16:57
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_shop_visitor")
public class ShopVisitor extends BaseEntity {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 时间 年-月-日
     */
    private LocalDate date;

    /**
     * 当日累计点击量
     */
    private Long uv;


    /**
     * 店铺id
     */
    private  Long shopId;


}
