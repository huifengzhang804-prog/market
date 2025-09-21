package com.medusa.gruul.addon.full.reduction.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.full.reduction.model.bo.FullReductionProduct;
import com.medusa.gruul.addon.full.reduction.model.bo.FullReductionRule;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionStatus;
import com.medusa.gruul.addon.full.reduction.model.enums.ProductType;
import com.medusa.gruul.common.mp.handler.type.MapToKeyValuesTypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 满减活动表
 *
 * @author WuDi
 * @since 2023-02-07
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_full_reduction", autoResultMap = true)
public class FullReduction extends BaseEntity {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 满减活动名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 活动状态
     */
    @TableField("`status`")
    private FullReductionStatus status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 满减规则
     */
    @TableField(value = "rules", typeHandler = Fastjson2TypeHandler.class)
    private List<FullReductionRule> rules;

    /**
     * 0:全部商品 1:制定商品 2:指定商品不参与
     */
    private ProductType productType;

    /**
     * 可以使用的商品id 列表
     * 当 productType 是全部商品时可以为空
     */
    @TableField(typeHandler = MapToKeyValuesTypeHandler.class)
    private Map<Long, FullReductionProduct> products;


    /**
     * 额外信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Extra extra;

    /**
     * 满减额外信息
     */
    @Getter
    @Setter
    @ToString
    public static class Extra {

        /**
         * xxl job id
         */
        private JobId jobId;

        /**
         * 违规下架原因
         */
        private String violation;
    }

    /**
     * 满减 xxl job 信息
     */
    @Getter
    @Setter
    @ToString
    public static class JobId {

        /**
         * 活动开始 任务id
         */
        private Integer startId;

        /**
         * 活动结束 任务id
         */
        private Integer stopId;
    }


}
