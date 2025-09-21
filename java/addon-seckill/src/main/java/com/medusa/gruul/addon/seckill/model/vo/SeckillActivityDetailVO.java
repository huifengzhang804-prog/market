package com.medusa.gruul.addon.seckill.model.vo;

import com.medusa.gruul.common.model.base.StackableDiscount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/5/30
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SeckillActivityDetailVO {

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动日期
     */
    private LocalDate date;

    /**
     * 活动开始时间
     */
    private LocalTime startTime;

    /**
     * 活动结束时间
     */
    private LocalTime endTime;

    /**
     * 活动支付超时时间 单位分钟
     */
    private Integer payTimeout;

    /**
     * 叠加优惠
     */
    private StackableDiscount stackable;

    /**
     * 商品
     */
    private List<SeckillProductVO> products;


}
