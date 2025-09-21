package com.medusa.gruul.addon.seckill.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 秒杀订单
 *
 * @author 张治保
 * @since 2024/5/29
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_seckill_order", autoResultMap = true)
public class SeckillOrder extends BaseEntity {

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderNo;


}
