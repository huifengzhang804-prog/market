package com.medusa.gruul.addon.seckill.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.seckill.model.enums.SeckillStatus;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 秒杀活动
 *
 * @author 张治保
 * @since 2024/5/28
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_seckill_activity", autoResultMap = true)
public class SeckillActivity extends BaseEntity {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动状态 只标识是否下架 具体是否进行中根据 活动时间判断
     */
    private SeckillStatus status;

    /**
     * 秒杀活动名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 活动日期
     */
    @TableField(value = "`date`")
    private LocalDate date;

    /**
     * 活动场次
     */
    private Integer round;

    /**
     * 叠加优惠
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private StackableDiscount stackable;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 额外信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Extra extra;

    /**
     * 额外信息
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Extra {

        /**
         * 违规原因
         */
        private String violation;

    }
}
