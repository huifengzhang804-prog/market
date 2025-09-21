package com.medusa.gruul.addon.full.reduction.model.vo;

import com.medusa.gruul.addon.full.reduction.model.enums.FullQueryStatus;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionRuleType;
import com.medusa.gruul.addon.full.reduction.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/6/17
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class FullReductionVO {

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 活动id
     */
    private Long id;

    /**
     * 活动状态
     */
    private FullQueryStatus status;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动商品类型
     */
    private ProductType productType;

    /**
     * 商品数
     */
    private Integer product;

    /**
     * 参与人数
     */
//    private Long user;

    /**
     * 支付订单数
     */
    private Long order;

    /**
     * 第一个满减规则类型
     */
    private FullReductionRuleType firstRuleType;

    /**
     * 第一个满减规则描述
     */
    private String firstRuleDesc;

    /**
     * 下架原因
     */
    private String violation;

}
