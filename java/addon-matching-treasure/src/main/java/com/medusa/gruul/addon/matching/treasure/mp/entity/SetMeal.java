package com.medusa.gruul.addon.matching.treasure.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealType;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 套餐表
 *
 * @author WuDi
 * @since 2023-02-27
 */
@Getter
@Setter
@TableName(value = "t_set_meal", autoResultMap = true)
@ToString
@Accessors(chain = true)
public class SetMeal extends BaseEntity {


    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 套餐名称
     */
    private String setMealName;

    /**
     * 套餐描述
     */
    private String setMealDescription;

    /**
     * 套餐主图
     */
    private String setMealMainPicture;

    /**
     * 套餐类型 [0:自选商品套餐 1:固定组合套餐]
     */
    private SetMealType setMealType;

    /**
     * 活动状态 [0:未开始 1:进行中 2:已结束 3:违规下架  4:下架]
     */
    private SetMealStatus setMealStatus;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 参与人数
     */
    private Integer peopleNum;

    /**
     * 应收金额
     */
    private Long amountReceivable;

    /**
     * 套餐配送类型
     */
    private DistributionMode distributionMode;

    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private StackableDiscount stackable;
    /**
     * 违规说明
     */
    private String violationExplain;

}
